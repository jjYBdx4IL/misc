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
package com.github.jjYBdx4IL.utils.vmmgmt;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public enum OS {

    /**
     * Ubuntu 15.10 amd64
     */
    UbuntuWilyAmd64(
            "http://archive.ubuntu.com/ubuntu/dists/wily/main/installer-amd64/current/images/cdrom/vmlinuz",
            "http://archive.ubuntu.com/ubuntu/dists/wily/main/installer-amd64/current/images/cdrom/initrd.gz",
            "http://releases.ubuntu.com/15.10/ubuntu-15.10-server-amd64.iso",
            "KickStart-UbuntuWilyAmd64.tpl"
    );

    private final String defaultInstallKernelURL;
    private final String defaultInstallInitrdURL;
    private final String defaultInstallIsoURL;
    private final String kickstartTplName;

    private OS(String defaultInstallKernelURL, String defaultInstallInitrdURL, String defaultInstallIsoURL, String kickstartTplName) {
        this.defaultInstallKernelURL = defaultInstallKernelURL;
        this.defaultInstallInitrdURL = defaultInstallInitrdURL;
        this.defaultInstallIsoURL = defaultInstallIsoURL;
        this.kickstartTplName = kickstartTplName;
    }

    /**
     * @return the defaultInstallKernelURL
     */
    public String getDefaultInstallKernelURL() {
        return defaultInstallKernelURL;
    }

    /**
     * @return the defaultInstallInitrdURL
     */
    public String getDefaultInstallInitrdURL() {
        return defaultInstallInitrdURL;
    }

    /**
     * @return the defaultInstallIsoURL
     */
    public String getDefaultInstallIsoURL() {
        return defaultInstallIsoURL;
    }

    /**
     * @return the kickstartTplName
     */
    public String getKickstartTplName() {
        return kickstartTplName;
    }
}
