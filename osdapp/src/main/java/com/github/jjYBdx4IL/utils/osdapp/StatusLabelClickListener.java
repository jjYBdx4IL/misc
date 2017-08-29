/*
 * Copyright © 2014 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.osdapp;

import com.github.jjYBdx4IL.utils.awt.Desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.event.MouseEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.event.MouseInputListener;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class StatusLabelClickListener implements MouseInputListener {

    private static final Logger LOG = LoggerFactory.getLogger(StatusLabelClickListener.class);
    private final String path;
    private final URI uri;

    public StatusLabelClickListener(URL urlToOpen) throws URISyntaxException {
        this.uri = new URI(urlToOpen.toString());
        this.path = null;
    }

    public StatusLabelClickListener(String pathToOpen) {
        this.uri = null;
        this.path = pathToOpen;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (this.path != null) {
            Desktop.open(new File(path));
        } else if (this.uri != null) {
            Desktop.browse(uri);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
