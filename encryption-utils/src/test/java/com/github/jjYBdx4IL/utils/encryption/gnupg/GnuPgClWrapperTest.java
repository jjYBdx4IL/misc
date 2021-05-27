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
package com.github.jjYBdx4IL.utils.encryption.gnupg;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.github.jjYBdx4IL.utils.env.Maven;

public class GnuPgClWrapperTest {

    @Test
    public void testDecryptTextAa() throws IOException, URISyntaxException, InterruptedException {
        GnuPgClWrapper wrapper = new GnuPgClWrapper();
        wrapper.setLogErrorOutput(true);
        String gnuPgHomeDir = new File(new File(Maven.getBasedir(GnuPgClWrapperTest.class)),
            "src/test/resources/gnupg_home").getAbsolutePath();
        gnuPgHomeDir = new File(GnuPgClWrapperTest.class.getResource("/gnupg_home").toURI()).getAbsolutePath();
        wrapper.setGnuPgHomeDir(gnuPgHomeDir);

        String encryptedTextAa = IOUtils
            .toString(GnuPgClWrapperTest.class.getResource("test_message.txt.asc").toURI(), "UTF-8");

        String plaintext = wrapper.decryptTextAa(encryptedTextAa);
        assertEquals("test message" + System.lineSeparator(), plaintext);
    }

}
