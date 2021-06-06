/*
 * Copyright © 2017 jjYBdx4IL (https://github.com/jjYBdx4IL)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jjYBdx4IL.jmon;

import static com.google.common.base.Preconditions.checkArgument;

import com.github.jjYBdx4IL.jmon.checks.ICheck;
import com.github.jjYBdx4IL.utils.time.TimeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.security.KeyStore;
import java.time.Duration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.internet.ParseException;

public class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    public static Gson createGson() {
        GsonBuilder b = new GsonBuilder();
        b.disableHtmlEscaping();
        if (LOG.isDebugEnabled()) {
            b.setPrettyPrinting();
        }
        b.registerTypeAdapter(Duration.class, new DurationDeserializer());
        b.registerTypeAdapter(Duration.class, new DurationSerializer());
        return b.create();
    }

    public static class DurationSerializer implements JsonSerializer<Duration> {
        public JsonElement serialize(Duration src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(TimeUtils.millisToDuration(src.getSeconds() * 1000L));
        }
    }

    public static class DurationDeserializer implements JsonDeserializer<Duration> {
        public Duration deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
            return Duration.ofSeconds(TimeUtils.durationToMillis(json.getAsJsonPrimitive().getAsString()) / 1000L);
        }
    }

    public static void sendMessage(String subject, String body)
        throws ParseException, MessagingException {
        Session session = createSmtpSession();
        MimeMessage msg = new MimeMessage(session);
        msg.setRecipient(Message.RecipientType.TO, new InternetAddress(System.getProperty("user.name") + "@localhost"));
        msg.setSubject(subject);
        Multipart mp = new MimeMultipart();
        BodyPart bp = new MimeBodyPart();
        bp.setText(body);
        mp.addBodyPart(bp);
        msg.setContent(mp);
        try (Transport t = session.getTransport()) {
            t.connect();
            t.sendMessage(msg, msg.getAllRecipients());
        } catch (MessagingException ex) {
            LOG.error("failed to send {} - {}", subject, body);
            LOG.error("", ex);
        }
    }

    protected static Session createSmtpSession() {
        Properties props = new Properties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.host", "localhost");

        Session session = Session.getDefaultInstance(props);
        if (LOG.isDebugEnabled()) {
            LOG.debug("enabling smtp debugging");
            session.setDebug(true);
        }
        return session;
    }

    public static Set<String> scanChecks() {
        LOG.debug("scanning check implementations");
        Set<String> klazznames = new HashSet<>();
        try (ScanResult scanResult = new ClassGraph()
            .enableClassInfo()
            .scan()) {
            scanResult.getClassesImplementing(ICheck.class.getName())
                .forEach(ci -> {
                    LOG.trace("{}", ci);
                    klazznames.add(ci.getName());
                });
        }
        LOG.debug("check implementations found: {}", klazznames.size());
        checkArgument(!klazznames.isEmpty());
        return klazznames;
    }

    public static SSLSocketFactory createSSLSocketFactory(Path trustStore) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream is = new FileInputStream(trustStore.toFile())) {
            keyStore.load(is, "password".toCharArray());
        }

        TrustManagerFactory trustManagerFactory = TrustManagerFactory
            .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

        return sslContext.getSocketFactory();
    }
    
    public static HttpClient getClient(String keyAlias) throws Exception {
        SslContextFactory.Client ssl = new SslContextFactory.Client();

        ssl.setValidatePeerCerts(true);
        ssl.setTrustStorePath(Config.ts.toString());
        ssl.setTrustStorePassword("password");

        ssl.setCertAlias(keyAlias);
        ssl.setKeyStorePath(Config.ks.toString());
        ssl.setKeyStorePassword("password");

        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(ssl);

        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));
        httpClient.start();
        return httpClient;
    }
}
