package com.github.jjYBdx4IL.cms.rest.app;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Context;

public class SessionData {

    public static final String SESSION_ATTRNAME = "session.data";

    private String userId = null;
    private String email = null;
    private String googleOauth2StateSecret = null;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGoogleOauth2StateSecret() {
        return googleOauth2StateSecret;
    }

    public void setGoogleOauth2StateSecret(String googleOauth2StateSecret) {
        this.googleOauth2StateSecret = googleOauth2StateSecret;
    }

    public boolean isAuthenticated() {
        return getUserId() != null;
    }

    public void logout() {
        setUserId(null);
        setEmail(null);
    }

    public static SessionData getOrCreate(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute(SESSION_ATTRNAME) == null) {
            session.setAttribute(SESSION_ATTRNAME, new SessionData());
        }
        return (SessionData) session.getAttribute(SESSION_ATTRNAME);
    }
}
