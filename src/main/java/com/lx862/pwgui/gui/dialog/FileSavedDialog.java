package com.lx862.pwgui.gui.dialog;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;

public class FileSavedDialog extends JDialog {
    public FileSavedDialog(JDialog parentDialog, String title, File file) {
        super(parentDialog, Util.withTitlePrefix(title), true);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h3.font")));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(titleLabel);

        JLabel descriptionLabel = new JLabel("Your file is saved under:");
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(descriptionLabel);

        JLabel pathLabel = new JLabel(file.toPath().toString());
        pathLabel.setFont(getFont().deriveFont(Font.BOLD));
        pathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(pathLabel);

        JPanel actionRowPanel = new JPanel();
        actionRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        if(file.isFile()) {
            KButton openFileButton = new KButton("Open File");
            openFileButton.setMnemonic(KeyEvent.VK_O);
            openFileButton.addActionListener(actionEvent -> {
                Util.tryOpenFile(file);
                dispose();
            });

            actionRowPanel.add(openFileButton);
        }

        KButton openDirectoryButton = new KButton("Open Folder");
        openDirectoryButton.setMnemonic(KeyEvent.VK_F);
        openDirectoryButton.addActionListener(actionEvent -> {
            File containingDirectory = file.isDirectory() ? file : file.getParentFile();
            Util.tryOpenFile(containingDirectory);
            dispose();
        });
        actionRowPanel.add(openDirectoryButton);

        KButton copyPathButton = new KButton("Copy Path");
        copyPathButton.setMnemonic(KeyEvent.VK_C);
        copyPathButton.addActionListener(actionEvent -> {
            Util.copyToClipboard(file.toPath().toString());
            dispose();
        });
        actionRowPanel.add(copyPathButton);

        KButton finishButton = new KButton("Finish!");
        finishButton.setMnemonic(KeyEvent.VK_I);
        finishButton.addActionListener(actionEvent -> {
            dispose();
        });
        actionRowPanel.add(finishButton);

        rootPanel.add(actionRowPanel);
        add(rootPanel);
        pack();
        setLocationRelativeTo(parentDialog);
        finishButton.requestFocusInWindow(); // Default to focus on the finish button
    }
}
