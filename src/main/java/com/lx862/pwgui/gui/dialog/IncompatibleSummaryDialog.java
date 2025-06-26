package com.lx862.pwgui.gui.dialog;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.gui.components.kui.KRootContentPanel;
import com.lx862.pwgui.pwcore.PackwizMetaFile;
import com.lx862.pwgui.gui.components.ModDetailListCellRenderer;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class IncompatibleSummaryDialog extends JDialog {
    public IncompatibleSummaryDialog(Window parent, List<PackwizMetaFile> items) {
        super(parent, Util.withTitlePrefix("Compatibility Summary"), ModalityType.DOCUMENT_MODAL);

        setSize(500, 400);
        setLocationRelativeTo(parent);

        KRootContentPanel contentPanel = new KRootContentPanel(10);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));

        JLabel titleLabel = new JLabel("Compatibility Summary");
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        JLabel descriptionLabel = new JLabel(String.format("%d item(s) does not have a compatible version for the current modpack:", items.size()));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descriptionLabel);

        DefaultListModel<PackwizMetaFile> updateModListModel = new DefaultListModel<>();
        for(PackwizMetaFile str : items) {
            updateModListModel.addElement(str);
        }

        JList<PackwizMetaFile> updateItemsList = new JList<>(updateModListModel);
        updateItemsList.setCellRenderer(new ModDetailListCellRenderer());

        JScrollPane updateItemsListScrollPane = new JScrollPane(updateItemsList);
        updateItemsListScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(updateItemsListScrollPane);

        JLabel updateConfirmLabel = new JLabel("What would you like to do?");
        updateConfirmLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(updateConfirmLabel);

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        KButton copyButton = new KButton("Copy to clipboard");
        copyButton.setMnemonic(KeyEvent.VK_C);
        copyButton.addActionListener(actionEvent -> {
            copyToClipboard(items);
            dispose();
        });
        actionRowPanel.add(copyButton);

        KButton removeButton = new KButton("Remove items");
        removeButton.setMnemonic(KeyEvent.VK_R);
        removeButton.addActionListener(actionEvent -> {
            int successfulRemoval = 0;
            for(PackwizMetaFile meta : items) {
                try {
                    Files.delete(meta.getPath());
                    successfulRemoval++;
                } catch (IOException e) {
                    PWGUI.LOGGER.exception(e);
                }
            }
            if(successfulRemoval == items.size()) {
                JOptionPane.showMessageDialog(parent, String.format("Removed %d incompatible item(s)", successfulRemoval), "Removed incompatible items", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parent, String.format("Removed %d incompatible item(s).\nFailed to remove %d item(s), see log for details!", successfulRemoval, items.size() - successfulRemoval), "Removed incompatible items", JOptionPane.WARNING_MESSAGE);
            }
            dispose();
        });
        actionRowPanel.add(removeButton);

        KButton ignoreButton = new KButton("Ignore");
        ignoreButton.setMnemonic(KeyEvent.VK_I);
        ignoreButton.addActionListener(actionEvent -> {
            dispose();
        });
        actionRowPanel.add(ignoreButton);

        contentPanel.add(actionRowPanel);
        add(contentPanel);
    }

    private void copyToClipboard(List<PackwizMetaFile> metas) {
        StringBuilder sb = new StringBuilder();
        for(PackwizMetaFile meta : metas) {
            sb.append("Name: ").append(meta.name).append("\n");
            sb.append("File name: ").append(meta.fileName).append("\n");
            sb.append("Project Link: ").append(meta.getProjectPageURL()).append("\n");
            sb.append("\n");
        }

        Util.copyToClipboard(sb.toString());
    }
}

