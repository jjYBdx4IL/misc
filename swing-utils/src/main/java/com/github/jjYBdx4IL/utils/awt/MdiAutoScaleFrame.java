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

import com.github.jjYBdx4IL.utils.math.LineFeedPacking;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;

//CHECKSTYLE:OFF
@SuppressWarnings("serial")
public class MdiAutoScaleFrame extends JFrame {

    final JDesktopPane desktop = new JDesktopPane();

    public MdiAutoScaleFrame(String title) {
        super(title);

        setContentPane(desktop);
        setPreferredSize(new Dimension(800, 600));

        desktop.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reLayout();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                reLayout();
            }
        });
    }
    
    protected void reLayout() {
        JInternalFrame[] frames = desktop.getAllFrames();
        int[] widths = new int[frames.length];
        int[] heights = new int[frames.length];
        for (int i = 0; i < frames.length; i++) {
            widths[i] = frames[i].getPreferredSize().width;
            heights[i] = frames[i].getPreferredSize().height;
        }
        float ratio = desktop.getWidth() * 1f / desktop.getHeight();
        LineFeedPacking lfp = new LineFeedPacking(widths, heights, ratio);
        lfp.fit();
        float scale = lfp.getOptimalSizeReductionFactor(desktop.getSize());
        List<Point> offsets = lfp.getLayoutOffsets();
        for (int i = 0; i < frames.length; i++) {
            frames[i].setLocation((int) (offsets.get(i).x * scale), (int) (offsets.get(i).y * scale));
            frames[i].setSize((int) (widths[i] * scale), (int) (heights[i] * scale));
        }
    }

}
