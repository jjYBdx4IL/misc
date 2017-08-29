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

import com.github.jjYBdx4IL.utils.awt.AWTUtils;
import com.github.jjYBdx4IL.utils.cfg.SimpleXmlAppCfg;
import com.github.jjYBdx4IL.utils.net.SSLUtils;
import com.github.jjYBdx4IL.utils.net.WakeOnLAN;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

//CHECKSTYLE:OFF
/**
 * If you need login credentials to access a nagios status page, put the
 * following file at ~/.config/java-osd/properties.xml
 *
 * <pre>
 * {@code
 * --- BEGIN SNIPPET ---
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
 * <properties>
 * <entry key="nagiosLoginName">your_login_name</entry>
 * <entry key="nagiosLoginPassword">your_password</entry>
 * </properties>
 * --- END SNIPPET ---
 * }
 * </pre>
 *
 * @author jjYBdx4IL
 */
public class OSDApp {

    private static final Logger LOG = LoggerFactory.getLogger(OSDApp.class);
    private final static Timer TIMER = new Timer();
    private final static String PROGNAME = "java-osd";
    protected final static String OPTNAME_H = "h";
    protected final static String OPTNAME_HELP = "help";
    protected final static String OPTNAME_PLUGIN_EXE = "plugin-exe";
    private static String optPluginExecutable = null;
    protected final static String OPTNAME_PLUGIN_WOL = "plugin-wol";
    private static boolean optPluginWoL = false;
    protected final static String OPTNAME_PLUGIN_IVAL = "plugin-ival";
    private final static int DEFAULT_PLUGIN_IVAL_SECONDS = 60;
    private static int optPluginIvalSeconds = DEFAULT_PLUGIN_IVAL_SECONDS;
    protected final static String OPTNAME_DISK_STANDBY = "disk-standby";
    private static String[] optDiskStandby = null;
    protected final static String OPTNAME_DISK_STANDBY_SCRPIT = "disk-standby-script";
    private static String optDiskStandbyScript = null;
    protected final static String OPTNAME_NAGIOS_URL = "nagios-url";
    protected final static String OPTNAME_NAGIOS_LOGINNAME = "nagios-login-name";
    protected final static String OPTNAME_NAGIOS_LOGINPASSWORD = "nagios-login-password";
    protected final static String CFGNAME_NAGIOS_LOGINNAME = "nagiosLoginName";
    protected final static String CFGNAME_NAGIOS_LOGINPASSWORD = "nagiosLoginPassword";
    protected final static String OPTNAME_JENKINS_URL = "jenkins-url";
    protected final static String OPTNAME_JENKINS_WOL = "jenkins-wol";
    private static boolean optJenkinsWoL = false;
    protected final static String OPTNAME_WOL = "wol";
    private static URL optNagiosUrl = null;
    private static String optNagiosLoginName = null;
    private static String optNagiosLoginPassword = null;
    private static URL optJenkinsUrl = null;
    protected final static String OPTNAME_DISKFREE = "diskfree";
    private static String[] optDiskFreePaths = null;
    protected final static String OPTNAME_NETDEV = "netdev";
    private static String[] optNetDevs = null;
    protected final static String OPTNAME_NETDEV_WOL = "netdev-wol";
    private static long optNetDevWoLRateLimit = -1L;
    protected final static String OPTNAME_ZFS = "zfs";
    private static boolean optZfs = false;
    protected final static String OPTNAME_ZFS_WOL = "zfs-wol";
    private static boolean optZfsWoL = false;
    private final static int DEFAULT_W = 300;
    private final static int DEFAULT_X = 0;
    private final static int DEFAULT_Y = 0;
    private final static String OPTNAME_X = "x";
    private final static String OPTNAME_Y = "y";
    private final static String OPTNAME_W = "w";
    private static int widgetX, widgetY, widgetW;
    private final static String OPTNAME_ORIENTATION = "orientation";
    private final static Orientation DEFAULT_ORIENTATION = Orientation.CENTER_TOP;
    private static Orientation widgetOrientation;
    private final static String OPTNAME_DISABLE_CERT_CHECKS = "disable-cert-checks";
    private static boolean disableCertChecks = false;
    private final static String LOADING_MSG = "loading...";
    private final static Color COLOR_LOADING = Color.lightGray;
    private final static int DISKFREE_POLL_IVAL_SECONDS = 10;
    private final static int NETDEV_POLL_IVAL_SECONDS = 3;
    private final static int NAGIOS_POLL_IVAL_SECONDS = 90;
    private final static int JENKINS_POLL_IVAL_SECONDS = 90;
    private final static int ZFS_POLL_IVAL_SECONDS = 90;
    private final static int DISK_STANDBY_POLL_IVAL = 90;
    private final static int VERTICAL_PADDING = 2;
    private final static String OPTNAME_SHOW_BORDER = "show-border";
    private static boolean optShowBorder = false;
    private static WakeOnLAN wol = null;

