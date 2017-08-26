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
import com.github.jjYBdx4IL.utils.env.CI;

import java.util.concurrent.TimeUnit;

import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class VMInstanceProviderTest {

    private static final Logger LOG = LoggerFactory.getLogger(VMInstanceProviderTest.class);

    @BeforeClass
    public static void beforeClass() {
        // require libvirt; require CI to have libvirt installed
        Assume.assumeTrue(LibvirtUtils.isAvailable() || CI.isCI() && !CI.isPublic());
    }
    
    private static void sshLoginTest(VMData vm) throws ConnectionException, TransportException {
        Command cmd = vm.getSSHSession().exec("true");
        cmd.join(15, TimeUnit.SECONDS);
        assertEquals(0, cmd.getExitStatus().intValue());
    }

    @Test
    public void testCreateVMCached() throws Exception {
        Assume.assumeFalse(CI.isCI());

        try (VMInstanceProvider vmip = new VMInstanceProvider()) {
            VMData vm = vmip.createVM(OS.UbuntuWilyAmd64);
            sshLoginTest(vm);
        }
    }

    @Test
    public void testCreateVMUncachedAndCached() throws Exception {
        Assume.assumeTrue(CI.isCI());

        long time1, time2;

        DiskImageCache.deleteIfExists(OS.UbuntuWilyAmd64.name());

        try (VMInstanceProvider vmip = new VMInstanceProvider()) {
            time1 = System.currentTimeMillis();
            VMData vm = vmip.createVM(OS.UbuntuWilyAmd64);
            sshLoginTest(vm);
            time1 = System.currentTimeMillis() - time1;
        }

        try (VMInstanceProvider vmip = new VMInstanceProvider()) {
            time2 = System.currentTimeMillis();
            VMData vm = vmip.createVM(OS.UbuntuWilyAmd64);
            sshLoginTest(vm);
            time2 = System.currentTimeMillis() - time2;
        }

        float speedup = time1/time2;
        LOG.info("speedup: " + speedup);
        assertTrue("speedup " + speedup + " larger than 4", speedup > 4f);
    }

}
