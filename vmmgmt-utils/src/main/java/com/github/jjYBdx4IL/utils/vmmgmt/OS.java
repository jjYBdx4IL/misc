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
            "http://old-releases.ubuntu.com/ubuntu/dists/wily/main/installer-amd64/current/images/cdrom/vmlinuz",
            "8e2339d6d10d26adaece26ab939eb6eea4a3a5c2d0d91029257d60af16a87890",
            "http://old-releases.ubuntu.com/ubuntu/dists/wily/main/installer-amd64/current/images/cdrom/initrd.gz",
            "4149ece322e1d59f1cb2414e02c1fd0db276c1bf10454f3676b5cbe69315cee7",
            //"http://releases.ubuntu.com/15.10/ubuntu-15.10-server-amd64.iso",
            "http://www.isoarchive.nl/operating_system/unix/ubuntu/wily%20werewolf/ubuntu-15.10-server-amd64.iso",
            "86aa35a986eba6e5ad30e3d486d57efe6803ae7ea4859b0216953e9e62871131",
            "KickStart-UbuntuWilyAmd64.tpl"
    );

    private final String defaultInstallKernelURL;
    private final String defaultInstallKernelSha256;
    private final String defaultInstallInitrdURL;
    private final String defaultInstallInitrdSha256;
    private final String defaultInstallIsoURL;
    private final String defulatInstallIsoSha256;
    private final String kickstartTplName;

    private OS(String defaultInstallKernelURL, String sha256kernel,
        String defaultInstallInitrdURL, String sha256initrd,
        String defaultInstallIsoURL, String sha256iso,
        String kickstartTplName) {
        this.defaultInstallKernelURL = defaultInstallKernelURL;
        this.defaultInstallKernelSha256 = sha256kernel;
        this.defaultInstallInitrdURL = defaultInstallInitrdURL;
        this.defaultInstallInitrdSha256 = sha256initrd;
        this.defaultInstallIsoURL = defaultInstallIsoURL;
        this.defulatInstallIsoSha256 = sha256iso;
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

    /**
     * @return the defaultInstallKernelSha256
     */
    public String getDefaultInstallKernelSha256() {
        return defaultInstallKernelSha256;
    }

    /**
     * @return the defaultInstallInitrdSha256
     */
    public String getDefaultInstallInitrdSha256() {
        return defaultInstallInitrdSha256;
    }

    /**
     * @return the defulatInstallIsoSha256
     */
    public String getDefaultInstallIsoSha256() {
        return defulatInstallIsoSha256;
    }
}