    private final static Map<DisplayLevel, Image> TRAY_IMAGES = new HashMap<>();
    private static TrayIcon trayIcon;
    private static DisplayLevel displayedTrayLevel;

    public static void main(String[] args)
        throws InterruptedException, MalformedURLException, SocketException, UnknownHostException {
        try {
            // first, load the app configuration - command line options will
            // override config file options
            SimpleXmlAppCfg.loadConfiguration(PROGNAME);
            Properties appCfg = SimpleXmlAppCfg.getConfiguration();
            optNagiosLoginName = appCfg.getProperty(CFGNAME_NAGIOS_LOGINNAME);
            optNagiosLoginPassword = appCfg.getProperty(CFGNAME_NAGIOS_LOGINPASSWORD);
        } catch (IOException ex) {
            LOG.error("", ex);
        }

        // create the command line parser
        CommandLineParser parser = new GnuParser();

        // create the Options
        Options options = new Options();
        options.addOption(null, OPTNAME_PLUGIN_EXE, true,
            "executable to use as plugin: exit codes 0+1/2+3/4+ indicate OK/WARN/ERROR, "
                + "STDOUT will be printed to the OSD bar");
        options.addOption(null, OPTNAME_PLUGIN_IVAL, true,
            "period length (in seconds) for executing the plugin executable");
        options.addOption(null, OPTNAME_PLUGIN_WOL, false, "send wake-on-lan ping for odd plugin exit codes");
        options.addOption(null, OPTNAME_DISKFREE, true, "show free disk space for given paths");
        options.addOption(null, OPTNAME_NETDEV, true, "show network traffic for given network interfaces");
        options.addOption(null, OPTNAME_NETDEV_WOL, true, "send wake-on-lan ping when an interface shows "
            + "activity above this many bytes per second");
        options.addOption(null, OPTNAME_NAGIOS_URL, true, "pointer to nagios status page, ie."
            + " http://localhost/nagios3/cgi-bin/status.cgi?host=all");
        options.addOption(null, OPTNAME_JENKINS_URL, true, "pointer to jenkins www frontend/view, without "
            + "/api/xml suffix");
        options.addOption(null, OPTNAME_JENKINS_WOL, false, "send wake-on-lan ping when jenkins is active");
        options.addOption(null, OPTNAME_WOL, true, "wake-on-lan target to ping; "
            + "use this with powernap to prevent suspend. Either interface name \"eth0\" or "
            + "\"ipaddr,macaddr\".");
        options.addOption(null, OPTNAME_ZFS, false, "show zfs zpool status, requires sudo entry for \"zpool status\"");
        options.addOption(null, OPTNAME_ZFS_WOL, false,
            "send wake-on-lan ping when a zpool is scrubbing or resilvering");
        options.addOption(null, OPTNAME_DISK_STANDBY, true, "argument is of the form \"sda,5\"; watches for disk "
            + "inactivity and puts the disk to sleep after 5 minutes. Requires sudo entry for \""
            + DiskStandbyPoller.getCommand("sda") + "\".");
        options.addOption(null, OPTNAME_DISK_STANDBY_SCRPIT, true, "sudo-execute this script instead of hdparm");
        options.addOption(null, OPTNAME_ORIENTATION, true,
            "choose one of: {LEFT,CENTER,RIGHT,X}_{TOP,MIDDLE,BOTTOM,Y}.");
        options.addOption(OPTNAME_X, true, "x position of OSD");
        options.addOption(OPTNAME_Y, true, "y position of OSD");
        options.addOption(OPTNAME_W, true, "width of OSD");
        options.addOption(OPTNAME_H, OPTNAME_HELP, false, "show help (this page)");
        options.addOption(null, OPTNAME_DISABLE_CERT_CHECKS, false, "disable certificate checks");
        options.addOption(null, OPTNAME_SHOW_BORDER, false, "show popup border");

        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption(OPTNAME_H)) {
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp(PROGNAME, options);
                System.exit(0);
            }
            widgetW = line.hasOption(OPTNAME_W) ? Integer.parseInt(line.getOptionValue(OPTNAME_W)) : DEFAULT_W;
            widgetX = line.hasOption(OPTNAME_X) ? Integer.parseInt(line.getOptionValue(OPTNAME_X)) : DEFAULT_X;
            widgetY = line.hasOption(OPTNAME_Y) ? Integer.parseInt(line.getOptionValue(OPTNAME_Y)) : DEFAULT_Y;
            optPluginExecutable = line.getOptionValue(OPTNAME_PLUGIN_EXE);
            optPluginIvalSeconds = line.hasOption(OPTNAME_PLUGIN_IVAL)
                ? Integer.parseInt(line.getOptionValue(OPTNAME_PLUGIN_IVAL))
                : DEFAULT_PLUGIN_IVAL_SECONDS;
            if (optPluginIvalSeconds < 1) {
                throw new ParseException("bad --" + OPTNAME_PLUGIN_IVAL + " value");
            }
            optPluginWoL = line.hasOption(OPTNAME_PLUGIN_WOL);
            try {
                widgetOrientation = line.hasOption(OPTNAME_ORIENTATION)
                    ? Orientation.valueOf(line.getOptionValue(OPTNAME_ORIENTATION))
                    : DEFAULT_ORIENTATION;
            } catch (IllegalArgumentException ex) {
                throw new ParseException("bad --" + OPTNAME_ORIENTATION + " value");
            }
            optDiskFreePaths = line.getOptionValues(OPTNAME_DISKFREE);
            optNetDevs = line.getOptionValues(OPTNAME_NETDEV);
            try {
                optNetDevWoLRateLimit = line.hasOption(OPTNAME_NETDEV_WOL)
                    ? Integer.parseInt(line.getOptionValue(OPTNAME_NETDEV_WOL))
                    : optNetDevWoLRateLimit;
            } catch (NumberFormatException ex) {
                throw new ParseException("bad --" + OPTNAME_NETDEV_WOL + " value");
            }
            optNagiosUrl = line.hasOption(OPTNAME_NAGIOS_URL) ? new URL(line.getOptionValue(OPTNAME_NAGIOS_URL))
                : optNagiosUrl;
            optJenkinsUrl = line.hasOption(OPTNAME_JENKINS_URL) ? new URL(line.getOptionValue(OPTNAME_JENKINS_URL))
                : optJenkinsUrl;
            optJenkinsWoL = line.hasOption(OPTNAME_JENKINS_WOL);
            if (line.hasOption(OPTNAME_WOL)) {
                String[] wolArgs = line.getOptionValue(OPTNAME_WOL).split(",");
                try {
                    if (wolArgs.length == 1) {
                        wol = new WakeOnLAN(wolArgs[0]);
                    } else if (wolArgs.length == 2) {
                        wol = new WakeOnLAN(wolArgs[0], wolArgs[1]);
                    }
                } catch (SocketException | UnknownHostException ex) {
                    System.err.println(ex.getMessage());
                }
                if (wol == null) {
                    throw new IllegalArgumentException("failed to parse argument to --" + OPTNAME_WOL);
                }
            }
            optZfs = line.hasOption(OPTNAME_ZFS);
            optZfsWoL = line.hasOption(OPTNAME_ZFS_WOL);
            optDiskStandby = line.getOptionValues(OPTNAME_DISK_STANDBY);
            optDiskStandbyScript = line.getOptionValue(OPTNAME_DISK_STANDBY_SCRPIT);
            disableCertChecks = line.hasOption(OPTNAME_DISABLE_CERT_CHECKS);
            optShowBorder = line.hasOption(OPTNAME_SHOW_BORDER);
        } catch (ParseException exp) {
            System.err.println("Failed to parse the command line:" + exp.getMessage());
            System.exit(1);
        }

        if (disableCertChecks) {
            try {
                SSLUtils.disableCertChecks();
            } catch (NoSuchAlgorithmException | KeyManagementException ex) {
                LOG.error("failed to disable certificate checks", ex);
            }
        }

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    new OSDApp().display();
                } catch (URISyntaxException | MalformedURLException | AWTException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private static Image createImage(Color color) {
        Dimension trayIconSize = SystemTray.getSystemTray().getTrayIconSize();
        final String text = "OSD";
        BufferedImage img = new BufferedImage(trayIconSize.width, trayIconSize.height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = (Graphics2D) img.getGraphics();
        Font font = new Font("Dialog", Font.PLAIN, 12);
        FontMetrics fm = g.getFontMetrics(font);
        Color alpha = new Color(0, 0, 0, 255);
        g.setColor(alpha);
        g.fillRect(0, 0, trayIconSize.width, trayIconSize.height);
        g.setColor(color);
        g.setFont(font);
        g.drawString(text, 0, (trayIconSize.height - fm.getHeight()) / 2 + fm.getAscent());
        return img;
    }

    protected static void setTrayLevel(DisplayLevel trayLevel) {
        if (!SystemTray.isSupported()) {
            return;
        }
        if (trayLevel.equals(displayedTrayLevel)) {
            return;
        }
        trayIcon.setImage(TRAY_IMAGES.get(trayLevel));
        displayedTrayLevel = trayLevel;
    }

    private JLabel[] diskFreeStatusDisplayLabels;
    private JLabel[] netDevStatusDisplayLabels;
    private JLabel nagiosStatusDisplayLabel;
    private JLabel jenkinsStatusDisplayLabel;
    private JLabel zfsStatusDisplayLabel;
    private JLabel diskStandbyLabel;
    private JLabel pluginLabel;
    private JFrame f;
    private JPanel p;

    private void startTimers() {
        try {
            if (optDiskFreePaths != null) {
                for (int i = 0; i < optDiskFreePaths.length; i++) {
                    // CHECKSTYLE IGNORE MagicNumber FOR NEXT 2 LINES
                    TIMER.schedule(new DiskFreePoller(optDiskFreePaths[i], diskFreeStatusDisplayLabels[i]),
                        0, DISKFREE_POLL_IVAL_SECONDS * 1000L);
                }
            }
            if (optNetDevs != null) {
                for (int i = 0; i < optNetDevs.length; i++) {
                    // CHECKSTYLE IGNORE MagicNumber FOR NEXT 2 LINES
                    TIMER.schedule(
                        new NetDevPoller(optNetDevs[i], optNetDevWoLRateLimit, optNetDevWoLRateLimit >= 0 ? wol : null,
                            netDevStatusDisplayLabels[i]),
                        0, NETDEV_POLL_IVAL_SECONDS * 1000L);
                }
            }
            if (optNagiosUrl != null) {
                // NOTE: Authenticator.setDefault() is used to supply
                // credentials to the URL object.
                // Therefore all URL objects should run inside the same timer
                // thread...
                // CHECKSTYLE IGNORE MagicNumber FOR NEXT 3 LINES
                TIMER.schedule(new NagiosPoller(optNagiosUrl, optNagiosLoginName, optNagiosLoginPassword,
                    nagiosStatusDisplayLabel),
                    0, NAGIOS_POLL_IVAL_SECONDS * 1000L);
            }
            if (optJenkinsUrl != null) {
                // CHECKSTYLE IGNORE MagicNumber FOR NEXT 2 LINES
                TIMER.schedule(new JenkinsPoller(optJenkinsUrl, optJenkinsWoL ? wol : null, jenkinsStatusDisplayLabel),
                    0, JENKINS_POLL_IVAL_SECONDS * 1000L);
            }
            if (optZfs) {
                // CHECKSTYLE IGNORE MagicNumber FOR NEXT 2 LINES
                TIMER.schedule(new ZFSPoller(optZfsWoL ? wol : null, zfsStatusDisplayLabel),
                    0, ZFS_POLL_IVAL_SECONDS * 1000L);
            }
            if (optDiskStandby != null) {
                TIMER.schedule(new DiskStandbyPoller(optDiskStandby, optDiskStandbyScript, diskStandbyLabel),
                    0, DISK_STANDBY_POLL_IVAL * 1000L);
            }
            if (optPluginExecutable != null) {
                TIMER.schedule(new PluginPoller(optPluginExecutable, optPluginWoL ? wol : null, pluginLabel),
                    0, optPluginIvalSeconds * 1000L);
            }
        } catch (MalformedURLException | UnknownHostException | SocketException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void display() throws MalformedURLException, URISyntaxException, AWTException {
        if (SystemTray.isSupported()) {
            displayTrayIcon();
        }
        displayOSD();

        // start the timers *after* we have initialized the graphics elements
        startTimers();
    }

    private void displayTrayIcon() throws AWTException {
        for (DisplayLevel level : DisplayLevel.values()) {
            TRAY_IMAGES.put(level, createImage(level.getColor()));
        }
        displayedTrayLevel = DisplayLevel.OK;
        trayIcon = new TrayIcon(TRAY_IMAGES.get(displayedTrayLevel));

        PopupMenu popupMenu = new PopupMenu("Tray Menu");

        MenuItem menuItem = new MenuItem();
        menuItem.setLabel("Toggle Visibility");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("", e);
                f.setVisible(!f.isVisible());
            }
        });
        popupMenu.add(menuItem);

        menuItem = new MenuItem();
        menuItem.setLabel("About");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("", e);
                AWTUtils.showInfoDialogOnMouseScreen(PROGNAME + " - About", "line 1\nline 2\nline 3");
            }
        });
        popupMenu.add(menuItem);

        menuItem = new MenuItem();
        menuItem.setLabel("Exit");
        menuItem.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                LOG.info("", e);
                if (AWTUtils.askForConfirmationOnMouseScreen(PROGNAME, "Really exit?")) {
                    System.exit(0);
                }
            }
        });
        popupMenu.add(menuItem);

        trayIcon.setPopupMenu(popupMenu);
        SystemTray.getSystemTray().add(trayIcon);
    }

    private void displayOSD() throws MalformedURLException, URISyntaxException {
        f = new JFrame();
        f.setType(Window.Type.POPUP);
        f.setAlwaysOnTop(true);
        f.setAutoRequestFocus(false);
        f.setUndecorated(true);
        p = new JPanel();
        if (optShowBorder) {
            p.setBorder(BorderFactory.createLineBorder(Color.lightGray));
        }
        // CHECKSTYLE IGNORE MagicNumber FOR NEXT 1 LINE
        FlowLayout flow = new FlowLayout(FlowLayout.CENTER, 5, 0);
        p.setLayout(flow);
        p.setBackground(Color.black);
        f.add(p);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        LOG.debug("" + ge);

        GraphicsDevice defaultScreen = ge.getDefaultScreenDevice();
        LOG.debug("" + defaultScreen);

        GraphicsConfiguration gc = defaultScreen.getDefaultConfiguration();
        LOG.debug("" + gc);

        Rectangle rect = gc.getBounds();
        LOG.debug("" + rect);

        f.pack();
        FontMetrics fm = f.getGraphics().getFontMetrics();

        f.setPreferredSize(new Dimension(widgetW, fm.getAscent() + fm.getDescent() + 2 * VERTICAL_PADDING));
        f.pack();

        int x = widgetX;
        if (x < 0) {
            x += rect.getMaxX();
        }

        int y = widgetY;
        if (y < 0) {
            y += rect.getMaxY();
        }

        switch (widgetOrientation) {
            case LEFT_BOTTOM:
            case LEFT_MIDDLE:
            case LEFT_TOP:
            case LEFT_Y:
                x = 0;
                break;
            case CENTER_BOTTOM:
            case CENTER_MIDDLE:
            case CENTER_TOP:
            case CENTER_Y:
                x = ((int) rect.getMaxX() - f.getWidth()) / 2;
                break;
            case RIGHT_BOTTOM:
            case RIGHT_MIDDLE:
            case RIGHT_TOP:
            case RIGHT_Y:
                x = (int) rect.getMaxX() - f.getWidth();
                break;
            case X_BOTTOM:
            case X_MIDDLE:
            case X_TOP:
            case X_Y:
            default:
        }

        switch (widgetOrientation) {
            case CENTER_TOP:
            case LEFT_TOP:
            case RIGHT_TOP:
            case X_TOP:
                y = 0;
                break;
            case CENTER_MIDDLE:
            case LEFT_MIDDLE:
            case RIGHT_MIDDLE:
            case X_MIDDLE:
                y = ((int) rect.getMaxY() - f.getHeight()) / 2;
                break;
            case CENTER_BOTTOM:
            case LEFT_BOTTOM:
            case RIGHT_BOTTOM:
            case X_BOTTOM:
                y = (int) rect.getMaxY() - f.getHeight();
                break;
            case LEFT_Y:
            case CENTER_Y:
            case RIGHT_Y:
            case X_Y:
            default:
        }

        if (optDiskFreePaths != null) {
            diskFreeStatusDisplayLabels = new JLabel[optDiskFreePaths.length];
            for (int i = 0; i < optDiskFreePaths.length; i++) {
                diskFreeStatusDisplayLabels[i] = new JLabel();
                diskFreeStatusDisplayLabels[i].setText(LOADING_MSG);
                diskFreeStatusDisplayLabels[i].setForeground(COLOR_LOADING);
                diskFreeStatusDisplayLabels[i].addMouseListener(
                    new StatusLabelClickListener(optDiskFreePaths[i]));
                p.add(diskFreeStatusDisplayLabels[i]);
            }
        }

        if (optNetDevs != null) {
            netDevStatusDisplayLabels = new JLabel[optNetDevs.length];
            for (int i = 0; i < optNetDevs.length; i++) {
                netDevStatusDisplayLabels[i] = new JLabel();
                netDevStatusDisplayLabels[i].setText(LOADING_MSG);
                netDevStatusDisplayLabels[i].setForeground(COLOR_LOADING);
                p.add(netDevStatusDisplayLabels[i]);
            }
        }

        if (optNagiosUrl != null) {
            nagiosStatusDisplayLabel = new JLabel();
            nagiosStatusDisplayLabel.setText(LOADING_MSG);
            nagiosStatusDisplayLabel.setForeground(COLOR_LOADING);
            nagiosStatusDisplayLabel.addMouseListener(new StatusLabelClickListener(optNagiosUrl));
            p.add(nagiosStatusDisplayLabel);
        }

        if (optJenkinsUrl != null) {
            jenkinsStatusDisplayLabel = new JLabel();
            jenkinsStatusDisplayLabel.setText(LOADING_MSG);
            jenkinsStatusDisplayLabel.setForeground(COLOR_LOADING);
            jenkinsStatusDisplayLabel.addMouseListener(new StatusLabelClickListener(optJenkinsUrl));
            p.add(jenkinsStatusDisplayLabel);
        }

        if (optZfs) {
            zfsStatusDisplayLabel = new JLabel();
            zfsStatusDisplayLabel.setText(LOADING_MSG);
            zfsStatusDisplayLabel.setForeground(COLOR_LOADING);
            p.add(zfsStatusDisplayLabel);
        }

        if (optDiskStandby != null) {
            diskStandbyLabel = new JLabel();
            diskStandbyLabel.setText(LOADING_MSG);
            diskStandbyLabel.setForeground(COLOR_LOADING);
            p.add(diskStandbyLabel);
        }

        if (optPluginExecutable != null) {
            pluginLabel = new JLabel();
            pluginLabel.setText(LOADING_MSG);
            pluginLabel.setForeground(COLOR_LOADING);
            p.add(pluginLabel);
        }

        f.pack();
        f.setLocation(x, y);
        f.setVisible(true);
    }

}
