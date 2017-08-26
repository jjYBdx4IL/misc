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

import net.schmizz.sshj.connection.channel.direct.Session;

import static org.junit.Assert.assertEquals;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author jjYBdx4IL
 */
public class VMInstanceProviderRuleTest {

    @BeforeClass
    public static void beforeClass() {
        Assume.assumeTrue(LibvirtUtils.isAvailable() && !CI.isPublic());
    }

    @Rule
    public final VMInstanceProviderRule instanceProviderRule = new VMInstanceProviderRule();

    @Test
    public void testGetProvider() throws Exception {
        VMData vm = instanceProviderRule.getProvider().createVM(OS.UbuntuWilyAmd64);
        Session.Command cmd = vm.getSSHSession().exec("true");
        cmd.join(15, TimeUnit.SECONDS);
        assertEquals(0, cmd.getExitStatus().intValue());
    }

}
