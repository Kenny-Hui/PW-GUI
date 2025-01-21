package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.base.kui.KListCellRenderer;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

public class ModSelectionDialog extends JDialog {
    public <T> ModSelectionDialog(JDialog dialog, String title, List<T> list, Consumer<Integer> callback) {
        super(dialog, Util.withTitlePrefix(title), true);
        init(dialog, title, list, callback);
    }

    public <T> ModSelectionDialog(JFrame frame, String title, List<T> list, Consumer<Integer> callback) {
        super(frame, Util.withTitlePrefix(title), true);
        init(frame, title, list, callback);
    }

    private <T> void init(Component component, String title, List<T> list, Consumer<Integer> callback) {
        KGridBagLayoutPanel panel = new KGridBagLayoutPanel(3, 1);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UIManager.getFont("h2.font"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.addRow(1, titleLabel);
        panel.addRow(1, new JLabel("<html>Multiple projects were found based on your search term<br>Please select which one you'd like to install</html>"));

        DefaultListModel<T> listModel = new DefaultListModel<>();

        for(T obj : list) {
            listModel.addElement(obj);
        }

        JList<T> jlist = new JList<>(listModel);
        jlist.setSize(Integer.MAX_VALUE, getHeight());
        jlist.setCellRenderer(new KListCellRenderer());
        jlist.setSelectedIndex(0);
        jlist.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.addRow(1, jlist);

        KButton okButton = new KButton("OK");
        okButton.setMnemonic(KeyEvent.VK_O);
        okButton.addActionListener(actionEvent -> {
            callback.accept(jlist.getSelectedIndex());
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
