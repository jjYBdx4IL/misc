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
//CHECKSTYLE:OFF
package com.github.jjYBdx4IL.utils.vmmgmt;

import com.github.jjYBdx4IL.utils.io.DlCache;

import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;
import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import freemarker.template.TemplateException;

/**
 * libvirt vm install.
 *
 * @author jjYBdx4IL
 */
public class VMInstall {
    
    private static final Logger LOG = LoggerFactory.getLogger(VMInstall.class);
    public static final int VM_INSTALLATION_TIMEOUT_SECS = 3600;

    private final VMData vm;
    private final Connect conn;

    public VMInstall(Connect conn, VMData vm) {
        this.conn = conn;
        this.vm = vm;
    }

    void run() throws IOException, URISyntaxException, TemplateException, LibvirtException {
        VMControl ctrl = new VMControl(conn, vm);

        if (ctrl.isVMRunningByName()) {
            ctrl.destroyVMByName();
        }

        try {
            fetch(vm.getInstallKernelURI(), vm.getInstallKernelSha256(), vm.getKernel());
        } catch (IOException ex) {
            LOG.error("failed to download: {}", vm.getInstallKernelURI());
            throw ex;
        }
        try {
            fetch(vm.getInstallInitrdURI(), vm.getInstallInitrdSha256(), vm.getInitRD());
        } catch (IOException ex) {
            LOG.error("failed to download: {}", vm.getInitRD());
            throw ex;
        }
        try {
            fetch(vm.getInstallIsoURI(), vm.getInstallIsoSha256(), vm.getInstallationISO());
        } catch (IOException ex) {
            LOG.error("failed to download: {}", vm.getInstallationISO());
            throw ex;
        }
        vm.getDiskImage().create();

        // append kickstart config to initrd
        appendKickstartConfig();

        ctrl.startVM(BootMode.INSTALLATION);
        ctrl.waitUntilVMHasStoppedByName(VM_INSTALLATION_TIMEOUT_SECS);

        // test installed VM for SSH connectivity
        ctrl.startVM(BootMode.REGULAR);
        ctrl.waitVMOnlineBySSH();
        ctrl.shutdownAndWait();
    }

    protected void appendKickstartConfig() throws IOException, TemplateException {
        KickStart ks = new KickStart(vm.getOs());
        byte[] kickstartContent = ks.createFileContent();
        try (OutputStream os = new FileOutputStream(vm.getInitRD(), true)) {
            try (GzipCompressorOutputStream gzos = new GzipCompressorOutputStream(os)) {
                try (CpioArchiveOutputStream cpio = new CpioArchiveOutputStream(gzos, CpioArchiveOutputStream.FORMAT_NEW)) {
                    CpioArchiveEntry ae = new CpioArchiveEntry(CpioArchiveEntry.FORMAT_NEW, "ks.cfg", kickstartContent.length);
                    cpio.putArchiveEntry(ae);
                    IOUtils.write(kickstartContent, cpio);
                    cpio.closeArchiveEntry();
                }
            }
        }
    }

    protected void fetch(URI uri, String sha256, File destFile) throws IOException {
        File parentDir = destFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (InputStream is = DlCache.get(uri.toURL(), sha256, null)) {
            try (OutputStream os = new FileOutputStream(destFile)) {
                IOUtils.copyLarge(is, os);
            }
        }
    }

}
