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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Usage: <pre>new ClipBoardListener() {...}.start();</pre>
 * 
 * This class is a workaround to the fact that Java does not provide a generic Clipboard listener interface.
 * (maybe because the clipboard is usually accessed via hotkeys)
 * 
 * Works by always taking ownership of the clipboard, settings its contents to the value
 * returned by {@link #onContentChange(String)} -- unless the content is not some variant of text.
 * In that case we rely on a data flavor change.
 * 
 * @author jjYBdx4IL
 */
public abstract class ClipBoardListener implements ClipboardOwner, FlavorListener {

    private static final Logger LOG = LoggerFactory.getLogger(ClipBoardListener.class);

    public ClipBoardListener() {
    }
    
    @Override
    public void lostOwnership(Clipboard c, Transferable t) {
        LOG.debug("lostOwnership() " + c);
        boolean doneWaiting = false;
        Transferable contents = null;
        while (!doneWaiting) {
            try {
                contents = c.getContents(this);
                doneWaiting = true;
            } catch (IllegalStateException ex) {
                LOG.debug("IllegalStateException while trying to READ clipboard contents, retrying...");
                try {
                    Thread.sleep(250L);
                } catch (InterruptedException ex1) {
                    LOG.error("", ex1);
                }
            }
        }
        try {
            processClipBoard(contents, c);
        } catch (Exception ex) {
            LOG.error("", ex);
        }
    }

    public void start() {
        Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
        takeOwnership(c, "");
    }
    
    void takeOwnership(final Clipboard c, final String text) {
        LOG.debug("taking clipboard ownership, setting content to: " + text);
        Transferable t = new Transferable() {

            final String data = text;

            @Override
            public DataFlavor[] getTransferDataFlavors() {
                return new DataFlavor[]{DataFlavor.stringFlavor};
            }

            @Override
            public boolean isDataFlavorSupported(DataFlavor flavor) {
                return DataFlavor.stringFlavor.equals(flavor);
            }

            @Override
            public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
                return data;
            }
        };
        while (true) {
            try {
                c.setContents(t, this);
                return;
            } catch (IllegalStateException ex) {
                LOG.debug("IllegalStateException while trying to WRITE clipboard contents, retrying...");
                try {
                    Thread.sleep(250L);
                } catch (InterruptedException ex1) {
                    LOG.error("", ex1);
                }
            }
        }
    }

    public void processClipBoard(Transferable t, Clipboard c) { //your implementation
        String tempText;
        Transferable trans = t;

        try {
            if (trans != null && trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                tempText = (String) trans.getTransferData(DataFlavor.stringFlavor);
                LOG.debug(tempText);
                final String newText = onContentChange(tempText);
                takeOwnership(c, newText);
            } else {
                LOG.debug("adding flavor listener " + this);
                c.addFlavorListener(this);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            LOG.error("", e);
        }
    }

    public abstract String onContentChange(String newTextContent);

    @Override
    public void flavorsChanged(FlavorEvent e) {
        Clipboard c = (Clipboard) e.getSource();
        LOG.debug("flavorsChanged() " + c);
        LOG.debug("removing flavor listener " + this);
        c.removeFlavorListener(this);
        Transferable trans = c.getContents(this);
        processClipBoard(trans, c);
    }

}
