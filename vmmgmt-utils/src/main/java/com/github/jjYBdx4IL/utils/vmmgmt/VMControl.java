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
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import freemarker.template.TemplateException;

/**
 *
 * @author jjYBdx4IL
 */
public class VMControl {

    private static final Logger log = LoggerFactory.getLogger(VMControl.class);
    public static final int vmOperationTimeoutSecs = 300;
    public static final long VM_STATUS_POLL_IVAL_MILLIS = 1000;
    public static final int MAX_START_TRIES = 10;
    private final VMData vm;
    private final Connect virtConn;
    private Domain domain = null;

    public VMControl(Connect conn, VMData vm) {
        this.virtConn = conn;
        this.vm = vm;
    }

    public Domain startVM(BootMode bootMode) throws IOException, LibvirtException, TemplateException {
        int ntries = 0;
        while(true) {
            ntries++;
            try {
                domain = virtConn.domainCreateXML(
                        vm.getDomainCreateXML(bootMode),
                        vm.isAutoDestroy() ? 2 : 0); // 2 == auto-destroy on connection lost
                return domain;
            } catch (LibvirtException ex) {
                if (ntries >= MAX_START_TRIES) {
                    throw ex;
                }
                log.info(String.format("failed to start VM, it follows retry no %d/%d", ntries, MAX_START_TRIES-1));
                vm.randomizePorts();
            }
        }
    }

    public void waitUntilVMHasStoppedByName(int timeoutSecs) {
        long timeout = System.currentTimeMillis() + timeoutSecs * 1000L;
        do {
            try {
                Thread.sleep(VM_STATUS_POLL_IVAL_MILLIS);
            } catch (InterruptedException ex) {}
        } while (isVMRunningByName() && System.currentTimeMillis() < timeout);
        if (isVMRunningByName()) {
            throw new RuntimeException("waiting for VM termination failed: " + vm.getName());
        }
    }

    public boolean isVMRunningByName() {
        try {
            virtConn.domainLookupByName(vm.getName());
            return true;
        } catch(LibvirtException ex) {}
        return false;
    }

    public void destroyVMByName() {
        try {
            Domain domain = virtConn.domainLookupByName(vm.getName());
            domain.destroy();
        } catch(LibvirtException ex) {}
        waitUntilVMHasStoppedByName(vmOperationTimeoutSecs);
    }

    public void sshExec(String cmd) throws IOException {
        @SuppressWarnings("resource")
        final SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());
        try {
            ssh.connect("localhost", vm.getSshForwardPort());
            ssh.authPassword("root", "");
            try (Session session = ssh.startSession()) {
                final Session.Command sessionCmd = session.exec(cmd);
                sessionCmd.join(5, TimeUnit.SECONDS);
                int exitCode = sessionCmd.getExitStatus();
                if (exitCode != 0) {
                    throw new RuntimeException("command " + cmd + " returned exit status code " + exitCode);
                }
            }
        } finally {
            if (ssh.isConnected()) {
                ssh.disconnect();
            }
        }
    }

    public void waitVMOnlineBySSH() {
        long timeout = System.currentTimeMillis() + vmOperationTimeoutSecs * 1000L;
        do {
            try {
                Thread.sleep(VM_STATUS_POLL_IVAL_MILLIS);
            } catch (InterruptedException ex) {}
        } while (!isVMOnlineBySSH() && System.currentTimeMillis() < timeout);
        if (!isVMOnlineBySSH()) {
            throw new RuntimeException("waiting for VM to go online failed: " + vm.getName());
        }
    }

    /**
     * Never returns null. Throws RuntimeException on timeout.
     * @return the ssh resources
     */
    public SSHResources waitForSSHSession() {
        long timeout = System.currentTimeMillis() + vmOperationTimeoutSecs * 1000L;
        SSHResources sshResources = null;
        do {
            try {
                Thread.sleep(VM_STATUS_POLL_IVAL_MILLIS);
            } catch (InterruptedException ex) {}
            try {
                sshResources = tryGetSSHSession();
            } catch (IOException ex) {
            }
        } while (sshResources == null && System.currentTimeMillis() < timeout);
        if (sshResources == null) {
            throw new RuntimeException("waiting for VM to go online failed: " + vm.getName());
        }
        return sshResources;
    }

    public boolean isVMOnlineBySSH() {
        try {
            sshExec("true");
            return true;
        } catch (Exception ex) {}
        return false;
    }

    void shutdownAndWait() throws LibvirtException {
        domain.shutdown();
        waitUntilVMHasStoppedByName(vmOperationTimeoutSecs);
    }

    class SSHResources {
        final SSHClient ssh;
        final Session session;
        SSHResources(SSHClient ssh, Session session) {
            this.ssh = ssh;
            this.session = session;
        }

        void close() {
            if (ssh.isConnected()) {
                if (session != null) {
                    try {
                        session.close();
                    } catch (ConnectionException | TransportException ex) {
                    }
                }
                try {
                    ssh.disconnect();
                } catch (IOException ex) {
                }
            }
        }
    }

    @SuppressWarnings("resource")
    private SSHResources tryGetSSHSession() throws IOException {
        final SSHClient ssh = new SSHClient();
        Session session = null;
        try {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect("localhost", vm.getSshForwardPort());
            ssh.authPassword("root", "");
            session = ssh.startSession();
            return new SSHResources(ssh, session);
        } catch (Throwable ex) {
            if (session != null) {
                try {
                    session.close();
                } catch (Throwable ex1) {
                }
            }
            if (ssh.isConnected()) {
                ssh.disconnect();
            }
            throw ex;
        }
    }

}
