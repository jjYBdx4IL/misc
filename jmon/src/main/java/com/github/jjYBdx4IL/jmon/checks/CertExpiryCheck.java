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
package com.github.jjYBdx4IL.jmon.checks;

import static com.github.jjYBdx4IL.utils.text.StringUtil.f;

import com.github.jjYBdx4IL.jmon.Config;
import com.github.jjYBdx4IL.jmon.dto.HostDef;
import com.github.jjYBdx4IL.jmon.dto.ServiceDef;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.net.ssl.HttpsURLConnection;

public class CertExpiryCheck extends CheckBase implements ICheck {

    public static final int WARN_DAYS = 10;
    public static final int ERROR_DAYS = 6;
    
    private final URL url;
    private final boolean truststore;
    
    public CertExpiryCheck(HostDef hostDef, ServiceDef serviceDef) {
        super(hostDef, serviceDef);
        
        Conf conf = null;
        if (serviceDef.conf != null) {
            conf = Config.gson.fromJson(serviceDef.conf, Conf.class);
        }
        if (conf != null) {
            truststore = conf.truststore;
        } else {
            truststore = false;
        }
        
        try {
            if (conf != null && conf.port != 0) {
                url = new URL(f("https://%s:%d", serviceDef.hostDef.hostname, conf.port));
            } else {                
                url = new URL(f("https://%s", serviceDef.hostDef.hostname));
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CheckResult execute() throws Exception {
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        if (truststore) {
            conn.setSSLSocketFactory(Config.sslSocketFactory);
        }
        try (InputStream is = conn.getInputStream()) {
            conn.connect();
            
            X509Certificate c = (X509Certificate) conn.getServerCertificates()[0];
            if (c == null) {
                throw new Exception("remote cert not found");
            }
            
            Date notAfter = c.getNotAfter();
            if (notAfter == null) {
                throw new Exception("notAfter date not found");
            }
            
            long daysRemaining = (notAfter.getTime() - System.currentTimeMillis()) / 86400L / 1000L;
            int status = 0;
            if (daysRemaining < ERROR_DAYS) {
                status = 2;
            }
            else if (daysRemaining < WARN_DAYS) {
                status = 1;
            }
            
            return new CheckResult(f("certificate expires in %d days", daysRemaining), status);
        }
    }

    public static String help() {
        return "{\"truststore\":false} : use server truststore.\n"
            + "  {\"port\":443} : use non-standard remote port.\n"
            + "  10 days - warning, 6 days before expiry - error";
    }
    
    public static class Conf {
        public boolean truststore;
        public int port;
    }
}
