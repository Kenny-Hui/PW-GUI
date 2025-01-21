package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;

public class FileSavedDialog extends JDialog {
    public FileSavedDialog(JDialog parentDialog, String title, File file) {
        super(parentDialog, Util.withTitlePrefix(title), true);

        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIManager.getFont("h3.font"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        JLabel descriptionLabel = new JLabel("Your file is saved under:");
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(descriptionLabel);

        JLabel pathLabel = new JLabel(file.toPath().toString());
        pathLabel.setFont(getFont().deriveFont(Font.BOLD));
        pathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(pathLabel);

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

        KButton openFolderButton = new KButton("Open Folder");
        openFolderButton.setMnemonic(KeyEvent.VK_F);
        openFolderButton.addActionListener(actionEvent -> {
            File fileOrFolder = file.isDirectory() ? file : file.getParentFile();
            Util.tryOpenFile(fileOrFolder);
            dispose();
        });
        actionRowPanel.add(openFolderButton);

        KButton copyPathButton = new KButton("Copy Path");
        copyPathButton.setMnemonic(KeyEvent.VK_C);
        copyPathButton.addActionListener(actionEvent -> {
            StringSelection stringSelection = new StringSelection(file.toPath().toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            dispose();
        });
        actionRowPanel.add(copyPathButton);

        KButton finishButton = new KButton("Finish!");
        finishButton.setMnemonic(KeyEvent.VK_I);
        finishButton.addActionListener(actionEvent -> {
            dispose();
        });
        actionRowPanel.add(finishButton);

        panel.add(actionRowPanel);
        add(panel);
        pack();
        finishButton.requestFocusInWindow(); // Default to focus on the finish button
        setLocationRelativeTo(parentDialog);
    }
}
