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
package com.github.jjYBdx4IL.desktop.clipboardmgr.plugin;

import com.github.jjYBdx4IL.desktop.clipboardmgr.ClipBoardPlugin;
import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.awt.JTextDisplayFrame;
import com.github.jjYBdx4IL.utils.encryption.gnupg.GnuPgClDecryptionException;
import com.github.jjYBdx4IL.utils.encryption.gnupg.GnuPgClWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decrypts a PGP message stored in the OS clipboard. Uses the external
 * <code>gpg</code> command line tool. Tested with cygwin under Windows
 * 10/amd64. Does <b>not</b> store the decrypted message in the clipboard, but
 * opens a window for each decrypted message to display it.
 *
 * @author jjYBdx4IL
 */
public class GpgDecryptionPlugin implements ClipBoardPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(GpgDecryptionPlugin.class);
    
    public static final Pattern PAT = Pattern.compile("(-----BEGIN PGP MESSAGE-----.*?-----END PGP MESSAGE-----)",
        Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    public String onNewText(String newTextContent) {
        Matcher matcher = PAT.matcher(newTextContent);
        boolean found = false;
        GnuPgClWrapper gpg = new GnuPgClWrapper();
        while (matcher.find()) {
            found = true;
            String encryptedMessage = matcher.group(1);
            try {
                String decryptedMessage = gpg.decryptTextAa(encryptedMessage);
                LOG.info(decryptedMessage);
                final JTextDisplayFrame frame = new JTextDisplayFrame(decryptedMessage);
                frame.setAutoRequestFocus(true);
                frame.pack();
                AWTUtils.centerOnMouseScreen(frame);
                frame.setVisible(true);
            } catch (GnuPgClDecryptionException | InterruptedException e) {
                LOG.error("", e);
            }
        }
        return found ? newTextContent : null;
    }

}
