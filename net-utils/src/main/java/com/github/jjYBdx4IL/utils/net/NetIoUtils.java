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
package com.github.jjYBdx4IL.utils.net;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;

public class NetIoUtils {

    /**
     * Perform POST operation and use application/x-www-form-urlencoded to send given parameters.
     * This basically simulates a html form submission (post) operation.
     * 
     * @param url the remote service location
     * @param params the application/x-www-form-urlencoded parameters
     * @return the returned html page upon completing the POST operation
     * @throws IOException on error
     */
    public static String toStringDoPost(URL url, String... params) throws IOException {
        if (params.length % 2 == 1) {
            throw new IllegalArgumentException();
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(url.toExternalForm());

            List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(params.length / 2);
            for (int i = 0; i < params.length; i += 2) {
                nameValuePair.add(new BasicNameValuePair(params[i], params[i + 1]));
            }

            // Url Encoding the POST parameters
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new IOException(e);
            }

            try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                HttpEntity resEntity = response.getEntity();
                Charset resCharset = getCharset(resEntity);
                return IOUtils.toString(resEntity.getContent(), resCharset);
            }
        }
    }

    /**
     * similar to {@link IOUtils#toString(URL,String)}, but allows to define the
     * accepted content type. The charset used to decode the remote's reply is
     * taken from the reply headers.
     * 
     * @param url
     *            the remote service location
     * @param acceptHeader
     *            ie. "text/plain; charset=ASCII" or "text/html" etc.
     * @return the url's content
     * @throws IOException
     *             on I/O error, ie. remote does not indicate any charset
     */
    public static String toString(URL url, String acceptHeader) throws IOException {
        return toString(url, (Charset) null, acceptHeader);
    }

    /**
     * similar to {@link IOUtils#toString(URL,String)}, but allows to define the
     * accepted content type.
     * 
     * @param url
     *            the remote service location
     * @param charset
     *            expected charset returned by remote service, used if headers
     *            returned by remote do not indicate anything
     * @param acceptHeader
     *            ie. "text/plain; charset=ASCII" or "text/html" etc.
     * @return the url's content
     * @throws IOException
     *             on I/O error
     */
    public static String toString(URL url, String charset, String acceptHeader) throws IOException {
        try {
            return toString(url, charset != null ? Charset.forName(charset) : null, acceptHeader);
        } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
            throw new IOException(ex);
        }
    }

    /**
     * similar to {@link IOUtils#toString(URL,Charset)}, but allows to define
     * the accepted content type.
     * 
     * @param url
     *            the remote service location
     * @param charset
     *            expected charset returned by remote service, used if headers
     *            returned by remote do not indicate anything
     * @param acceptHeader
     *            ie. "text/plain; charset=ASCII" or "text/html" etc.
     * @return the url's content
     * @throws IOException
     *             on I/O error
     */
    public static String toString(URL url, Charset charset, String acceptHeader) throws IOException {
        if (acceptHeader == null) {
            throw new IllegalArgumentException("acceptHeader must not be null");
        }
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept", acceptHeader);
        try (InputStream is = urlConnection.getInputStream()) {
            Charset expectedCharset = getCharset(urlConnection);
            if (expectedCharset == null) {
                expectedCharset = charset;
            }
            if (expectedCharset == null) {
                throw new IOException("failed to determine charset for remote's reply");
            }
            return IOUtils.toString(is, expectedCharset);
        } finally {
            urlConnection.disconnect();
        }
    }

    private static Charset getCharset(URLConnection connection) throws IOException {
        return getCharset(connection.getContentType());
    }

    private static Charset getCharset(HttpEntity entity) throws IOException {
        return getCharset(entity.getContentType().getValue());
    }

    private static Charset getCharset(String contentType) throws IOException {
        String[] values = contentType.split(";"); // values.length should be 2

        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                try {
                    return Charset.forName(value.substring("charset=".length()));
                } catch (IllegalCharsetNameException | UnsupportedCharsetException ex) {
                    throw new IOException(ex);
                }
            }
        }

        return null;
    }
}
