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

import static org.junit.Assume.assumeFalse;

import org.junit.Before;
import org.junit.Test;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.JInternalFrame;

public class MdiAutoScaleFrameTest {

    @Before
    public void before() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }
    
    @Test
    public void test() {
        MdiAutoScaleFrame frame = new MdiAutoScaleFrame(MdiAutoScaleFrame.class.getSimpleName() + " Test");
        frame.setPreferredSize(new Dimension(800, 600));
        frame.add(create("internal 1", 320, 260));
        frame.add(create("internal 2", 240, 200));
        frame.add(create("internal 3", 740, 200));

        AWTUtils.showFrameAndWaitForCloseByUserTest(frame);        
    }
    
    JInternalFrame create(String title, int w, int h) {
        JInternalFrame intFrame = new JInternalFrame(title);
        intFrame.setResizable(true);
        intFrame.setMaximizable(true);
        intFrame.setPreferredSize(new Dimension(w, h));
        intFrame.pack();
        intFrame.setVisible(true);
        return intFrame;
    }
    
}
