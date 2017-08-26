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

import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Github jjYBdx4IL Projects
 */
public class AWTUtilsTest {

    private static final Logger LOG = LoggerFactory.getLogger(AWTUtilsTest.class);

    @Before
    public void before() {
        Assume.assumeTrue(Surefire.isSingleTestExecution());
    }

    @Test
    public void testAskForConfirmationOnMouseScreen() {
        LOG.info(Boolean.toString(AWTUtils.askForConfirmationOnMouseScreen("title", "are you sure?")));
    }

    @Test
    public void testShowInfoDialogOnMouseScreen() {
        AWTUtils.showInfoDialogOnMouseScreen("title", "<html>line 1<br />line 2<br /><i><u>test</u></i><br /><pre>1234567890<br>       8</pre></html>");
    }

    @Test
    public void testShowPopupNotification() throws InterruptedException {
        Assume.assumeTrue(Surefire.isSingleTestExecution());

        AWTUtils.showPopupNotification(-1, "some test message", AWTUtils.POS_RIGHT|AWTUtils.POS_BOTTOM, 5000, false);
        Thread.sleep(7000L);
    }

}
