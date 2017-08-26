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
package com.github.jjYBdx4IL.utils.awt;

//CHECKSTYLE:OFF
import com.github.jjYBdx4IL.utils.env.Surefire;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.Locale;
import static org.junit.Assume.assumeTrue;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ClipBoardListenerTest {

    private static final Logger LOG = LoggerFactory.getLogger(ClipBoardListenerTest.class);

    @Test
    public void test() throws InterruptedException {
        assumeTrue(Surefire.isSingleTestExecution());

        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        
        new ClipBoardListener() {
            @Override
            public String onContentChange(String newTextContent) {
                LOG.info(newTextContent);
                return newTextContent.toUpperCase(Locale.ROOT);
            }
        }.takeOwnership(c, "");

        AWTUtils.showFrameAndWaitForCloseByUser();
    }

}
