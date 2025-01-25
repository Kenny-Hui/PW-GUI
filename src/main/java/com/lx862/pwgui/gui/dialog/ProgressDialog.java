package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.gui.components.kui.KCollapsibleToggle;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class ProgressDialog extends JDialog {
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private final JTextArea logTextArea;

    public ProgressDialog(Window window, String title) {
        super(window, Util.withTitlePrefix(title), ModalityType.DOCUMENT_MODAL);

        setSize(450, 160);
        setLocationRelativeTo(window);

        JPanel rootPanel = new JPanel();
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIManager.getFont("h2.font"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(titleLabel);

        rootPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(progressBar);

        rootPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusLabel = new JLabel("Status text");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(statusLabel);

        rootPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        logTextArea = new JTextArea(10, 0);
        logTextArea.setEditable(false);
        logTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        KCollapsibleToggle logToggle = new KCollapsibleToggle("Show Log", "Hide Log");
        logToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(logToggle);

        JScrollPane logWrapper = new JScrollPane(logTextArea);
        logWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        logWrapper.setVisible(false);

        logToggle.addItemListener(itemEvent -> {
            logWrapper.setVisible(logToggle.isSelected());
            setSize(getSize().width, getPreferredSize().height);
        });

        rootPanel.add(logWrapper);

        add(rootPanel);
    }

    protected void setProgress(int progress) {
        progressBar.setIndeterminate(false);
        progressBar.setValue(progress);
    }

    protected void setStatus(String text) {
        statusLabel.setText(text);
        logTextArea.append(text + "\n");
    }
}
