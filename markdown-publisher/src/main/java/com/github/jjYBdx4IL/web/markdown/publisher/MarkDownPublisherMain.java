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

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.feed.synd.SyndFeedImpl;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
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
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.text.ParseException;
import java.util.List;
import java.util.Properties;

import javax.swing.SwingUtilities;

/**
 * Main class.
 *
 * @author jjYBdx4IL
 */
public class MarkDownPublisherMain implements ActionListener {

    private static final Logger LOG = LoggerFactory.getLogger(MarkDownPublisherMain.class);
    public static final String APP_NAME = "MarkDown Publisher";
    public static final String MENU_ITEM_CONFIG_TITLE = "Configuration";
    public static final String MENU_ITEM_EXIT_TITLE = "Exit";
    public static final int PROCESSING_TRIES = 10;
    public static final long PROCESSING_RETRY_DELAY_MS = 3000L;

    private TrayIcon trayIcon = null;
    private ConfigurationFrame configurationWindow = null;
    private final AppConfig config;

    MarkDownPublisherMain() {
        config = new AppConfig();
    }

    void run() throws FileNotFoundException, IOException, FeedException, ParseException, AWTException {
        config.read();
        initTray();

        while (true) {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Path dir = null;
            synchronized (config) {
                if (config.localMarkdownFileLocation != null) {
                    try {
                        File parentDir = new File(config.localMarkdownFileLocation).getParentFile();
                        if (parentDir != null) {
                            dir = parentDir.toPath();
                        }
                    } catch (InvalidPathException ex) {
                        LOG.error("", ex);
                    }
                }
            }
            if (dir == null) {
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    LOG.error("", ex);
                }
                LOG.debug("no watchable directory configured");
                continue;
            }
            @SuppressWarnings("unused")
            WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_MODIFY);
            boolean done = false;
            while (!done) {
                try {
                    processWatchEvents(watcher, dir);
                } catch (InterruptedException | ClosedWatchServiceException ex) {
                    done = true;
                }
            }
        }
    }

    private void processWatchEvents(WatchService watcher, Path dir) throws InterruptedException {
        LOG.debug("waiting for directory events");
        WatchKey key = watcher.take();
        Path markdownFile = new File(config.localMarkdownFileLocation).toPath();
        boolean somethingMatched = false;
        for (WatchEvent<?> event : key.pollEvents()) {
            WatchEvent.Kind<?> kind = event.kind();

            if (kind == OVERFLOW) {
                continue;
            }

            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) event;
            Path filename = ev.context();
            LOG.debug("changed file: " + filename.toString());

            if (!markdownFile.endsWith(filename)) {
                continue;
            }

            somethingMatched = true;
        }

        if (!key.reset()) {
            throw new ClosedWatchServiceException();
        }

        if (somethingMatched) {
            doProcessing();
        }
    }

    void doProcessing() {
        try {
            for (int i = 0; i < PROCESSING_TRIES; i++) {
                // wait before retry
                if (i > 0) {
                    try {
                        Thread.sleep(PROCESSING_RETRY_DELAY_MS);
                    } catch (InterruptedException ex) {
                        LOG.debug("", ex);
                    }
                }

                try {
                    doProcessingInner();
                    showMessage("Upload done.");
                    return;
                } catch (IOException ex) {
                    LOG.error("", ex);
                }
            }
        } catch (FeedException | ParseException ex) {
            showException(ex);
        }

        showError("Upload failed.");
    }

    void doProcessingInner() throws IOException, FeedException, ParseException {
        Parser parser = Parser.builder().build();
        Node document;
        try (InputStream is = new FileInputStream(config.localMarkdownFileLocation)) {
            document = parser.parse(IOUtils.toString(is, "UTF-8"));
        }

        // generate atom feed
        MarkDownVisitor visitor = new MarkDownVisitor(config.httpLocation);
        document.accept(visitor);
        visitor.close();
        String atomFeed = generateAtomFeed(visitor.getBlogTitle(), visitor.getEntries());

        // generate html
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);

        LOG.info("generated atom feed: " + atomFeed);
        LOG.info("generated html: " + html);

        URL htmlUrl = new URL(config.ftpLocation + "index.html");
        URL atomUrl = new URL(config.ftpLocation + "feed.xml");
        ftpUpload(htmlUrl, html);
        ftpUpload(atomUrl, atomFeed);
    }

    private String generateAtomFeed(String blogTitle, List<SyndEntry> entries)
        throws IOException, FeedException, ParseException {

        String feedType = "rss_2.0";

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedType);

        feed.setTitle(blogTitle);
        feed.setLink(config.httpLocation);
        feed.setDescription("");

        feed.setEntries(entries);

        try (Writer writer = new StringWriter()) {
            SyndFeedOutput output = new SyndFeedOutput();
            output.output(feed, writer, true);
            return writer.toString();
        }
    }

    private void ftpUpload(URL remoteFile, String contents) throws IOException {
        String server = remoteFile.getHost();
        int port = remoteFile.getPort();
        if (port == -1) {
            port = remoteFile.getDefaultPort();
        }

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(server, port);
            ftpClient.login(config.ftpUsername, config.ftpPassword);
            ftpClient.enterLocalPassiveMode();

            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            boolean done = false;
            try (InputStream inputStream = new ByteArrayInputStream(contents.getBytes("UTF-8"))) {
                LOG.info("Start uploading " + remoteFile);
                done = ftpClient.storeFile(remoteFile.getPath(), inputStream);
            }
            if (done) {
                LOG.info("uploaded successfully: " + remoteFile);
            } else {
                throw new IOException("FTP upload failed");
            }
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                LOG.error("", ex);
            }
        }
    }

    void initTray() throws AWTException, IOException {
        if (!SystemTray.isSupported()) {
            throw new RuntimeException("system tray not supported");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = getClass().getResourceAsStream("MP.png")) {
            IOUtils.copy(is, baos);
        }

        Image image = Toolkit.getDefaultToolkit().createImage(baos.toByteArray());
        trayIcon = new TrayIcon(image, APP_NAME);
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip(APP_NAME);

        PopupMenu popupMenu = new PopupMenu("main menu");

        // configuration menu entry
        MenuItem menuItem = new MenuItem();
        menuItem.setLabel(MENU_ITEM_CONFIG_TITLE);
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

        // exit menu entry
        menuItem = new MenuItem();
        menuItem.setLabel(MENU_ITEM_EXIT_TITLE);
        menuItem.addActionListener(this);
        popupMenu.add(menuItem);

        trayIcon.setPopupMenu(popupMenu);

        SystemTray.getSystemTray().add(trayIcon);
    }

    /**
     * Main entry point.
     * 
     * @param args
     *            the command line arguments.
     */
    public static void main(String[] args) {
        System.out.println(APP_NAME + " " + getVersion() + " starting");
        try {
            new MarkDownPublisherMain().run();
        } catch (FeedException | AWTException | IOException | ParseException ex) {
            LOG.error("", ex);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOG.info(event.toString());
        if (ActionEvent.ACTION_PERFORMED == event.getID() && event.getActionCommand().equals(MENU_ITEM_CONFIG_TITLE)) {
            showConfigurationWindow();
        }
        if (ActionEvent.ACTION_PERFORMED == event.getID() && event.getActionCommand().equals(MENU_ITEM_EXIT_TITLE)) {
            // TODO
            System.exit(0);
        }
        if (ActionEvent.ACTION_PERFORMED == event.getID()
            && event.getActionCommand().equals(ConfigurationFrame.SAVE_BUTTON_TITLE)) {
            configurationWindow.setVisible(false);
            synchronized (config) {
                configurationWindow.saveTo(config);
            }
            try {
                config.write();
            } catch (IOException ex) {
                showException(ex);
            }
        }
    }

    private boolean showException(Throwable ex) {
        LOG.error("", ex);
        return showTrayNotification(ex.getMessage(), ex.toString(), TrayIcon.MessageType.ERROR);
    }

    private void showConfigurationWindow() {
        if (configurationWindow == null) {
            configurationWindow = new ConfigurationFrame(this);
        } else if (configurationWindow.isVisible()) {
            return;
        }
        configurationWindow.pack();
        AWTUtils.centerOnScreen(AWTUtils.getMousePointerScreenDeviceIndex(), configurationWindow);
        synchronized (config) {
            configurationWindow.loadFrom(config);
        }
        configurationWindow.setVisible(true);
    }

    boolean showTrayNotification(final String line1, final String line2) {
        return showTrayNotification(line1, line2, TrayIcon.MessageType.INFO);
    }

    boolean showTrayNotification(final String line1, final String line2, final TrayIcon.MessageType type) {
        if (trayIcon == null) {
            return false;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                trayIcon.displayMessage(line1, line2, type);
            }
        });
        return true;
    }

    boolean showMessage(String text) {
        return showTrayNotification(APP_NAME, text, TrayIcon.MessageType.INFO);
    }

    boolean showError(String text) {
        return showTrayNotification(APP_NAME, text, TrayIcon.MessageType.ERROR);
    }

    static String getVersion() {
        Properties props = new Properties();
        try (InputStream is = MarkDownPublisherMain.class.getResourceAsStream("/app.properties")) {
            props.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props.getProperty("project.version");
    }
}
