package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.gui.components.ModDetailListCellRenderer;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KCollapsibleToggle;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.function.Consumer;

public class UpdateSummaryDialog extends JDialog {
    public UpdateSummaryDialog(JFrame parentFrame, List<String> newItems, List<String> skippedItems, List<String> unsupportedItems, Consumer<Boolean> callback) {
        super(parentFrame, Util.withTitlePrefix("Update Summary"), true);

        setSize(500, 400);
        setLocationRelativeTo(parentFrame);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel("Update Summary");
        titleLabel.setFont(UIManager.getFont("h2.font"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(titleLabel);

        JLabel descriptionLabel = new JLabel(String.format("%d item(s) can be updated:", newItems.size()));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(descriptionLabel);

        DefaultListModel<String> updateModListModel = new DefaultListModel<>();
        for(String str : newItems) {
            updateModListModel.addElement(str);
        }

        JList<String> updateItemsList = new JList<>(updateModListModel);
        updateItemsList.setCellRenderer(new ModDetailListCellRenderer());

        JScrollPane updateItemsListScrollPane = new JScrollPane(updateItemsList);
        updateItemsListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(updateItemsListScrollPane);

        if(!skippedItems.isEmpty()) {
            DefaultListModel<String> skippedItemsListModel = new DefaultListModel<>();
            for(String mod : skippedItems) {
                skippedItemsListModel.addElement(mod);
            }

            JList<String> skippedItemsList = new JList<>(skippedItemsListModel);
            JScrollPane skippedItemsScrollPane = new JScrollPane(skippedItemsList);
            skippedItemsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            skippedItemsScrollPane.setVisible(false);

            KCollapsibleToggle pinnedItemsToggle = new KCollapsibleToggle(String.format("Show pinned items (%d)", skippedItems.size()), "Hide pinned items");
            pinnedItemsToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
            pinnedItemsToggle.addActionListener(actionEvent -> skippedItemsScrollPane.setVisible(!skippedItemsScrollPane.isVisible()));
            rootPanel.add(pinnedItemsToggle);

            rootPanel.add(skippedItemsScrollPane);
        }

        if(!unsupportedItems.isEmpty()) {
            DefaultListModel<String> unsupportedItemsListModel = new DefaultListModel<>();
            for(String mod : unsupportedItems) {
                unsupportedItemsListModel.addElement(mod);
            }

            JList<String> unsupportedItemsList = new JList<>(unsupportedItemsListModel);
            JScrollPane unsupportedItemsScrollPane = new JScrollPane(unsupportedItemsList);
            unsupportedItemsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            unsupportedItemsScrollPane.setVisible(false);

            KCollapsibleToggle unsupportedItemsToggle = new KCollapsibleToggle(String.format("Show unsupported items (%d)", unsupportedItems.size()), "Hide unsupported items");
            unsupportedItemsToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
            unsupportedItemsToggle.addActionListener(actionEvent -> unsupportedItemsScrollPane.setVisible(!unsupportedItemsScrollPane.isVisible()));
            rootPanel.add(unsupportedItemsToggle);

            rootPanel.add(unsupportedItemsScrollPane);
        }

        JLabel updateConfirmLabel = new JLabel("Do you want to update?");
        updateConfirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(updateConfirmLabel);

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        KButton yesButton = new KButton("Yes");
        yesButton.setMnemonic(KeyEvent.VK_Y);
        yesButton.addActionListener(actionEvent -> {
            dispose();
            callback.accept(true);
        });
        actionRowPanel.add(yesButton);

        KButton noButton = new KButton("No");
        noButton.setMnemonic(KeyEvent.VK_N);
        noButton.addActionListener(actionEvent -> {
            dispose();
            callback.accept(false);
        });
        actionRowPanel.add(noButton);

        rootPanel.add(actionRowPanel);
        add(rootPanel);
    }
}

