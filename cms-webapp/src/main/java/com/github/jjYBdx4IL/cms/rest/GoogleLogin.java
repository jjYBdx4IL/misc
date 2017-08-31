package com.github.jjYBdx4IL.cms.rest;

import com.github.jjYBdx4IL.cms.jpa.dto.ConfigKey;
import com.github.jjYBdx4IL.cms.jpa.dto.QueryFactory;
import com.github.jjYBdx4IL.cms.jpa.tx.TxRo;
import com.github.jjYBdx4IL.cms.rest.app.SessionData;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Locale;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@SuppressWarnings("deprecation")
@Path("googleLogin")
public class GoogleLogin {

    private static final Logger LOG = LoggerFactory.getLogger(GoogleLogin.class);

    @Context
    UriInfo uriInfo;
    @Inject
    public EntityManager em;
    @Inject
    private SessionData session;

    @GET
    @TxRo
    public Response login() throws URISyntaxException {
        LOG.trace("login()");

        // this is per login:
        String stateSecret = RandomStringUtils.randomAlphanumeric(16, 17);
        session.setGoogleOauth2StateSecret(stateSecret);

        // set up the code flow support instance:
        GoogleAuthorizationCodeFlow codeFlow = getGAuthCodeFlow();
        String callbackUrl = uriInfo.getBaseUriBuilder().path(GoogleLogin.class).path(GoogleLogin.class, "callback")
            .build().toASCIIString();
        String loginUrl = codeFlow.newAuthorizationUrl().setState(stateSecret).setRedirectUri(callbackUrl)
            .build();
        LOG.info("codeFlow.newAuthorizationUrl()...build(): " + loginUrl);

        return Response.temporaryRedirect(new URI(loginUrl))
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

    // https://console.developers.google.com/apis/credentials
    @GET
    @TxRo
    @Path("googleOauth2Callback")
    public Response callback(@QueryParam("code") String code, @QueryParam("state") String state) throws IOException {
        LOG.trace("callback()");

        if (code == null || code.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("code required").build();
        }
        if (state == null || state.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("state required").build();
        }

        GoogleAuthorizationCodeFlow codeFlow = getGAuthCodeFlow();
        // from the redirect we obtain the code and verify the state secret for
        // security:
        LOG.info(String.format(Locale.ROOT, "code=%s, state=%s", code, state));
        String sessionState = session.getGoogleOauth2StateSecret();
        if (sessionState == null || sessionState.isEmpty()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("bad state (1)").build();
        }
        if (!sessionState.equals(state)) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("bad state (2)").build();
        }
        session.setGoogleOauth2StateSecret(null);

        // third step: now we can use (once only) the code to get and verify the
        // user's verified Google ID
        // contained in the token response:
        String callbackUrl = uriInfo.getBaseUriBuilder().path(GoogleLogin.class).path(GoogleLogin.class, "callback")
            .build()
            .toASCIIString();
        GoogleTokenResponse tokenResponse = codeFlow.newTokenRequest(code).setRedirectUri(callbackUrl)
            .execute(); // POST request to Google token API
        Payload payload = tokenResponse.parseIdToken().getPayload();

        LOG.info("user info payload: " + payload);
        if (payload == null) {
            throw new IllegalStateException();
        }

        LOG.info("unique google user id: " + payload.getSubject());
        LOG.info("user email: " + payload.getEmail());
        LOG.info("user email verified: " + payload.getEmailVerified());
        if (payload.getSubject() == null || payload.getSubject().isEmpty()) {
            throw new IllegalStateException();
        }
        if (payload.getEmail() == null || payload.getEmail().isEmpty()) {
            throw new IllegalStateException();
        }
        if (payload.getEmailVerified() == null) {
            throw new IllegalStateException();
        }

        if (!payload.getEmailVerified()) {
            return Response.status(HttpServletResponse.SC_BAD_REQUEST).entity("email not verified").build();
        }

        session.setUserId(payload.getSubject());
        session.setEmail(payload.getEmail());

        return Response.temporaryRedirect(uriInfo.getBaseUriBuilder().path(Root.class).build())
            .status(HttpServletResponse.SC_FOUND)
            .build();
    }

    private GoogleAuthorizationCodeFlow getGAuthCodeFlow() {
        String clientId = QueryFactory.getConfigValue(em, ConfigKey.KEY_GOOGLE_OAUTH2_CLIENT_ID);
        String clientSecret = QueryFactory.getConfigValue(em, ConfigKey.KEY_GOOGLE_OAUTH2_CLIENT_SECRET);
        return new GoogleAuthorizationCodeFlow(
            new NetHttpTransport(), new JacksonFactory(), clientId, clientSecret,
            Arrays.asList(new String[] { "openid", "email" }));
    }
}