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

import java.awt.AWTException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class RobotServer implements Runnable, AutoCloseable {

    public static int DEFAULT_PORT = 7654;

    private Set<Thread> childs = new HashSet<>();
    private int port = -1;
    private CountDownLatch startupLatch = new CountDownLatch(1);
    private CountDownLatch shutdownLatch = new CountDownLatch(1);
    private volatile boolean shutdownRequested = false;
    private ServerSocket serverSocket = null;
    private Thread serverThread = null;

    /**
     * The main method.
     * 
     * @param args
     *            port number, default 7654, 0 for automatic selection
     * @throws Exception
     *             on error
     */
    public static void main(String[] args) throws Exception {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        try (RobotServer server = new RobotServer(port)) {
            server.start();
            server.join();
        }
    }

    public RobotServer(int port) {
        this.port = port;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);

            System.out.println("Listening at: " + serverSocket.getLocalSocketAddress());

            startupLatch.countDown();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Connected: " + clientSocket.getRemoteSocketAddress());
                Thread child = new Thread(new RobotServerChild(Thread.currentThread(), clientSocket),
                    "RobotServerChild");
                childs.add(child);
                child.start();

                cleanUpThreads();
            }
        } catch (IOException e) {
            // Shutdown is done by closing the server socket, so we always get
            // an exception on shutdown. Suppress it if the shutdown was
            // requested.
            if (!shutdownRequested) {
                e.printStackTrace();
            }
        } catch (AWTException e) {
            e.printStackTrace();
        } finally {
            System.out.println("Server shutting down...");
            try {
                close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            for (Thread child : childs) {
                if (child.isAlive()) {
                    child.interrupt();
                }
                try {
                    child.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            shutdownLatch.countDown();
            System.out.println("Server down and out.");
        }
    }

    private void cleanUpThreads() {
        List<Thread> toBeRemoved = new ArrayList<>();
        for (Thread child : childs) {
            if (!child.isAlive()) {
                try {
                    child.join();
                    toBeRemoved.add(child);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        for (Thread t : toBeRemoved) {
            childs.remove(t);
        }
    }

    public int getPort() {
        return serverSocket.getLocalPort();
    }

    public void shutdown() throws Exception {
        shutdownRequested = true;
        close();
    }

    @Override
    public void close() throws Exception {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
        }
    }

    public void shutdownWait() throws Exception {
        shutdown();
        shutdownLatch.await();
    }

    public void waitStarted() throws InterruptedException {
        startupLatch.await();
    }

    public void start() {
        serverThread = new Thread(this, "RobotServer");
        serverThread.start();
    }

    public void join() throws InterruptedException {
        serverThread.join();
    }
}
