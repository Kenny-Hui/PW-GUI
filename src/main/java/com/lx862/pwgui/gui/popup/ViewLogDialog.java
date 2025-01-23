package com.lx862.pwgui.gui.popup;

import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KFileChooser;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ViewLogDialog extends JDialog {
    public ViewLogDialog(JFrame frame) {
        super(frame, Util.withTitlePrefix("View Program Log"));
        setSize(600, 400);
        setLocationRelativeTo(frame);

        JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(logTextArea);
        add(scrollPane);

        Main.LOGGER.listen(line -> {
            logTextArea.append(line + "\n");
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        });

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        KButton saveAsButton = new KButton("Save As...");
        saveAsButton.addActionListener(actionEvent -> {
            KFileChooser fileChooser = new KFileChooser();
            if (fileChooser.openSaveAsDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try(FileWriter fw = new FileWriter(file)) {
                    for(String line : Main.LOGGER.lines) {
                        fw.write(line + "\n");
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, String.format("Failed to save log:\n%s", e.getMessage()), Util.withTitlePrefix("Save Log"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        saveAsButton.setMnemonic(KeyEvent.VK_S);
        actionRowPanel.add(saveAsButton);

        KButton closeButton = new KButton("Close");
        closeButton.setMnemonic(KeyEvent.VK_C);
        closeButton.addActionListener(actionEvent -> dispose());
        actionRowPanel.add(closeButton);

        add(actionRowPanel, BorderLayout.PAGE_END);
    }
}
