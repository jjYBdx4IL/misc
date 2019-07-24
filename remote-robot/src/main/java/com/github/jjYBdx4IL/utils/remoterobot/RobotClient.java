/*
 * Copyright © 2019 jjYBdx4IL (https://github.com/jjYBdx4IL)
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
package com.github.jjYBdx4IL.utils.remoterobot;

import com.github.jjYBdx4IL.utils.remoterobot.dto.Ack;
import com.github.jjYBdx4IL.utils.remoterobot.dto.CreateScreenCaptureRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.GetPixelColorReply;
import com.github.jjYBdx4IL.utils.remoterobot.dto.GetPixelColorRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.GetScreenSizeReply;
import com.github.jjYBdx4IL.utils.remoterobot.dto.GetScreenSizeRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.KeyPressRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.KeyReleaseRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.MouseMoveRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.MousePressRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.MouseReleaseRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.MouseWheelRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.ScreenshotReply;
import com.github.jjYBdx4IL.utils.remoterobot.dto.SendInputRequest;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class RobotClient implements AutoCloseable {

    public static final long DEFAULT_SEND_INPUT_DELAY_MS = 10;

    private RobotClient() {
    }

    private ObjectInputStream ois = null;
    private ObjectOutputStream oos = null;
    private InputStream is = null;
    private OutputStream os = null;
    private Socket socket = null;
    // request sequence id
    private long seq = -1;

    // for local operation: don't require a server
    private Robot localRobot = null;

    private long sendInputDelayMs = DEFAULT_SEND_INPUT_DELAY_MS;
    private long autoDelayMs = 0;
    private long lastInputEventMs = 0;

    /**
     * Create a robot client and connect it to a remote robot server.
     * 
     * @param serverAddress
     *            the remote server's address
     * @param serverPort
     *            the remote server's port
     * @return the client
     * @throws IOException
     *             on error
     */
    public static RobotClient connect(InetAddress serverAddress, int serverPort) throws IOException {
        RobotClient client = new RobotClient();
        if (serverAddress == null) {
            try {
                client.localRobot = new Robot();
            } catch (AWTException e) {
                throw new IOException(e);
            }
        } else {
            client.socket = new Socket(serverAddress, serverPort);
            client.open();
        }
        return client;
    }

    private void open() throws IOException {
        os = socket.getOutputStream();
        oos = new ObjectOutputStream(os);
        // send object stream header
        oos.flush();
        is = socket.getInputStream();
        // this will block until the server sends us the object stream header
        // (and that's why we open it after the output):
        ois = new ObjectInputStream(is);
    }
    
    public boolean isLocal() {
        return socket == null;
    }

    @Override
    public void close() throws IOException {
        IOException ex = null;
        for (Closeable closeable : new Closeable[] { oos, os, is, ois, socket }) {
            try {
                if (closeable != null) {
                    closeable.close();
                }
            } catch (IOException e) {
                if (ex != null) {
                    e.addSuppressed(ex);
                }
                ex = e;
            }
        }
        if (ex != null) {
            throw ex;
        }
        System.out.println("Client closed.");
    }

    /**
     * Send mouse move event.
     * 
     * @param x
     *            x-coordinate of target
     * @param y
     *            y-coordinate of target
     * @throws IOException
     *             on error
     */
    public void mouseMove(int x, int y) throws IOException {
        autoDelay(false);

        if (localRobot != null) {
            localRobot.mouseMove(x, y);
        } else {
            Utils.writeTo(oos, MouseMoveRequest.create(++seq, x, y));
            readAck();
        }

        autoDelay(true);
    }

    /**
     * Convenience method for {@link #mouseMove(int, int)}.
     * 
     * @param p
     *            where to move
     * @throws IOException
     *             on error
     */
    public void mouseMove(Point p) throws IOException {
        mouseMove(p.x, p.y);
    }

    /**
     * Send mouse wheel event.
     * 
     * @param wheelAmt
     *            the wheel amount
     * @throws IOException
     *             on error
     */
    public void mouseWheel(int wheelAmt) throws IOException {
        autoDelay(false);

        if (localRobot != null) {
            localRobot.mouseWheel(wheelAmt);
        } else {
            Utils.writeTo(oos, MouseWheelRequest.create(++seq, wheelAmt));
            readAck();
        }

        autoDelay(true);
    }

    /**
     * Send mouse button press event.
     * 
     * @param button
     *            the button
     * @throws IOException
     *             on error
     */
    public void mousePress(MouseButton button) throws IOException {
        autoDelay(false);

        if (localRobot != null) {
            localRobot.mousePress(button.pressCode);
        } else {
            Utils.writeTo(oos, MousePressRequest.create(++seq, button.pressCode));
            readAck();
        }

        autoDelay(true);
    }

    /**
     * Send mouse button release event.
     * 
     * @param button
     *            the button
     * @throws IOException
     *             on error
     */
    public void mouseRelease(MouseButton button) throws IOException {
        autoDelay(false);

        if (localRobot != null) {
            localRobot.mouseRelease(button.releaseCode);
        } else {
            Utils.writeTo(oos, MouseReleaseRequest.create(++seq, button.releaseCode));
            readAck();
        }

        autoDelay(true);
    }

    /**
     * Send key press event.
     * 
     * @param keycode
     *            the key
     * @throws IOException
     *             on error
     */
    public void keyPress(int keycode) throws IOException {
        autoDelay(false);

        if (localRobot != null) {
            localRobot.keyPress(keycode);
        } else {
            Utils.writeTo(oos, KeyPressRequest.create(++seq, keycode));
            readAck();
        }

        autoDelay(true);
    }

    /**
     * Send key release event.
     * 
     * @param keycode
     *            the key
     * @throws IOException
     *             on error
     */
    public void keyRelease(int keycode) throws IOException {
        autoDelay(false);

        if (localRobot != null) {
            localRobot.keyRelease(keycode);
        } else {
            Utils.writeTo(oos, KeyReleaseRequest.create(++seq, keycode));
            readAck();
        }

        autoDelay(true);
    }

    /**
     * Convenience method to send a sequence of input events. Multiple
     * occurrences of the same keycode will alternate between press and release
     * events. Keys that appear an uneven times will get released implicitly.
     * ALT, CONTROL, SHIFT keys will be released last.
     * 
     * @param delayMs
     *            the delay between the key events
     * @param keycodes
     *            the keys to press
     * @throws IOException
     *             on error
     */
    public void sendInput(long delayMs, int... keycodes) throws IOException {
        autoDelay(false);

        SendInputRequest request = SendInputRequest.create(++seq, delayMs, keycodes);

        if (localRobot != null) {
            request.handle(localRobot);
        } else {
            Utils.writeTo(oos, request);
            readAck();
        }

        autoDelay(true);
    }

    /**
     * Convenience method for {@link #sendInput(long, int...)} - uses
     * {@link #DEFAULT_SEND_INPUT_DELAY_MS} as the delayMs value.
     * 
     * @see #sendInput(long,int...)
     * @param keycodes
     *            the keys to press
     * @throws IOException
     *             on error
     */
    public void sendInput(int... keycodes) throws IOException {
        sendInput(sendInputDelayMs, keycodes);
    }

    /**
     * Get pixel color.
     * 
     * @param x
     *            x-coordinate of the pixel
     * @param y
     *            y-coordinate of the pixel
     * @return the pixel's color
     * @throws IOException
     *             on error
     */
    public Color getPixelColor(int x, int y) throws IOException {
        autoDelay(false);

        Color color;

        if (localRobot != null) {
            color = localRobot.getPixelColor(x, y);
        } else {
            Utils.writeTo(oos, GetPixelColorRequest.create(++seq, x, y));
            GetPixelColorReply reply;
            try {
                reply = (GetPixelColorReply) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
            verifyAck(reply);
            color = reply.color;
        }

        return color;
    }

    /**
     * Get screen size.
     * 
     * @return the screen size
     * @throws IOException
     *             on error
     */
    public Dimension getScreenSize() throws IOException {
        Dimension size;

        if (localRobot != null) {
            size = Toolkit.getDefaultToolkit().getScreenSize();
        } else {
            Utils.writeTo(oos, GetScreenSizeRequest.create(++seq));
            GetScreenSizeReply reply;
            try {
                reply = (GetScreenSizeReply) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
            verifyAck(reply);
            size = reply.dimension;
        }

        return size;
    }

    /**
     * Take a screenshot.
     * 
     * @param screenRect
     *            the screen region, null for entire screen
     * @return the screenshot
     * @throws IOException
     *             on error
     */
    public BufferedImage createScreenCapture(Rectangle screenRect) throws IOException {
        if (screenRect != null && (screenRect.width < 1 || screenRect.height < 1)) {
            throw new IllegalArgumentException("invalid screenRect");
        }

        autoDelay(false);

        BufferedImage capture;

        if (localRobot != null) {
            if (screenRect == null) {
                screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            }
            capture = localRobot.createScreenCapture(screenRect);
        } else {
            Utils.writeTo(oos, CreateScreenCaptureRequest.create(++seq, screenRect));
            ScreenshotReply reply;
            try {
                reply = (ScreenshotReply) ois.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException(e);
            }
            verifyAck(reply);
            capture = reply.toBufferedImage();
        }

        return capture;
    }

    private void readAck() throws IOException {
        Ack ack;
        try {
            ack = (Ack) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
        verifyAck(ack);
    }

    private void verifyAck(Ack ack) throws IOException {
        if (ack.seq != seq) {
            throw new IOException("wrong sequence id returned by server");
        }
    }

    /**
     * Get the autodelay.
     * 
     * @return delay in ms
     */
    public long getAutoDelayMs() {
        return autoDelayMs;
    }

    /**
     * Set the auto-delay, which is used to delay sequential events that might
     * depend on previous ones (ie. right-clicking and waiting for the context
     * menu to pop up).
     * 
     * @param autoDelayMs
     *            the delay in ms, default 0
     */
    public void setAutoDelayMs(long autoDelayMs) {
        this.autoDelayMs = autoDelayMs;
    }

    private void autoDelay(boolean delayNextEvent) {
        long now = System.currentTimeMillis();
        if (delayNextEvent) {
            lastInputEventMs = now;
        }
        if (autoDelayMs <= 0) {
            return;
        }
        long remainingDelayMs = lastInputEventMs + autoDelayMs - now;
        if (remainingDelayMs <= 0) {
            return;
        }
        Utils.delay(remainingDelayMs);
    }
    
    public long getSendInputDelayMs() {
        return sendInputDelayMs;
    }

    public void setSendInputDelayMs(long sendInputDelayMs) {
        this.sendInputDelayMs = sendInputDelayMs;
    }

}
