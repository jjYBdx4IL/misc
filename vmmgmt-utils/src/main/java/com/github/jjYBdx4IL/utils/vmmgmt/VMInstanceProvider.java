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
import com.sun.jna.Pointer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.libvirt.Connect;
import org.libvirt.LibvirtException;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.virError;

import freemarker.template.TemplateException;

/**
 *
 * @author jjYBdx4IL
 */
public class VMInstanceProvider implements AutoCloseable {

    private final Connect conn;

    private final List<VMData> vms = new ArrayList<>();

    public VMInstanceProvider() throws LibvirtException {
        this(ConnectType.QemuSession);
    }

    /**
     *
     * @param connectType
     * @param vmName
     * @param autoDestroy
     * @throws LibvirtException
     */
    private VMInstanceProvider(ConnectType connectType) throws VMMgmtException {
        try {
            conn = new Connect(connectType.getValue());
            // prevent libvirt error messages from cluttering the console:
            conn.setConnectionErrorCallback(new Libvirt.VirErrorCallback() {
                @Override
                public void errorCallback(Pointer userData, virError error) {
                }
            });
        } catch (LibvirtException ex) {
            throw new VMMgmtException(ex);
        }
    }

    @Override
    public void close() throws VMMgmtException {
        for (VMData vm : vms) {
            vm.releaseSSHResources();
        }
        try {
            conn.close();
        } catch (LibvirtException ex) {
            throw new VMMgmtException(ex);
        }
    }

    /**
     * Prepare a running VM in a defined, clean state. This method assumes the VM to be named "default". Any
     * previously running VM having the same name and being executed in the same domain, will get destroyed
     * first.
     *
     * @param os the OS to install
     * @return the vm data
     */
    public VMData createVM(OS os) throws VMMgmtException {
        return createVM(os, "default");
    }

    /**
     * Prepare a running VM in a defined, clean state. Any previously running VM having the same name and
     * being executed in the same domain, will get destroyed first.
     *
     * @param os the OS to install
     * @param vmName identifies the VM instance. There cannot be two VMs running with the same name in the
     * same domain at one time.
     * @return the vm data
     */
    public VMData createVM(OS os, String vmName) throws VMMgmtException {
        try {
            VMData vm = new VMData();
            vm.setOs(os);
            vm.setName(vmName);
            vm.setAutoDestroy(true);
            vm.setSshForwardPort(5555);
            DiskImage diskImage = DiskImageCache.get(os.name());
            // installation required?
            if (diskImage == null) {
                diskImage = new DiskImage(new File(vm.getWorkDir(), "disk.img"), DiskImageFormat.qcow2, 100, null);
                vm.setDiskImage(diskImage);
                new VMInstall(conn, vm).run();
                // store disk image
                diskImage = DiskImageCache.put(os.name(), diskImage);
            }
            // run on snapshot of clean image
            DiskImage snapshot = new DiskImage(
                    new File(vm.getWorkDir(), "snapshot"),
                    diskImage.getFormat(),
                    diskImage.getSizeGB(),
                    diskImage.getImage());
            snapshot.create();
            vm.setDiskImage(snapshot);

            VMControl ctrl = new VMControl(conn, vm);
            if (ctrl.isVMRunningByName()) {
                ctrl.destroyVMByName();
            }
            ctrl.startVM(BootMode.REGULAR);
            vm.setSSHResources(ctrl.waitForSSHSession());

            vms.add(vm);

            return vm;
        } catch (IOException | URISyntaxException | TemplateException | LibvirtException ex) {
            throw new VMMgmtException(ex);
        }
    }

}
