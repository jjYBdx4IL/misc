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
package com.github.jjYBdx4IL.web.markdown.publisher;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.env.Surefire;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jjYBdx4IL
 */
public class ConfigurationFrameTest implements ActionListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFrameTest.class);
    
    @Test
    public void test1() throws InterruptedException {
        Assume.assumeTrue(Surefire.isSingleTestExecution());
        
        ConfigurationFrame f = new ConfigurationFrame(this);
        f.pack();
        AWTUtils.centerOnScreen(AWTUtils.getMousePointerScreenDeviceIndex(), f);
        f.setVisible(true);
        
        Thread.sleep(30000);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        LOG.info(e.toString());
    }
    
}
