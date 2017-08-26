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
package com.github.jjYBdx4IL.utils.cfg;

import com.github.jjYBdx4IL.utils.awt.AWTUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * A trivial GUI to set up configuration parameters.
 * 
 * <p>
 * Example usage:
 * </p>
 * 
 * <pre>
 * {@code
 *   AtomicPropsFileSimpleGui gui = new AtomicPropsFileSimpleGui(AtomicPropsFileSimpleGuiTest.class, "config key 1",
 *                "config key 2");
 *   gui.loadOrShow(false); // asks user to configure the config params if load from disk fails or we find invalid
 *                     // config param values, throws IOException if he fails to do so.
 *                     // set the parameter to true if you want the user to edit a valid configuration.
 *   gui.get("config key 2") ...
 * }
 * </pre>
 * 
 * <p>
 * If you want to allow the user to configure the parameters without GUI, catch
 * the IOException thrown by {@link #loadOrShow(boolean)}, call {@link #saveInvalid()}
 * and use the result to tell the user which file to edit.
 * </p>
 * 
 * <p>
 * Currently, we only check whether the given config keys have values that are
 * neither null nor empty.
 * </p>
 * 
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class AtomicPropsFileSimpleGui extends JFrame implements ActionListener {

    private static final Logger LOG = LoggerFactory.getLogger(AtomicPropsFileSimpleGui.class);

    protected final AtomicPropsFile propsFile;
    private final List<JLabel> labels = new ArrayList<>();
    private final List<JTextField> textFields = new ArrayList<>();
    private final JButton saveButton = new JButton("Save");
    private volatile boolean saveButtonPressed = false;

    public AtomicPropsFileSimpleGui(Class<?> klazz, String... configKeys) {
        this(new AtomicPropsFile(klazz), configKeys);
    }

    public AtomicPropsFileSimpleGui(String relativeConfigFileName, Class<?> klazz, String... configKeys) {
        this(new AtomicPropsFile(klazz, relativeConfigFileName), configKeys);
    }

    /**
     * Constructor.
     * 
     * @param propsFile
     *            the properties file.
     * @param configKeys
     *            a subset of the configuration parameters to configure using
     *            the GUI
     */
    public AtomicPropsFileSimpleGui(AtomicPropsFile propsFile, String... configKeys) {
        super();
        this.propsFile = propsFile;

        for (String key : configKeys) {
            labels.add(new JLabel(key));
            textFields.add(new JTextField());
        }

        Container container = getContentPane();
        container.setLayout(new GridLayout(configKeys.length + 1, 2));
        for (int i = 0; i < labels.size(); i++) {
            container.add(labels.get(i));
            container.add(textFields.get(i));
        }
        container.add(new JLabel());
        container.add(saveButton);

        saveButton.addActionListener(this);

        pack();
        setPreferredSize(new Dimension((int) getSize().getWidth() * 2, (int) getSize().getHeight()));
        // setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    /**
     * Shows the configuration UI if we fail to load a proper config and waits until the user terminates it.
     * 
     * @param forceShow
     *             show the config UI even if the configuration looks good
     * @throws IOException
     *             if there was an error loading the config and the user refused
     *             to supply valid data (potentially because we are running
     *             headless)
     */
    public void loadOrShow(boolean forceShow) throws IOException {
        try {
            propsFile.load();
        } catch (IOException e) {
            LOG.warn(e.toString());
        }

        while (forceShow || !checkConfig()) {
            forceShow = false;
            saveButtonPressed = false;
            for (int i = 0; i < labels.size(); i++) {
                textFields.get(i).setText(propsFile.get(labels.get(i).getText(), ""));
            }
            if (GraphicsEnvironment.isHeadless()) {
                throw new IOException("failed to load config and cannot ask user because environment is headless");
            }
            AWTUtils.showFrameAndWaitForCloseByUser(this);
            if (!saveButtonPressed) {
                throw new IOException("failed to load config and user refused to supply valid data");
            } else if (checkConfig()) {
                propsFile.save();
            }
        }
    }

    /**
     * Save a potentially incomplete config file with the specified config keys
     * added in order to let the user manually complete it.
     * 
     * @return absolute path of the saved config file
     * @throws IOException
     *             if there was an IO error
     */
    public String saveInvalid() throws IOException {
        for (int i = 0; i < labels.size(); i++) {
            propsFile.put(labels.get(i).getText(), textFields.get(i).getText());
        }
        propsFile.save();
        return propsFile.getCfgFile().getAbsolutePath();
    }

    private boolean checkConfig() {
        for (int i = 0; i < labels.size(); i++) {
            String value = propsFile.get(labels.get(i).getText());
            if (value == null || value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        LOG.debug(ev.toString());
        if (ev.getSource() == saveButton) {
            for (int i = 0; i < labels.size(); i++) {
                propsFile.put(labels.get(i).getText(), textFields.get(i).getText());
            }
            saveButtonPressed = true;
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    public AtomicPropsFile getPropsFile() {
        return propsFile;
    }

    public String get(String configKey) {
        return propsFile.get(configKey);
    }

    public String get(String configKey, String defaultValue) {
        return propsFile.get(configKey, defaultValue);
    }

    public void put(String configKey, String value) {
        propsFile.put(configKey, value);
    }

    public void save() throws IOException {
        propsFile.save();
    }
}
