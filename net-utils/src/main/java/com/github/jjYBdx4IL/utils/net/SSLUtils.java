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

//CHECKSTYLE:OFF
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class SSLUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SSLUtils.class);

    public static void disableCertChecks() throws NoSuchAlgorithmException, KeyManagementException {
        LOG.warn("Disabling certificate checks.");

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
    
    public static String getSubjectPart(X509Certificate cert, String key) throws InvalidNameException {
        return getPrincipalPart(cert.getSubjectX500Principal(), key);
    }

    public static String getIssuerPart(X509Certificate cert, String key) throws InvalidNameException {
        return getPrincipalPart(cert.getIssuerX500Principal(), key);
    }

    public static String getPrincipalPart(X500Principal principal, String key) throws InvalidNameException {
        LdapName ldapName = new LdapName(principal.getName());
        String value = null;
        for (Rdn rdn : ldapName.getRdns()) {
            if (rdn.getType().equalsIgnoreCase(key)) {
                value = (String) rdn.getValue();
            }
        }
        return value;
    }

    private SSLUtils() {
    }

    public static enum PrincipalParts {
        C("C"), L("L"), ST("ST"), O("O"), OU("OU"), CN("CN");
        private final String value;
        PrincipalParts(String value) {
            this.value = value;
        }
        @Override
        public String toString() {
            return value;
        }
    }
}
