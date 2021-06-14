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
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import freemarker.template.TemplateException;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jjYBdx4IL
 */
public class VMData {

    private static final Logger LOG = LoggerFactory.getLogger(VMData.class);
    
    public static final int SSH_MIN_PORT = 5000;
    public static final int SSH_MAX_PORT = 64000;
    public static final String VM_NAME_REGEX = "^[a-z][a-z0-9-]*$";
    public static final Pattern VM_NAME_PATTERN = Pattern.compile(VM_NAME_REGEX, Pattern.CASE_INSENSITIVE);
    private boolean autoDestroy = true;
    private File workDir = null;
    private OS os = null;
    private String name = null;
    private int ramMB = 1024;
    private DiskImage diskImage = null;
    private final List<PathMapping> pathMappings = new ArrayList<>();
    private int nvcpus = 0;
    private int sshForwardPort = 0;
    private URI installKernelURI = null;
    private URI installInitrdURI = null;
    private URI installIsoURI = null;
    private String installKernelSha256 = null;
    private String installInitrdSha256 = null;
    private String installIsoSha256 = null;
    private VMControl.SSHResources sshResources = null;
    private Random r = new Random();

    public VMData() {
        // do some sensible working directory auto-configuration when running under maven:
        String basedir = System.getProperty("basedir");
        if (basedir != null && !basedir.isEmpty()) {
            workDir = new File(basedir, "target" + File.separator + getClass().getPackage().getName());
        } else {
            try {
                workDir = Files.createTempDirectory("tmpvmdata").toFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getDomainCreateXML(BootMode bootMode) throws IOException, TemplateException {
        String xmlFileName = null;
        switch (bootMode) {
            case INSTALLATION:
                xmlFileName = "LibvirtInstallDomainTemplate.xml";
                break;
            case REGULAR:
                xmlFileName = "LibvirtRunDomainTemplate.xml";
                break;
        }
        return XMLWriter.createDomainCreateXML(xmlFileName, getXmlTplData());
    }

    public File getKernel() {
        return new File(getWorkDir(), "kernel");
    }

    public File getInitRD() {
        return new File(getWorkDir(), "initrd");
    }

    public File getInstallationISO() {
        return new File(getWorkDir(), "install.iso");
    }

    public Map<String, Object> getXmlTplData() {
        Map<String, Object> tplData = new HashMap<>();
        tplData.put("name", getName());
        tplData.put("title", "title");
        tplData.put("vmlinuzFile", getKernel().getAbsolutePath());
        tplData.put("initrdFile", getInitRD().getAbsolutePath());
        tplData.put("cmdline", "ks=file:///ks.cfg");
        tplData.put("ram", Integer.toString(getRamMB()));
        tplData.put("diskImg", getDiskImage().getImage().getAbsolutePath());
        tplData.put("isoImg", getInstallationISO().getAbsolutePath());
        tplData.put("sharedFolders", getSharedFolders());
        if (getNvcpus() < 1) {
            setNvcpus((int) new Integer(Runtime.getRuntime().availableProcessors()));
        }
        tplData.put("nvcpus", Integer.toString(getNvcpus()));
        tplData.put("sshFwdPort", Integer.toString(getSshForwardPort()));
        tplData.put("keymap", "");
        return tplData;
    }

    protected List<Map<String, Object>> getSharedFolders() {
        int mountTagId = 0;
        List<Map<String, Object>> list = new ArrayList<>();
        for (PathMapping pm : getPathMappings()) {
            Map<String, Object> map = new HashMap<>();
            map.put("source", pm.getSource());
            map.put("destination", pm.getDestination());
            map.put("mountTag", String.format("mounttag%d", mountTagId++));
            list.add(map);
        }
        return list;
    }

    /**
     * @return the autoDestroy
     */
    public boolean isAutoDestroy() {
        return autoDestroy;
    }

    /**
     * @param autoDestroy the autoDestroy to set
     */
    public void setAutoDestroy(boolean autoDestroy) {
        this.autoDestroy = autoDestroy;
    }

    /**
     * @return the workDir
     */
    public File getWorkDir() {
        return workDir;
    }

    /**
     * @param workDir the workDir to set
     */
    public void setWorkDir(File workDir) {
        this.workDir = workDir;
    }

    /**
     * @return the os
     */
    public OS getOs() {
        return os;
    }

    /**
     * @param os the os to set
     */
    public void setOs(OS os) {
        this.os = os;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        if (!VM_NAME_PATTERN.matcher(name).find()) {
            throw new IllegalArgumentException("illegal VM name: " + name + ", validation regex is " + VM_NAME_REGEX);
        }
        this.name = name;
    }

    /**
     * @return the ramMB
     */
    public int getRamMB() {
        return ramMB;
    }

    /**
     * @param ramMB the ramMB to set
     */
    public void setRamMB(int ramMB) {
        this.ramMB = ramMB;
    }

    /**
     * @return the diskImage
     */
    public DiskImage getDiskImage() {
        return diskImage;
    }

    /**
     * @param diskImage the diskImage to set
     */
    public void setDiskImage(DiskImage diskImage) {
        this.diskImage = diskImage;
    }

    /**
     * @return the pathMappings
     */
    public List<PathMapping> getPathMappings() {
        return pathMappings;
    }

    /**
     * @return the nvcpus
     */
    public int getNvcpus() {
        return nvcpus;
    }

    /**
     * @param nvcpus the nvcpus to set
     */
    public void setNvcpus(int nvcpus) {
        this.nvcpus = nvcpus;
    }

    /**
     * @return the sshForwardPort
     */
    public int getSshForwardPort() {
        return sshForwardPort;
    }

    /**
     * @param sshForwardPort the sshForwardPort to set
     */
    public void setSshForwardPort(int sshForwardPort) {
        this.sshForwardPort = sshForwardPort;
    }

    public URI getInstallKernelURI() throws URISyntaxException {
        if (installKernelURI == null) {
            return new URI(os.getDefaultInstallKernelURL());
        }
        return installKernelURI;
    }

    public URI getInstallInitrdURI() throws URISyntaxException {
        if (installInitrdURI == null) {
            return new URI(os.getDefaultInstallInitrdURL());
        }
        return installInitrdURI;
    }

    public URI getInstallIsoURI() throws URISyntaxException {
        if (installIsoURI == null) {
            return new URI(os.getDefaultInstallIsoURL());
        }
        return installIsoURI;
    }

    public String getInstallKernelSha256() throws URISyntaxException {
        if (installKernelURI == null) {
            return os.getDefaultInstallKernelSha256();
        }
        return installKernelSha256;
    }

    public String getInstallInitrdSha256() throws URISyntaxException {
        if (installInitrdURI == null) {
            return os.getDefaultInstallInitrdSha256();
        }
        return installInitrdSha256;
    }

    public String getInstallIsoSha256() throws URISyntaxException {
        if (installIsoURI == null) {
            return os.getDefaultInstallIsoSha256();
        }
        return installIsoSha256;
    }

    public SSHClient getSSHClient() {
        return sshResources.ssh;
    }

    public Session getSSHSession() {
        return sshResources.session;
    }

    void releaseResources() {
        releaseSSHResources();
        try {
            FileUtils.deleteDirectory(workDir);
        } catch (IOException e) {
            if (SystemUtils.IS_OS_LINUX) {
                LOG.warn("failed to delete " + workDir);
            }
            workDir.deleteOnExit();
        }
    }
    
    void releaseSSHResources() {
        if (sshResources != null) {
            sshResources.close();
        }
    }

    void setSSHResources(VMControl.SSHResources sshResources) {
        this.sshResources = sshResources;
    }

    void randomizePorts() {
        int lastPort = sshForwardPort;
        while (sshForwardPort == lastPort) {
            sshForwardPort = r.nextInt(SSH_MAX_PORT + 1 - SSH_MIN_PORT) + SSH_MIN_PORT;
        }
    }

}
