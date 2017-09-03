package com.github.jjYBdx4IL.cms.rest.app;

import com.github.jjYBdx4IL.cms.jpa.dto.User;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionData implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public static final String SESSION_ATTRNAME = "session.data";

    private User user = null;
    private String googleOauth2StateSecret = null;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getGoogleOauth2StateSecret() {
        return googleOauth2StateSecret;
    }

    public void setGoogleOauth2StateSecret(String googleOauth2StateSecret) {
        this.googleOauth2StateSecret = googleOauth2StateSecret;
    }

    public boolean isAuthenticated() {
        return getUser() != null;
    }

    public void logout() {
        setUser(null);
    }

    public static SessionData getOrCreate(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session.getAttribute(SESSION_ATTRNAME) == null) {
            session.setAttribute(SESSION_ATTRNAME, new SessionData());
        }
        return (SessionData) session.getAttribute(SESSION_ATTRNAME);
    }
}
