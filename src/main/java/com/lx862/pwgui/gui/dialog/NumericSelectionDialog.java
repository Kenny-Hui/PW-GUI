package com.lx862.pwgui.gui.dialog;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.components.kui.KListCellRenderer;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

public class NumericSelectionDialog extends JDialog {
    public <T> NumericSelectionDialog(JDialog dialog, String title, List<T> list, Consumer<Integer> callback) {
        super(dialog, Util.withTitlePrefix(title), true);
        init(dialog, title, list, callback);
    }

    public <T> NumericSelectionDialog(JFrame frame, String title, List<T> list, Consumer<Integer> callback) {
        super(frame, Util.withTitlePrefix(title), true);
        init(frame, title, list, callback);
    }

    private <T> void init(Component component, String title, List<T> list, Consumer<Integer> callback) {
        KGridBagLayoutPanel panel = new KGridBagLayoutPanel(3, 1);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.addRow(1, titleLabel);
        panel.addRow(1, new JLabel("<html>Multiple projects were found based on your search term<br>Please select which one you'd like to choose</html>"));

        DefaultListModel<T> defaultListModel = new DefaultListModel<>();

        for(T obj : list) {
            defaultListModel.addElement(obj);
        }

        JList<T> jList = new JList<>(defaultListModel);
        jList.setSize(Integer.MAX_VALUE, getHeight());
        jList.setCellRenderer(new KListCellRenderer());
        jList.setSelectedIndex(0);
        jList.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.addRow(1, jList);

        KButton okButton = new KButton("OK");
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.addActionListener(actionEvent -> {
            callback.accept(jList.getSelectedIndex());
            dispose();
        });

        KButton cancelButton = new KButton("Cancel");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(actionEvent -> {
            callback.accept(-1);
            dispose();
        });

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionRowPanel.add(okButton);
        actionRowPanel.add(cancelButton);

        panel.addRow(1, actionRowPanel);
        add(panel);

        pack();
        setLocationRelativeTo(component);
    }
}
