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

import com.github.jjYBdx4IL.jmon.dto.ServiceStateXfer;
import com.google.gson.JsonSyntaxException;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLSession;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RequestHandler extends AbstractHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);
    public static final String JSON_TYPE = "application/json";
    private final IServiceStateReceiver stateReceiver;

    public RequestHandler(IServiceStateReceiver receiver) {
        LOG.debug("RequestHandler()");
        this.stateReceiver = receiver;
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {

        if (!"POST".equals(request.getMethod())) {
            error(baseRequest, response, "http method not supported");
            return;
        }

        SSLSession ssl = (SSLSession) request.getAttribute("org.eclipse.jetty.servlet.request.ssl_session");
        if (ssl == null) {
            error(baseRequest, response, "no SSL");
            return;
        }

        // we use the remote certificate's CN subject part as the service's
        // hostname:
        String cn = extractCn((X509Certificate) ssl.getPeerCertificates()[0]);
        LOG.debug("authenticated cn: {}", cn);

        ServiceStateXfer serviceState;
        try {
            serviceState = Config.gson.fromJson(request.getReader(), ServiceStateXfer.class);
        } catch (JsonSyntaxException ex) {
            LOG.error("{}", cn, ex);
            error(baseRequest, response, "json syntax error");
            return;
        }

        if (!stateReceiver.handle(cn, serviceState)) {
            error(baseRequest, response, null);
            return;
        }
        
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
    }

    protected static void error(Request baseRequest, HttpServletResponse response, String publicMessage)
        throws IOException {
        if (publicMessage != null) {
            LOG.error(publicMessage);
        } else {
            publicMessage = "internal server error";
        }
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("text/plain; charset=utf-8");
        response.getWriter().println(publicMessage);
        baseRequest.setHandled(true);
    }

    private String extractCn(X509Certificate peerCert) {
        try {
            X500Name x500name = new JcaX509CertificateHolder(peerCert).getSubject();
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];
            return IETFUtils.valueToString(cn.getFirst().getValue());
        } catch (CertificateEncodingException ex) {
            LOG.error("", ex);
        }
        return null;
    }
}
