/*
 * Copyright © 2021 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.wildfly;

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;
import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.jjYBdx4IL.utils.io.IoUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;

import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * To enable wire-logging, set log level of this class to TRACE.
 */
public class WildFlyClient {

    private static final Logger LOG = LoggerFactory.getLogger(WildFlyClient.class);
    public static final long TIMEOUT_SECS = 300;

    static {
        if (LOG.isTraceEnabled()) {
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "DEBUG");
            System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "ERROR");
            System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
            System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "false");
        }
    }

    private boolean prettyPrint = false;

    final URL url;
    final URL uploadUrl;
    final HttpHost target;
    final CredentialsProvider credsProvider = new BasicCredentialsProvider();
    final CookieStore cookieStore = new BasicCookieStore();
    final CloseableHttpClient httpclient;
    Gson gson;

    public String reply;
    public JsonObject r;
    private boolean reloadRequired = false;

    /**
     * Create a new wildfly management client.
     */
    public WildFlyClient(String mgmtUser, String mgmtPass, int mgmtPort) throws Exception {
        gson = createGson();
        url = new URL(f("http://localhost:%d/management", mgmtPort));
        uploadUrl = new URL(f("http://localhost:%d/management/add-content", mgmtPort));
        target = new HttpHost(url.getHost(), url.getPort(), url.getProtocol());
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(mgmtUser, mgmtPass);
        credsProvider.setCredentials(new AuthScope(target), credentials);
        httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore)
            .setDefaultCredentialsProvider(credsProvider).build();
    }

    protected Gson createGson() {
        GsonBuilder b = new GsonBuilder();
        b.disableHtmlEscaping();
        if (prettyPrint) {
            b.setPrettyPrinting();
        }
        return b.create();
    }

    /**
     * Enable pretty printing of requests and responses.
     */
    public WildFlyClient setPrettyPrint(boolean pp) {
        this.prettyPrint = pp;
        gson = createGson();
        return this;
    }

    protected String getProp(String name, String defaultValue) {
        String value = System.getProperty(name);
        if (value == null) {
            LOG.warn("system property {} not found, using default value {}", name, defaultValue);
            value = defaultValue;
        }
        return value;
    }

    private JsonArray addrStr2JsonArray(String address) {
        JsonArray ja = new JsonArray();
        for (String part : address.split("/")) {
            if (part.isEmpty()) {
                continue;
            }
            String[] arr = part.split("=", 2);
            ja.add(arr[0]);
            ja.add(arr[1]);
        }
        return ja;
    }

    private void addCmdArgs(JsonObject jo, String a) {
        int i = 0;
        while (i < a.length()) {
            char c = a.charAt(i);
            if (c == ',') {
                i++;
            }
            int j = a.indexOf("=", i);
            if (j == -1 || j == i) {
                throw new IllegalArgumentException(a);
            }
            if (j == a.length() - 1) {
                jo.addProperty(a.substring(i, j), "");
                return;
            }
            c = a.charAt(j + 1);
            if (c == '[') {
                int k = a.indexOf("]", j + 2);
                if (k == -1) {
                    throw new IllegalArgumentException(a);
                }
                jo.add(a.substring(i, j), gson.fromJson(a.substring(j + 1, k + 1), JsonArray.class));
                i = k + 1;
            } else if (c == '{') {
                int k = a.indexOf("}", j + 2);
                if (k == -1) {
                    throw new IllegalArgumentException(a);
                }
                jo.add(a.substring(i, j), gson.fromJson(a.substring(j + 1, k + 1), JsonObject.class));
                i = k + 1;
            } else if (c == '"') {
                int k = a.indexOf('"', j + 2);
                if (k == -1) {
                    throw new IllegalArgumentException(a);
                }
                jo.add(a.substring(i, j), gson.fromJson(a.substring(j + 1, k + 1), JsonPrimitive.class));
                i = k + 1;
            } else {
                int k = a.indexOf(",", j + 1);
                if (k == -1) {
                    k = a.length();
                }
                jo.addProperty(a.substring(i, j), a.substring(j + 1, k));
                i = k;
            }
        }
    }

    private static final Pattern CMD_PAT = Pattern.compile("^([^:]*):([^:(]+)(?:\\((.*)\\))?$");

    /**
     * Execute wildfly management command.
     * 
     * @param cmd
     *            a string in CLI style, ie.
     *            <code>/deployment=ROOT.war:remove</code>
     */
    public boolean exec(String cmd) throws Exception {
        Matcher m = CMD_PAT.matcher(cmd);
        if (!m.find()) {
            throw new IllegalArgumentException("invalid command: " + cmd);
        }
        JsonArray address = addrStr2JsonArray(m.group(1));
        String operation = m.group(2);

        JsonObject jo = new JsonObject();
        jo.addProperty("operation", operation);
        jo.add("address", address);
        if (m.group(3) != null) {
            addCmdArgs(jo, m.group(3));
        }
        jo.addProperty("json.pretty", prettyPrint ? 1 : 0);
        String jsonCmd = gson.toJson(jo);
        LOG.info("posting: {}", jsonCmd);

        HttpPost post = new HttpPost(url.toExternalForm());
        post.setEntity(new StringEntity(jsonCmd, ContentType.APPLICATION_JSON));

        try (CloseableHttpResponse response = httpclient.execute(target, post)) {
            // {"outcome" : "success", "response-headers" :
            // {"operation-requires-reload" :
            // true, "process-state" : "reload-required"}}
            reply = EntityUtils.toString(response.getEntity(), UTF_8);
            try {
                String s = JsonPath.parse(reply).read("$['response-headers']['process-state']");
                if (s != null && s.equalsIgnoreCase("reload-required")) {
                    this.reloadRequired = true;
                }
            } catch (PathNotFoundException ex) {
                // ignored
            }
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                LOG.info(reply);
            } else {
                LOG.warn(reply);
            }
            r = gson.fromJson(reply, JsonObject.class);
            return status == 200;
        }
    }

    /**
     * Convenience wrapper method for {@link #exec(String)}.
     * 
     * @throws RuntimeException
     *             if result is not positive
     */
    public void assertPostCmd(String cmd) throws Exception {
        if (!exec(cmd)) {
            throw new RuntimeException("command failed: " + cmd);
        }
    }

    /**
     * Upload file.
     * 
     * @param file
     *            the file to upload
     * @return the base64 encoded SHA-1 of the uploaded contents as reported by
     *         the server
     */
    public String upload(File file) throws Exception {
        LOG.info("uploading: " + file);

        HttpPost post = new HttpPost(uploadUrl.toExternalForm());

        MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();
        entitybuilder.addBinaryBody("test.war", file);
        HttpEntity mutiPartHttpEntity = entitybuilder.build();
        post.setEntity(mutiPartHttpEntity);

        try (CloseableHttpResponse response = httpclient.execute(target, post)) {
            // {"outcome":"success","result":{"BYTES_VALUE":"FA9RsImC4xgvXNqpkDKQU72SF+8="}}
            reply = EntityUtils.toString(response.getEntity(), UTF_8);
            r = gson.fromJson(reply, JsonObject.class);
            int status = response.getStatusLine().getStatusCode();
            if (status == 200) {
                LOG.info(reply);
            } else {
                throw new IOException("upload failed: " + reply);
            }
            return JsonPath.parse(reply).read("$['result']['BYTES_VALUE']");
        }
    }

    public String upload(String fileLoc) throws Exception {
        return upload(new File(fileLoc));
    }

    /**
     * Upload and deploy (add+enable).
     */
    public void uploadDeploy(String deploymentName, String fileLoc) throws Exception {
        String uploadId = upload(fileLoc);
        assertPostCmd(f("/deployment=%s:add(enabled=true,content=[{\"hash\":{\"BYTES_VALUE\":\"%s\"}}])",
            deploymentName, uploadId));
    }

    /**
     * Upload and deploy (add+enable). Skips upload and deployment if remote
     * checksum matches.
     */
    public void uploadDeployIfChanged(String deploymentName, String fileLoc) throws Exception {
        final String localSha1 = IoUtils.getDigest(fileLoc, "SHA-1").toLowerCase(Locale.ROOT);
        boolean needsRemoval = false;
        if (exec(f("/deployment=%s:read-resource", deploymentName))) {
            needsRemoval = true;
            String deployedHash = null;
            try {
                deployedHash = JsonPath.parse(reply).read("$['result']['content'][0]['hash']['BYTES_VALUE']");
            } catch (PathNotFoundException ex) {
                // ignored.
            }
            if (deployedHash != null) {
                String deployedSha1 = Hex.encodeHexString(Base64.getDecoder().decode(deployedHash), true);
                if (localSha1.equals(deployedSha1)) {
                    LOG.info("deployment content hash unchanged, skipping");
                    return;
                }
            }
        }

        String uploadHash = upload(fileLoc);
        String uploadSha1 = Hex.encodeHexString(Base64.getDecoder().decode(uploadHash), true);
        if (!localSha1.equals(uploadSha1)) {
            throw new IOException("checksum mismatch for upload: upload=" + uploadSha1 + ", local=" + localSha1);
        }

        if (needsRemoval) {
            assertPostCmd(f("/deployment=%s:remove", deploymentName));
        }
        assertPostCmd(f("/deployment=%s:add(enabled=true,content=[{\"hash\":{\"BYTES_VALUE\":\"%s\"}}])",
            deploymentName, uploadHash));
    }

    /**
     * Check whether the given resource exists.
     */
    public boolean resourceExists(String resLoc) throws Exception {
        return exec(f("%s:read-resource", resLoc));
    }
    
    /**
     * Add the resource if it does not exist yet.
     */
    public boolean addResourceIfNotExists(String resLoc, String spec) throws Exception {
        if (resourceExists(resLoc)) {
            return false;
        }
        if (spec != null) {
            assertPostCmd(f("%s:add(%s)", resLoc, spec));
        } else {
            assertPostCmd(f("%s:add", resLoc));
        }
        return true;
    }

    /**
     * Add VHost if it doesn't exist.
     */
    public boolean addVhostIfNotExists(String name) throws Exception {
        String hostres = f("/subsystem=undertow/server=default-server/host=%s", name);
        return addResourceIfNotExists(hostres, null);
    }
    
    /**
     * Set vhost alias(es).
     */
    public void setVhostAliases(String vhost, String[] aliases) throws Exception {
        if (vhost == null) {
            vhost = "default-host";
        }
        String hostres = f("/subsystem=undertow/server=default-server/host=%s", vhost);
        writeAttr(hostres, "alias", aliases);
    }

    /**
     * Write attribute.
     */
    public void writeAttr(String resLoc, String attrName, String[] attrValue) throws Exception {
        assertPostCmd(f("%s:write-attribute(name=%s,value=%s)", resLoc, attrName, gson.toJson(attrValue)));
    }

    /**
     * Write attribute.
     */
    public void writeAttr(String resLoc, String attrName, String attrValue) throws Exception {
        assertPostCmd(f("%s:write-attribute(name=%s,value=%s)", resLoc, attrName, gson.toJson(attrValue)));
    }
    
    /**
     * Undeploy and remove.
     * 
     * @return false if removal failed or deployment does not exist.
     */
    public boolean undeployAndRemove(String deployment) throws Exception {
        exec("/deployment=" + deployment + ":undeploy");
        return exec("/deployment=" + deployment + ":remove");
    }

    /**
     * Add and deploy path. If path is a directory, assume an exploded deployment (archive=false).
     *
     * @param path
     *            the path (directory) to deploy
     * @throws Exception
     *             if anything fails, ie. deployment already exists etc.
     */
    public void deploy(String deployment, Path path) throws Exception {
        if (!Files.exists(path)) {
            throw new FileNotFoundException(path.toString());
        }
        assertPostCmd(f("/deployment=%s:add(enabled=true,content={\"archive\":%s,\"path\":%s})",
            deployment, Files.isDirectory(path) ? "false" : "true", gson.toJson(path.toString())));
    }

    /**
     * Add and deploy.
     */
    public void addAndDeploy(String deployment) throws Exception {
        assertPostCmd("/deployment=" + deployment + ":add");
    }

    /**
     * Redeploy.
     */
    public void redeploy(String deployment) throws Exception {
        exec("/deployment=" + deployment + ":redeploy");
    }

    /**
     * Set/update wildfly system property.
     */
    public void setSysProp(String name, String value) throws Exception {
        if (exec(f("/system-property=%s:read-resource", name))) {
            updateSysProp(name, value);
        } else {
            addSysProp(name, value);
        }
    }

    /**
     * Update wildfly system property.
     */
    public void updateSysProp(String name, String value) throws Exception {
        assertPostCmd(f("/system-property=%s:write-attribute(name=value,value=%s)", name, value));
    }

    /**
     * Add system property to wildfly server.
     */
    public void addSysProp(String name, String value) throws Exception {
        assertPostCmd("/system-property=" + name + ":add(value=" + value + ")");
    }

    /**
     * Remove system property from wildfly server.
     */
    public void removeSysProp(String name) throws Exception {
        assertPostCmd("/system-property=" + name + ":remove()");
    }

    /**
     * Trigger server reload.
     */
    public void reload() throws Exception {
        assertPostCmd(":reload(blocking=false)");
        reloadRequired = false;
    }

    /**
     * Initiate server reload and wait for it to finish.
     */
    public void reloadBlocking() throws Exception {
        assertPostCmd(":reload(blocking=true)");
        waitFor();
        reloadRequired = false;
    }

    /**
     * Trigger server reload if any of the previous responses indicated the
     * necessity to do so.
     */
    public void reloadIfRequired() throws Exception {
        if (reloadRequired) {
            reloadBlocking();
        }
    }

    /**
     * Initiate server shutdown.
     */
    public void shutdown() throws Exception {
        assertPostCmd(":shutdown(restart=false)");
        reloadRequired = false;
    }

    /**
     * Initiate server restart.
     */
    public void restart() throws Exception {
        assertPostCmd(":shutdown(restart=true)");
        reloadRequired = false;
    }

    // category: (any parent) package name, ie. "ms" for "ms.**"
    // ALL, FINEST, FINER, FINE, TRACE, DEBUG, INFO, WARN, ERROR, FATAL etc.
    public static final String LOGCAT_HIBERNATE = "org.hibernate";
    public static final String LOGCAT_HIBERNATE_SQL = "org.hibernate.SQL";
    public static final String LOGCAT_HIBERNATE_QUERY = "org.hibernate.query";
    // track what's going on with container managed EntityManager
    public static final String LOGCAT_HIBERNATE_AS_JPA = "org.jboss.as.jpa";

    public static final String LOGPRIO_ERROR = "ERROR";
    public static final String LOGPRIO_WARN = "WARN";
    public static final String LOGPRIO_INFO = "INFO";
    public static final String LOGPRIO_DEBUG = "DEBUG";
    public static final String LOGPRIO_TRACE = "TRACE";
    public static final String LOGPRIO_ALL = "ALL";

    /**
     * Set log level.
     */
    public void setCategoryLogLevel(String category, String logLevel) throws Exception {
        assertPostCmd("/subsystem=logging/console-handler=CONSOLE:write-attribute(name=level,value=ALL)");
        exec("/subsystem=logging/logger=" + category + ":remove()");
        assertPostCmd(
            "/subsystem=logging/logger=" + category + ":add(level=" + logLevel + ",use-parent-handlers=true)");
    }

    /**
     * Remove a data source.
     */
    public void removeDataSource(String dsName) throws Exception {
        String res = f("/subsystem=datasources");
        if (exec(f("%s:read-children-resources(child-type=data-source)", res))) {
            Map<String, Object> m = JsonPath.parse(reply).read("$.result");
            if (m != null && m.containsKey(dsName)) {
                res = f("/subsystem=datasources/data-source=%s", dsName);
                assertPostCmd(f("%s:remove", res));
            }
        }
    }

    /**
     * Add a data source.
     * 
     * @param dsName
     *            the name of the data source
     * @param config
     *            the CLI style part after the command part, without the
     *            parentheses.
     */
    public void addDataSource(String dsName, String config) throws Exception {
        String res = f("/subsystem=datasources/data-source=%s", dsName);
        if (exec(f("%s:read-resource", res))) {
            return;
        }
        assertPostCmd(f("%s:add(%s)", res, config));
    }

    /**
     * Ping the server.
     */
    public boolean ping() throws Exception {
        try {
            return exec(":read-children-resources(child-type=system-property)");
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Wait for server to respond.
     * 
     * @throws Exception
     *             on timeout, {@link #TIMEOUT_SECS}
     */
    public void waitFor() throws Exception {
        long timeout = System.currentTimeMillis() + 1000 * TIMEOUT_SECS;
        while (System.currentTimeMillis() < timeout) {
            if (ping()) {
                return;
            }
            Thread.sleep(2000);
        }
        throw new IOException("timeout");
    }
}
