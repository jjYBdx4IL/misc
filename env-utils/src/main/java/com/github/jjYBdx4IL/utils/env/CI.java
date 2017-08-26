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
package com.github.jjYBdx4IL.utils.env;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class CI extends Env {

    private final static String HUDSON_HOME = "HUDSON_HOME";
    private final static String JENKINS_HOME = "JENKINS_HOME";
    private final static String HUDSON_URL = "HUDSON_URL";
    private final static String JENKINS_URL = "JENKINS_URL";

    public static boolean isCI() {
        return isJenkins() || isHudson() || isTravis() || isPublic()
                || Boolean.parseBoolean(System.getenv("CI"))
                || Boolean.parseBoolean(System.getenv("CONTINUOUS_INTEGRATION"));
    }

    public static boolean isPublic() {
        return System.getenv("PUBLIC_CI") != null;
    }

    public static boolean isTravis() {
        return Boolean.parseBoolean(System.getenv("TRAVIS"));
    }

    public static boolean isJenkins() {
        return System.getenv(JENKINS_HOME) != null;
    }

    public static boolean isHudson() {
        return System.getenv(HUDSON_HOME) != null;
    }

    public static String getJenkinsHome() {
        return get(JENKINS_HOME);
    }

    public static String getHudsonHome() {
        return get(HUDSON_HOME);
    }

    public static String getJenkinsUrl() {
        return get(JENKINS_URL);
    }

    public static String getHudsonUrl() {
        return get(HUDSON_URL);
    }

    public static String getJenkinsUrl(String fallback) {
        return get(JENKINS_URL, fallback);
    }

    public static String getHudsonUrl(String fallback) {
        return get(HUDSON_URL, fallback);
    }

    public static String getCIUrl() {
        return getCIUrl(null);
    }

    public static String getCIUrl(String fallback) {
        if (isJenkins()) {
            return getJenkinsUrl(fallback);
        }
        return getHudsonUrl(fallback);
    }

}
