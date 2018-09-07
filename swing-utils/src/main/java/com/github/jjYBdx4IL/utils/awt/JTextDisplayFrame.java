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

import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class JTextDisplayFrame extends JFrame implements KeyListener {

    private Font font = new Font("Monospaced", Font.BOLD, 16);
    private final JTextArea textArea = new JTextArea();

    /**
     * A scrollable JFrame displaying the given text. Can be closed using 'q',
     * ESC or the usual platform specific keys.
     * 
     * @param textToShow
     *            the text to display
     */
    public JTextDisplayFrame(String textToShow) {
        textArea.setText(textToShow);
        textArea.setFont(font);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getContentPane().add(scrollPane);

        textArea.addKeyListener(this);
    }

    public Font getFont() {
        return font;
    }

    public void setFont(Font font) {
        this.font = font;
        textArea.setFont(font);
    }

    @Override
    public void keyTyped(KeyEvent event) {
    }

    @Override
    public void keyReleased(KeyEvent event) {
        // don't close window when using scroll keys
        if (event.getKeyCode() == KeyEvent.VK_ESCAPE || event.getKeyCode() == KeyEvent.VK_Q) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void keyPressed(KeyEvent event) {
    }
}
