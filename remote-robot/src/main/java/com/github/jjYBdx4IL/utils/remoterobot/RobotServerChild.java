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
import com.github.jjYBdx4IL.utils.remoterobot.dto.ErrorReply;
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
import com.github.jjYBdx4IL.utils.remoterobot.dto.Request;
import com.github.jjYBdx4IL.utils.remoterobot.dto.ScreenshotReply;
import com.github.jjYBdx4IL.utils.remoterobot.dto.SendInputRequest;
import com.github.jjYBdx4IL.utils.remoterobot.dto.ShutdownRequest;

import java.awt.AWTException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class RobotServerChild implements Runnable {

    private final Socket socket;
    private final Robot robot;
    private final Thread parent;

    RobotServerChild(Thread parent, Socket socket) throws AWTException {
        this.socket = socket;
        this.robot = new Robot();
        this.parent = parent;
    }

    @Override
    public void run() {
        try (
            OutputStream os = socket.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(os)) {
            // send header so the remote end can open the object input stream
            oos.flush();
            try (InputStream is = socket.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is)) {
                for (;;) {
                    Object request = ois.readObject();
                    Object reply;

                    if (request instanceof CreateScreenCaptureRequest) {
                        reply = handle((CreateScreenCaptureRequest) request);
                    } else if (request instanceof GetPixelColorRequest) {
                        reply = handle((GetPixelColorRequest) request);
                    } else if (request instanceof KeyPressRequest) {
                        reply = handle((KeyPressRequest) request);
                    } else if (request instanceof KeyReleaseRequest) {
                        reply = handle((KeyReleaseRequest) request);
                    } else if (request instanceof SendInputRequest) {
                        reply = handle((SendInputRequest) request);
                    } else if (request instanceof MousePressRequest) {
                        reply = handle((MousePressRequest) request);
                    } else if (request instanceof MouseReleaseRequest) {
                        reply = handle((MouseReleaseRequest) request);
                    } else if (request instanceof MouseMoveRequest) {
                        reply = handle((MouseMoveRequest) request);
                    } else if (request instanceof MouseWheelRequest) {
                        reply = handle((MouseWheelRequest) request);
                    } else if (request instanceof GetScreenSizeRequest) {
                        reply = handle((GetScreenSizeRequest) request);
                    } else if (request instanceof ShutdownRequest) {
                        reply = handle((ShutdownRequest) request);
                    } else if (request instanceof Request) {
                        reply = ErrorReply.create(((Request)request).seq,
                            "cannot handle type " + request.getClass().getName());
                    } else {
                        reply = ErrorReply.create(-1, "cannot handle type " + request.getClass().getName());
                    }

                    Utils.writeTo(oos, reply);
                }
            }
        } catch (EOFException ex) {
            System.out.println("Disconnected: " + socket.getRemoteSocketAddress());
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
    }

    private Object handle(ShutdownRequest request) {
        parent.interrupt();
        return Ack.create(request.seq);
    }

    private GetScreenSizeReply handle(GetScreenSizeRequest request) {
        return GetScreenSizeReply.create(request.seq, Toolkit.getDefaultToolkit().getScreenSize());
    }

    private Ack handle(MouseWheelRequest request) {
        robot.mouseWheel(request.wheelAmt);
        return Ack.create(request.seq);
    }

    private Ack handle(MouseMoveRequest request) {
        robot.mouseMove(request.x, request.y);
        return Ack.create(request.seq);
    }

    private Ack handle(MouseReleaseRequest request) {
        robot.mouseRelease(request.buttons);
        return Ack.create(request.seq);
    }

    private Ack handle(MousePressRequest request) {
        robot.mousePress(request.buttons);
        return Ack.create(request.seq);
    }

    private Ack handle(SendInputRequest request) {
        request.handle(robot);
        return Ack.create(request.seq);
    }

    private Ack handle(KeyReleaseRequest request) {
        robot.keyRelease(request.keycode);
        return Ack.create(request.seq);
    }

    private Ack handle(KeyPressRequest request) {
        robot.keyPress(request.keycode);
        return Ack.create(request.seq);
    }

    private GetPixelColorReply handle(GetPixelColorRequest request) {
        return GetPixelColorReply.create(request.seq, robot.getPixelColor(request.x, request.y));
    }

    private ScreenshotReply handle(CreateScreenCaptureRequest request) {
        Rectangle screenRect = request.screenRect;
        if (screenRect == null) {
            screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        }
        BufferedImage image = robot.createScreenCapture(screenRect);
        return ScreenshotReply.createFrom(request.seq, image);
    }
    
}
