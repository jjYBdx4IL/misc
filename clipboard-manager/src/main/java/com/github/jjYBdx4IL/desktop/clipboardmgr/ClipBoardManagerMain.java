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
package com.github.jjYBdx4IL.desktop.clipboardmgr;

import com.github.jjYBdx4IL.desktop.clipboardmgr.plugin.GpgDecryptionPlugin;
import com.github.jjYBdx4IL.desktop.clipboardmgr.plugin.MavenDependencyParserPlugin;
//import com.github.jjYBdx4IL.utils.Log4JUtils;
import com.github.jjYBdx4IL.utils.awt.ClipBoardListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JFrame;

/**
 * Main program.
 *
 * @author jjYBdx4IL
 */
public class ClipBoardManagerMain extends ClipBoardListener implements ActionListener {

    public static final String APP_NAME = "ClipBoard Manager";

    // static {
    // try {
    // File logDir = Env.provideLogDir(APP_NAME.replaceAll("\\s", ""));
    // Log4JUtils.addFileAppender(new File(logDir, "log").getAbsolutePath());
    // } catch (IOException ex) {
    // ex.printStackTrace();
    // }
    // }

    private static final Logger LOG = LoggerFactory.getLogger(ClipBoardManagerMain.class);
    public static final String MENU_ITEM_EXIT_TITLE = "Exit";
    public static final List<ClipBoardPlugin> PLUGINS = new ArrayList<>();

    private TrayIcon trayIcon = null;

    public ClipBoardManagerMain() {
    }

    /**
     * Make sure we behave like a real GUI so windows actually get placed in
     * front when requested. Probably not the way it's meant to be done but it
     * works for Win 10/amd64/zulu vm 8-181. It's possible that this has to do
     * with how Windows treats focus requests when the user has actively
     * switched to another application before this app has opened its own
     * window.
     */
    private static void fakeGui() {
        if (SystemUtils.IS_OS_WINDOWS) {
            JFrame frame = new JFrame("Main App Window");
            frame.pack();
            frame.setVisible(true);
            frame.setVisible(false);
            frame.dispose();
        }
    }

    /**
     * Main entry point.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LOG.info(APP_NAME + " " + getVersion() + " starting");
        fakeGui();
        PLUGINS.add(new GpgDecryptionPlugin());
        PLUGINS.add(new MavenDependencyParserPlugin());
        try {
            new ClipBoardManagerMain().run();
        } catch (AWTException | IOException ex) {
            LOG.error("", ex);
        }
    }

    void run() throws AWTException, IOException {
        initTray();
        start();
        while (true) {
            try {
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException ex) {
                LOG.error("", ex);
            }
        }
    }

    @Override
    public String onContentChange(String newTextContent) {
        LOG.debug(newTextContent);
        for (ClipBoardPlugin plugin : PLUGINS) {
            String processedText = plugin.onNewText(newTextContent);
            if (processedText != null) {
                return processedText;
            }
        }
        return newTextContent;
    }

    void initTray() throws AWTException, IOException {
        if (!SystemTray.isSupported()) {
            throw new RuntimeException("system tray not supported");
        }

        final SystemTray tray = SystemTray.getSystemTray();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = getClass().getResourceAsStream("CM.png")) {
            IOUtils.copy(is, baos);
        }

        Image image = Toolkit.getDefaultToolkit().createImage(baos.toByteArray());
        trayIcon = new TrayIcon(image, APP_NAME);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(APP_NAME);

        PopupMenu popupMenu = new PopupMenu("main menu");

        // exit menu entry
        MenuItem menuItem = new MenuItem();
        menuItem.setLabel(MENU_ITEM_EXIT_TITLE);
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

        trayIcon.setPopupMenu(popupMenu);

        tray.add(trayIcon);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOG.info(event.toString());
        if (ActionEvent.ACTION_PERFORMED == event.getID() && event.getActionCommand().equals(MENU_ITEM_EXIT_TITLE)) {
            SystemTray.getSystemTray().remove(trayIcon);
            System.exit(0);
        }
    }
    
    static String getVersion() {
        Properties props = new Properties();
        try (InputStream is = ClipBoardManagerMain.class.getResourceAsStream("/app.properties")) {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props.getProperty("project.version");
    }
}
