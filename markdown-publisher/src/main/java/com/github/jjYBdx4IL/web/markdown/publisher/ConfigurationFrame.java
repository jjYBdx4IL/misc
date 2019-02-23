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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Configuration UI.
 * 
 * @author jjYBdx4IL
 */
@SuppressWarnings("serial")
public class ConfigurationFrame extends JFrame implements ActionListener {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationFrame.class);

    public static final String SAVE_BUTTON_TITLE = "Save";
    public static final String SELECT_MD_FILE_BUTTON_TITLE = "Select File";

    private final JLabel ftpLocLabel = new JLabel("FTP location (directory)");
    private final JTextField ftpLocTf = new JTextField();
    private final JLabel ftpUsernameLabel = new JLabel("FTP username");
    private final JTextField ftpUsernameTf = new JTextField();
    private final JLabel ftpPasswordLabel = new JLabel("FTP password");
    private final JPasswordField ftpPasswordPf = new JPasswordField();

    private final JLabel localMarkdownFileLocLabel = new JLabel("Local markdown file");
    private final JTextField localMarkdownFileLocTf = new JTextField();
    private final JLabel httpLocLabel = new JLabel("HTTP URL of published blog");
    private final JTextField httpLocTf = new JTextField();

    private final JButton saveButton = new JButton(SAVE_BUTTON_TITLE);
    private final JButton selectMarkdownFileButton = new JButton(SELECT_MD_FILE_BUTTON_TITLE);

    ConfigurationFrame(ActionListener saveListener) {

        localMarkdownFileLocTf.setEditable(false);

        saveButton.addActionListener(saveListener);
        selectMarkdownFileButton.addActionListener(this);

        Container contents = getContentPane();
        GridBagConstraints gbc = new GridBagConstraints();
        contents.setLayout(new GridBagLayout());

        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        contents.add(ftpLocLabel, gbc);
        gbc.gridx++;
        gbc.weightx = 1.0;
        contents.add(ftpLocTf, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        contents.add(ftpUsernameLabel, gbc);
        gbc.gridx++;
        contents.add(ftpUsernameTf, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        contents.add(ftpPasswordLabel, gbc);
        gbc.gridx++;
        contents.add(ftpPasswordPf, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        contents.add(localMarkdownFileLocLabel, gbc);
        gbc.gridx++;
        contents.add(localMarkdownFileLocTf, gbc);
        gbc.gridx++;
        contents.add(selectMarkdownFileButton, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        contents.add(httpLocLabel, gbc);
        gbc.gridx++;
        contents.add(httpLocTf, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 3;
        contents.add(saveButton, gbc);

        pack();
        Dimension dim = getSize();
        dim.setSize(dim.getWidth() * 2, dim.getHeight());
        setPreferredSize(dim);
    }

    void loadFrom(AppConfig config) {
        ftpLocTf.setText(config.ftpLocation);
        ftpUsernameTf.setText(config.ftpUsername);
        ftpPasswordPf.setText(config.ftpPassword);
        localMarkdownFileLocTf.setText(config.localMarkdownFileLocation);
        httpLocTf.setText(config.httpLocation);
    }

    void saveTo(AppConfig config) {
        config.ftpLocation = ftpLocTf.getText();
        config.ftpUsername = ftpUsernameTf.getText();
        config.ftpPassword = new String(ftpPasswordPf.getPassword());
        config.localMarkdownFileLocation = localMarkdownFileLocTf.getText();
        config.httpLocation = httpLocTf.getText();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        LOG.info(event.toString());
        if (ActionEvent.ACTION_PERFORMED == event.getID()
            && event.getActionCommand().equals(SELECT_MD_FILE_BUTTON_TITLE)) {
            JFileChooser chooser = new JFileChooser();
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                localMarkdownFileLocTf.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

}
