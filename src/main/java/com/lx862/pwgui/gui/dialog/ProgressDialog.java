package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.gui.base.kui.KCollapsibleToggle;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ProgressDialog extends JDialog {
    private final JProgressBar progressBar;
    private final JLabel statusLabel;
    private final JTextArea logTextArea;

    public ProgressDialog(JFrame frame, String title) {
        super(frame, Util.withTitlePrefix(title), true);
        JPanel panel = new JPanel();
        setSize(450, 160);
        setLocationRelativeTo(frame);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIManager.getFont("h2.font"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(progressBar);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        statusLabel = new JLabel("Status text");
        statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(statusLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        logTextArea = new JTextArea(10, 0);
        logTextArea.setEditable(false);
        logTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        KCollapsibleToggle logToggle = new KCollapsibleToggle("Show Log", "Hide Log");
        logToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(logToggle);

        JScrollPane logWrapper = new JScrollPane(logTextArea);
        logWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        logWrapper.setVisible(false);

        logToggle.addItemListener(itemEvent -> {
            logWrapper.setVisible(logToggle.isSelected());
            setSize(getSize().width, getPreferredSize().height);
        });

        panel.add(logWrapper);

        add(panel);
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
