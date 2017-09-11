package com.github.jjYBdx4IL.cms.rest.app;

import java.lang.reflect.Method;

public interface SessionData {

    String SESSION_ATTRNAME = "session.data";

    String getUid();

    void setUid(String uid);

    String getGoogleOauth2StateSecret();

    void setGoogleOauth2StateSecret(String googleOauth2StateSecret);

    boolean isAuthenticated();

    void logout();

    boolean isAllowed(Method method);

    String toString();

    boolean isDevel();

}