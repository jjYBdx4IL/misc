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

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.github.jjYBdx4IL.jmon.dto.HostState;
import com.github.jjYBdx4IL.jmon.dto.ServiceState;
import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request.Content;
import org.eclipse.jetty.client.dynamic.HttpClientTransportDynamic;
import org.eclipse.jetty.client.util.StringRequestContent;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.io.ClientConnector;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLHandshakeException;

public class RunServerTest {

    private static final Logger LOG = LoggerFactory.getLogger(RunServerTest.class);

    public static final String url = "https://localhost:8443/";
    public static final String CLIENT_KEY_ALIAS = "client";
    public static final String CLIENT_KEY_ALIAS_UNKNOWN = "unknownclient";
    public static final String SERVER_KEY_ALIAS = "server";

    public static final Path tgtDir = Paths.get(System.getProperty("project.build.directory", "./target"));

    private static final Gson gson = Utils.createGson();

    Path cfgDir;
    Path serverKs;
    Path serverTs;
    Path clientKs;
    Path clientTs;
    Path sslTestServerKs;
    Path sslTestServerTs;
    Path sslTestServerExpiredKs;
    Path sslTestServerExpiredTs;
    static int testCounter = 0;

    public Thread t = null;
    public Main main = null;
    public RunServer m = null;
    public Throwable caught = null;
    final BlockingQueue<String> mails = new LinkedBlockingQueue<>();

    public void keytool(Path store, String alias, String cmdline) throws InterruptedException, IOException {
        List<String> args = new ArrayList<>();
        args.add("keytool");
        args.add("-keystore");
        args.add(store.toString());
        args.add("-storepass");
        args.add("password");
        args.add("-alias");
        args.add(alias);
        args.add("-noprompt");
        args.addAll(Arrays.asList(cmdline.split("\\s+")));
        assertEquals(0, new ProcessBuilder(args).inheritIO().directory(tgtDir.toFile()).start().waitFor());
    }

    public void setupKeystores() throws Exception {
        if (Files.exists(serverKs)) {
            return;
        }

        // generate (jmon) server key and server keystore
        keytool(serverKs, SERVER_KEY_ALIAS, "-genkey -dname CN=localhost -keyalg rsa");
        keytool(serverKs, SERVER_KEY_ALIAS, "-export -file cert");
        keytool(serverTs, "server-cert", "-import -file cert");
        keytool(clientTs, "server-cert", "-import -file cert");

        // generate client key and client keystore for passive check submissions
        keytool(clientKs, CLIENT_KEY_ALIAS, "-genkey -dname CN=client -keyalg rsa");
        keytool(clientKs, CLIENT_KEY_ALIAS, "-export -file cert");
        keytool(serverTs, "client-cert", "-import -file cert");

        // client unknown to jmon:
        keytool(clientKs, CLIENT_KEY_ALIAS_UNKNOWN, "-genkey -dname CN=unknownclient -keyalg rsa");

        // "remote" ssl test server to run active checks against:
        keytool(sslTestServerKs, "testserver", "-genkey -dname CN=localhost -keyalg rsa");
        keytool(sslTestServerKs, "testserver", "-export -file cert");
        keytool(serverTs, "testserver-cert", "-import -file cert");

        // "remote" ssl test server to run active checks against:
        keytool(sslTestServerExpiredKs, "testserver", "-genkey -dname CN=localhost -keyalg rsa -validity 1");
        keytool(sslTestServerExpiredKs, "testserver", "-export -file cert");
        keytool(serverTs, "testserverExpired-cert", "-import -file cert");
    }

    @Before
    public void before() throws Exception {
        cfgDir = tgtDir.resolve("test" + testCounter++);
        serverKs = tgtDir.resolve("keystore");
        serverTs = tgtDir.resolve("truststore");
        clientKs = tgtDir.resolve("keystore_client");
        clientTs = tgtDir.resolve("truststore_client");
        sslTestServerKs = tgtDir.resolve("keystore_testserver");
        ;
        sslTestServerTs = null;
        sslTestServerExpiredKs = tgtDir.resolve("keystore_testserver_expired");
        ;
        sslTestServerExpiredTs = null;

        setupKeystores();

        FileUtils.deleteDirectory(cfgDir.toFile());
        Files.createDirectories(cfgDir);
        for (Path p : new Path[] { serverKs, serverTs }) {
            Files.copy(p, cfgDir.resolve(p.toFile().getName()));
        }
    }

