package com.lx862.pwgui.gui.popup;

import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.core.Logger;
import com.lx862.pwgui.gui.action.CloseWindowAction;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.function.Consumer;

/** Dialog to view the program's log */
public class ViewLogDialog extends JDialog {
    private final Logger.LogCallback appendLogCallback;

    public ViewLogDialog(JFrame frame) {
        super(frame, Util.withTitlePrefix("View Program Log"));

        setSize(600, 400);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextArea logTextArea = new JTextArea();
        logTextArea.setEditable(false);
        JScrollPane logTextAreaScrollPane = new JScrollPane(logTextArea);
        add(logTextAreaScrollPane);

        this.appendLogCallback = (line, realtime) -> {
            logTextArea.append(line + "\n");
            logTextAreaScrollPane.getVerticalScrollBar().setValue(logTextAreaScrollPane.getVerticalScrollBar().getMaximum()); // Jump to bottom
        };

        PWGUI.LOGGER.addListener(appendLogCallback);

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        KButton saveAsButton = new KButton("Save As...");
        saveAsButton.setMnemonic(KeyEvent.VK_S);
        saveAsButton.addActionListener(actionEvent -> {
            KFileChooser fileChooser = new KFileChooser("save-log");
            if (fileChooser.openSaveAsDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try(FileWriter fw = new FileWriter(file)) {
                    for(String line : PWGUI.LOGGER.getLogHistory()) {
                        fw.write(line + "\n");
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, String.format("Failed to save log:\n%s", e.getMessage()), Util.withTitlePrefix("Save Log"), JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        actionRowPanel.add(saveAsButton);

        KButton closeButton = new KButton(new CloseWindowAction(this, false));
        actionRowPanel.add(closeButton);

        add(actionRowPanel, BorderLayout.PAGE_END);
    }

    @Override
    public void dispose() {
        super.dispose();
        PWGUI.LOGGER.removeListener(appendLogCallback);
    }
}
