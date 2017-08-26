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
package com.github.jjYBdx4IL.utils.junit4;

import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeFalse;

import com.github.jjYBdx4IL.utils.env.Maven;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

//CHECKSTYLE:OFF
/**
 *
 * @author jjYBdx4IL
 */
public class InteractiveTestBase {

    private static final Logger log = LoggerFactory.getLogger(InteractiveTestBase.class);
    public final static long INTERACTIVE_DELAY = 3000L;
    public final static String FRAME_TITLE = InteractiveTestBase.class.getName();
    private int screenshotCounter = 0;

    public static void waitForSwing() {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalThreadStateException();
        }
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                }
            });
        } catch (InterruptedException e) {
        } catch (InvocationTargetException e) {
        }
    }

    private final AtomicBoolean windowClosed = new AtomicBoolean(false);
    protected JFrame jf;
    protected JLabel label = new JLabel("loading...");
    protected boolean removeLoadingLabel = true;

    @Before
    public void beforeTest() {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    @After
    public void afterTest() throws InterruptedException, InvocationTargetException {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalThreadStateException();
        }
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                if (jf != null) {
                    log.debug("jf.dispose()");
                    if (!RDRunner.isCoSWatch()) {
                        jf.dispose();
                    }
                }
            }
        });
    }

    protected void openWindow() {
        openWindow(false);
    }

    /**
     *
     * @param reuse set to true if you want to re-use an existing presentation window
     */
    protected void openWindow(final boolean reuse) {
        waitForSwing();
        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    if (reuse) {
                        jf = null;
                        for (Frame f : Frame.getFrames()) {
                            if (FRAME_TITLE.equals(f.getTitle())) {
                                jf = (JFrame) f;
                                log.debug("openWindow(): re-using existing frame " + jf);
                            }
                        }
                        if (jf != null) {
                            setupListeners(jf);
                            removeLoadingLabel = false;
                            getContainer().removeAll();
                            jf.pack();
                            jf.setVisible(true);
                            return;
                        }
                    }
                    jf = createTestJFrame();
                    log.debug("openWindow(): created new frame " + jf);
                    jf.setAutoRequestFocus(true);
                    jf.setVisible(true);
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected void openWindowWait() throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                jf = createTestJFrame();
                jf.setVisible(true);
            }
        });
    }

    protected void waitForWindowClosingManual() throws InterruptedException {
        waitForWindowClosing(
                // single junit test running under maven/netbeans?
                System.getProperty("basedir") != null
                && System.getProperty("test", "").contains("#")
                || // single junit test running directly under eclipse?
                System.getProperty("basedir") == null
                && System.getProperty("sun.java.command").startsWith("org.eclipse.jdt.internal.junit.runner.RemoteTestRunner ")
                && System.getProperty("sun.java.command").contains(" -test ")
        );
    }

    protected void waitForWindowClosing() throws InterruptedException {
        waitForWindowClosing(false);
    }

    protected void waitForWindowClosing(boolean waitForManualClose) throws InterruptedException {
        log.debug("waitForWindowClosing()");

        if (!waitForManualClose) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (jf != null) {
                        log.debug("waitForWindowClosing(): jf.dispose()");
                        jf.dispose();
                    }
                    //                jf = null;
                }
            });
        }

