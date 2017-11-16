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
package com.github.jjYBdx4IL.cms.jpa.dto;

import com.github.jjYBdx4IL.cms.Description;

import java.lang.reflect.Field;

//CHECKSTYLE:OFF
public enum ConfigKey {

    //@formatter:off
    GOOGLE_OAUTH2_CLIENT_ID,
    GOOGLE_OAUTH2_CLIENT_SECRET,
    WEBSITE_TITLE,
    HTML_HEAD_FRAGMENT,
    HTML_FOOT_FRAGMENT,
    @Description("leave empty to disable, set to 'true' to enable")
    ENABLE_ADBLOCK_BLOCKER,
    @Description("leave empty to disable cookie consent banner, enter consent message to activate.")
    COOKIE_CONSENT_MESSAGE,
    @Description("enter comma-separated list of admin users, ie 'google-$subject,...' "+
        "where $subject is Google's unique used identifier")
    ADMINS,
    FEEDBACK_MAILTO_ADDR,
    BANNER_HTML,
    MAX_NEW_URLS_PER_DOC,
    IMPRESSUM_URL,
    PRIVACY_POLICY_URL,
    ABOUT_URL;
    //@formatter:on

    public String getDesc() {
        try {
            Field f = ConfigKey.class.getField(name());
            if (f.isAnnotationPresent(Description.class)) {
                Description desc = f.getAnnotation(Description.class);
                return desc.value();
            }
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