    public void start(String[] args) throws Exception {
        TestRequestHandler.reset();
        SslTestServer.start(sslTestServerKs, sslTestServerTs);

        if (args == null) {
            args = new String[] { "--runServer", "--cfgdir", cfgDir.toString() };
        }
        // force saves on every service state update
        Config.hostSaveIvalMillis = 0;
        Config.port = 8443;
        Config.reporter = new IProblemReporter() {

            @Override
            public void send(String subject, String body) throws Exception {
                mails.add(subject + "\n\n" + body);
            }
        };
        mails.clear();

        final String[] args2 = args;

        main = new Main();
        t = new Thread("main-forked") {
            public void run() {
                try {
                    main.run(args2);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
        };
        caught = null;
        t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                caught = e;
            }
        });
        t.start();
        main.startup.await();
        if (main.m != null && main.m instanceof RunServer) {
            m = (RunServer) main.m;
        } else {
            m = null;
        }
        LOG.info("m = {}", m);
        Thread.sleep(1000);
    }

    @After
    public void after() throws Exception {

        if (m != null) {
            int dataSize = 1024 * 1024;

            System.out.println("Used Memory   : "
                + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / dataSize + " MB");
            System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory() / dataSize + " MB");
            System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory() / dataSize + " MB");
            System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory() / dataSize + " MB");

            System.out.println("** After GC: **");
            Runtime.getRuntime().gc();

            System.out.println("Used Memory   : "
                + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / dataSize + " MB");
            System.out.println("Free Memory   : " + Runtime.getRuntime().freeMemory() / dataSize + " MB");
            System.out.println("Total Memory  : " + Runtime.getRuntime().totalMemory() / dataSize + " MB");
            System.out.println("Max Memory    : " + Runtime.getRuntime().maxMemory() / dataSize + " MB");

            m.server.stop();
            main.shutdown.await();
            t.join();
            t = null;
            m = null;
            main = null;
        }

        SslTestServer.server.stop();
    }

    @Test
    public void testNoHostDefinitionsError() throws Exception {
        start(null);
        t.join();
        assertTrue(caught.getMessage().contains("no host definitions found"));
    }

    @Test
    public void testPassiveCheckResultSubmission() throws Exception {
        writePassiveHostDef();
        start(new String[] { "--runServer", "--cfgdir", cfgDir.toString(), "--ignoreNoActiveChecks" });

        // there are no passive check submissions yet, so we immediately get an
        // error for the service:
        HostState hs = parseState("client");
        assertEquals(1, hs.services.size());
        assertEquals(2, hs.services.values().iterator().next().status);

        assertHttpStatus(200, validPassiveCheckSubmitJson);
        hs = parseState("client");
        assertEquals(1, hs.services.size());
        assertEquals(0, hs.services.values().iterator().next().status);
    }

    @Test
    public void testInvalidPassiveCheckResultSubmission() throws Exception {
        writePassiveHostDef();
        start(new String[] { "--runServer", "--cfgdir", cfgDir.toString(), "--ignoreNoActiveChecks" });

        parseState("client");

        assertHttpStatus(500, invalidPassiveCheckSubmitJson1);

        try {
            parseState("client");
            fail();
        } catch (NoSuchFileException ex) {
        }

        assertHttpStatus(500, invalidPassiveCheckSubmitJson2);

        try {
            parseState("client");
            fail();
        } catch (NoSuchFileException ex) {
        }
    }

    @Test
    public void testPassiveCheckResultSubmissionSslHandshakeException() throws Exception {
        writePassiveHostDef();
        start(new String[] { "--runServer", "--cfgdir", cfgDir.toString(), "--ignoreNoActiveChecks" });

        try {
            fetchResponse(validPassiveCheckSubmitJson, CLIENT_KEY_ALIAS_UNKNOWN);
            fail();
        } catch (ExecutionException ex) {
            assertTrue(ex.getCause() instanceof SSLHandshakeException);
        }
    }

    @Test
    public void testHelp() throws Exception {
        start(new String[] { "--help" });
    }

    @Test
    public void testHelpConfig() throws Exception {
        start(new String[] { "--helpConfig" });
    }

    @Test
    public void testReportingNoMessage() throws Exception {
        writeActiveHostDef("urlcheck", f("{truststore:true,port:%d}", SslTestServer.port));
        start(null);

        HostState hs = parseState("localhost");
        assertEquals(1, hs.services.size());
        ServiceState s = hs.services.values().iterator().next();
        assertEquals(0, s.status);

        assertTrue(mails.isEmpty());
    }

    @Test
    public void testReportingErrorMessage() throws Exception {
        writeActiveHostDef("urlcheck", f("{content=\"Test-Content\",truststore:true,port:%d}", SslTestServer.port),
            ",\"tries\":1,\"retryIval\":\"2s\"");
        start(null);

        HostState hs = parseState("localhost");
        assertEquals(1, hs.services.size());
        ServiceState s = hs.services.values().iterator().next();
        assertEquals(2, s.status);

        String report = mails.poll(10, TimeUnit.SECONDS);

        assertTrue(report, report.contains("localhost - testservice1 - ERROR - "));
    }

    @Test
    public void testReportingRecoveryMessage() throws Exception {
        writeActiveHostDef("urlcheck", f("{content=\"Test-Content\",truststore=true,port:%d}", SslTestServer.port),
            ",\"tries\":1,\"checkIval\":\"1s\"");
        start(null);

        HostState hs = parseState("localhost");
        assertEquals(1, hs.services.size());
        ServiceState s = hs.services.values().iterator().next();
        assertEquals(2, s.status);

        String report = mails.poll(10, TimeUnit.SECONDS);
        assertTrue(report, report.contains("localhost - testservice1 - ERROR - "));

        TestRequestHandler.TEST_CONTENT = "Test-Content";

        report = mails.poll(10, TimeUnit.SECONDS);
        assertTrue(report, report.contains("All clear!"));
    }

    @Test
    public void testUrlCheck() throws Exception {
        writeActiveHostDef("urlcheck", f("{truststore:true,port:%d}", SslTestServer.port));
        start(null);

        HostState hs = parseState("localhost");
        assertEquals(1, hs.services.size());
        ServiceState s = hs.services.values().iterator().next();
        assertEquals(0, s.status);
    }

    @Test
    public void testCertExpiryGoogleCom() throws Exception {
        writeActiveHostDef("www.google.com", "certexpirycheck", null, "");
        start(null);

        HostState hs = parseState("www.google.com");
        assertEquals(1, hs.services.size());
        ServiceState s = hs.services.values().iterator().next();
        assertEquals(0, s.status);
    }

    @Test
    public void testCertExpiryCheck() throws Exception {
        writeActiveHostDef("certexpirycheck", f("{truststore:true,port:%d}", SslTestServer.port));
        start(null);

        HostState hs = parseState("localhost");
        assertEquals(1, hs.services.size());
        ServiceState s = hs.services.values().iterator().next();
        assertEquals(0, s.status);
    }

    @Test
    public void testCertExpiryCheckError() throws Exception {
        sslTestServerKs = sslTestServerExpiredKs;
        sslTestServerTs = sslTestServerExpiredTs;
        writeActiveHostDef("certexpirycheck", f("{truststore:true,port:%d}", SslTestServer.port));
        start(null);

        HostState hs = parseState("localhost");
        assertEquals(1, hs.services.size());
        ServiceState s = hs.services.values().iterator().next();
        assertEquals(2, s.status);
    }

    @Test
    public void testSslTestServer() throws Exception {
        SslTestServer.start(sslTestServerKs, sslTestServerTs);

        HttpClient c = getJmonClient();
        assertEquals(200, c.GET(f("https://localhost:%d/", SslTestServer.port)).getStatus());
    }

    public void writeActiveHostDef(String checkName, String conf) throws IOException {
        writeActiveHostDef(checkName, conf, "");
    }

    public void writeActiveHostDef(String checkName, String conf, String append) throws IOException {
        writeActiveHostDef("localhost", checkName, conf, append);
    }

    public void writeActiveHostDef(String hostname, String checkName, String conf, String append) throws IOException {
        Files.writeString(cfgDir.resolve(f("%s.def", hostname)), f("    {\n"
            + "        \"services\": {\n"
            + "            \"testservice1\": {\n"
            + "                \"check\": \"%s\",\n"
            + "                \"conf\": %s\n%s"
            + "            }\n"
            + "        }\n"
            + "    }", checkName, gson.toJson(conf), append));
    }

    public static final String invalidPassiveCheckSubmitJson1 = "{\"state\":\"0\",\"msg\":\"\",\"service\":\"\"}";
    public static final String invalidPassiveCheckSubmitJson2 = "{\"state\":\"a\",\"msg\":\"\",\"service\":\"\"}";
    public static final String validPassiveCheckSubmitJson = "{\"state\":0,\"msg\":\"testmsg\",\"service\":\"passivetestservice1\"}";

    public void writePassiveHostDef() throws IOException {
        Files.writeString(cfgDir.resolve("client.def"), f("    {\n"
            + "        \"services\": {\n"
            + "            \"passivetestservice1\": {\n"
            + "                \"passive\": true\n"
            + "            }\n"
            + "        }\n"
            + "    }"));
    }

    private void assertHttpStatus(int expectedHttpStatus, String stateMsg) throws Exception {
        assertHttpStatus(expectedHttpStatus, stateMsg, CLIENT_KEY_ALIAS);
    }

    private void assertHttpStatus(int expectedHttpStatus, String stateMsg, String keyAlias) throws Exception {
        ContentResponse response = fetchResponse(stateMsg, keyAlias);
        assertEquals(expectedHttpStatus, response.getStatus());

    }

    private ContentResponse fetchResponse(String stateMsg, String keyAlias) throws Exception {
        HttpClient c = getClient(keyAlias);
        Content content = new StringRequestContent(RequestHandler.JSON_TYPE, stateMsg, UTF_8);
        return c.newRequest(url).method(HttpMethod.POST).body(content).send();
    }

    private HostState parseState(String hostname) throws IOException {
        Path stateFile = cfgDir.resolve(hostname + ".state");
        String s = Files.readString(stateFile);
        Files.delete(stateFile);
        try {
            return gson.fromJson(s, HostState.class);
        } catch (Exception ex) {
            LOG.error("json: {}", s);
            throw ex;
        }
    }

    public HttpClient getClient() throws Exception {
        return getClient("client");
    }

    public HttpClient getClient(String keyAlias) throws Exception {
        SslContextFactory.Client ssl = new SslContextFactory.Client();

        ssl.setValidatePeerCerts(true);
        ssl.setTrustStorePath(clientTs.toString());
        ssl.setTrustStorePassword("password");

        ssl.setCertAlias(keyAlias);
        ssl.setKeyStorePath(clientKs.toString());
        ssl.setKeyStorePassword("password");

        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(ssl);

        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));
        httpClient.start();
        return httpClient;
    }

    // client that identifies as if it were JMon itself
    public HttpClient getJmonClient() throws Exception {
        SslContextFactory.Client ssl = new SslContextFactory.Client();

        ssl.setValidatePeerCerts(true);
        ssl.setTrustStorePath(serverTs.toString());
        ssl.setTrustStorePassword("password");

        ssl.setCertAlias(SERVER_KEY_ALIAS);
        ssl.setKeyStorePath(serverKs.toString());
        ssl.setKeyStorePassword("password");

        ClientConnector clientConnector = new ClientConnector();
        clientConnector.setSslContextFactory(ssl);

        HttpClient httpClient = new HttpClient(new HttpClientTransportDynamic(clientConnector));
        httpClient.start();
        return httpClient;
    }
}