//        log.info("IDE detected, waiting for user to close the window.");
        synchronized (windowClosed) {
            while (!windowClosed.get()) {
                log.debug("waitForWindowClosing(): wait for windowClosed");
                windowClosed.wait(1000l);
            }
        }
        log.debug("waitForWindowClosing(): done.");
    }

    protected void setupListeners(final JFrame jf) {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalThreadStateException();
        }

        for (WindowStateListener l : jf.getWindowStateListeners()) {
            jf.removeWindowStateListener(l);
        }
        jf.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent arg0) {
                log.info(arg0.toString());
            }
        });
        for (WindowListener l : jf.getWindowListeners()) {
            jf.removeWindowListener(l);
        }
        jf.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent arg0) {
                log.info(arg0.toString());
            }

            @Override
            public void windowIconified(WindowEvent arg0) {
                log.info(arg0.toString());
            }

            @Override
            public void windowDeiconified(WindowEvent arg0) {
                log.info(arg0.toString());
            }

            @Override
            public void windowDeactivated(WindowEvent arg0) {
                log.info(arg0.toString());
            }

            @Override
            public void windowClosing(WindowEvent arg0) {
                log.info(arg0.toString());
                log.debug("jf.dispose()");
                jf.dispose();
            }

            @Override
            public void windowClosed(WindowEvent arg0) {
                log.info(arg0.toString());
                synchronized (windowClosed) {
                    log.debug("setting windowClosed to true");
                    windowClosed.set(true);
                    log.debug("windowClosed.notifyAll()");
                    windowClosed.notifyAll();
                }
                log.debug("done: " + arg0);
            }

            @Override
            public void windowActivated(WindowEvent arg0) {
                log.info(arg0.toString());
            }
        });
        for (KeyListener l : jf.getKeyListeners()) {
            jf.removeKeyListener(l);
        }
        jf.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                log.info(e.toString());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                log.info(e.toString());
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE || e.getKeyChar() == 'q' || e.getKeyChar() == 'Q') {
                    log.debug("jf.dispose()");
                    jf.dispose();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                log.info(e.toString());
            }
        });

    }

    protected JFrame createTestJFrame() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalThreadStateException();
        }
        final JFrame jf = new JFrame(InteractiveTestBase.class.getName());

        setupListeners(jf);
        jf.setAutoRequestFocus(false);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(200, 255, 200));
        container.add(label);
        JScrollPane jsp = new JScrollPane(container);
        jf.getContentPane().add(jsp);
        jf.pack();
        jf.setLocationRelativeTo(null);
        return jf;
    }

    public static void annotate(BufferedImage img, String annotation) {
        Graphics2D g = (Graphics2D) img.getGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Font font = new Font(Font.MONOSPACED, Font.BOLD, 16);
        g.setFont(font);
        FontMetrics fm = g.getFontMetrics(font);
        Rectangle2D bounds = fm.getStringBounds(annotation, g);
        g.setColor(Color.GRAY);
        g.fillRect((int) (img.getWidth() - bounds.getWidth()) / 2, 0,
                (int) bounds.getWidth(), (int) bounds.getHeight());
        g.setColor(Color.BLACK);
        g.drawString(annotation, (int) (img.getWidth() - bounds.getWidth()) / 2, (int) Math.nextUp(-bounds.getMinY()));
    }

    protected void append(BufferedImage image, String annotation) throws InvocationTargetException, InterruptedException {
        append(image, annotation, false);
    }

    protected void append(BufferedImage image, String annotation, boolean direct) throws InvocationTargetException, InterruptedException {
        BufferedImage img = image;
        if (!direct) {
            img = deepCopy(image);
        }
        if (annotation != null) {
            annotate(img, annotation);
        }
        appendImage(img);
    }

    protected void append(BufferedImage image) throws InvocationTargetException, InterruptedException {
        append(image, null, false);
    }

    protected void append(BufferedImage image, boolean direct) throws InvocationTargetException, InterruptedException {
        append(image, null, direct);
    }

    protected void appendImage(BufferedImage image) throws InvocationTargetException, InterruptedException {
        final ImageIcon imageIcon = new ImageIcon(image);
        append(new JLabel(imageIcon));
    }

    protected void append(final Component c) throws InvocationTargetException, InterruptedException {

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                if (removeLoadingLabel) {
                    getContainer().remove(label);
                }
                getContainer().add(c);
                jf.pack();
//                if (removeLoadingLabel) {
//                    jf.setLocationRelativeTo(null);
//                }
                removeLoadingLabel = false;
            }
        });
    }

    public static BufferedImage deepCopy(BufferedImage bi) {
        ColorModel cm = bi.getColorModel();
        boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
        WritableRaster raster = bi.copyData(bi.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
    }

    protected void saveWindowAsImage(String filename) {
        screenshotCounter++;
        BufferedImage img = new BufferedImage(getContainer().getWidth(), getContainer().getHeight(), BufferedImage.TYPE_INT_ARGB);
        getContainer().paintAll(img.getGraphics());
        File f = new File(Maven.getMavenTargetDir(),
                "screenshots" + File.separator + getClass().getName() + "_" + screenshotCounter + (filename != null ? "_" + filename : "") + ".png");
        File parent = f.getParentFile();
        if (!parent.exists()) {
            assertTrue(parent.mkdirs());
        }
        log.info("saving window contents to " + f.getPath());
        try {
            assertTrue(ImageIO.write(img, "png", f));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @return the container
     */
    public JPanel getContainer() {
        return (JPanel) ((JViewport) ((JScrollPane) jf.getContentPane().getComponent(0)).getComponent(0)).getComponent(0);
    }
}
