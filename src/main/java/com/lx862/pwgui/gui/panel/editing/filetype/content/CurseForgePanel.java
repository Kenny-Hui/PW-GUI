package com.lx862.pwgui.gui.panel.editing.filetype.content;

import com.lx862.pwgui.gui.components.DocumentChangedListener;
import com.lx862.pwgui.data.model.file.ContentDirectoryModel;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.components.kui.KTextField;
import com.lx862.pwgui.gui.panel.editing.filetype.FileEntryPaneContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class CurseForgePanel extends JPanel {
    public CurseForgePanel(FileEntryPaneContext context, ContentDirectoryModel fileEntry) {
        setLayout(new BorderLayout());

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        KGridBagLayoutPanel formPanel = new KGridBagLayoutPanel(3, 2);
        formPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("You can add file to packwiz with projects from CurseForge");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, descriptionLabel);

        KTextField contentTextField = new KTextField("Cloth Config API");
        formPanel.addRow(1, new JLabel("URL/Search Term: "), contentTextField);

        contentTextField.addActionListener(actionEvent -> {
            if(!contentTextField.getText().isEmpty()) AddContentPanel.addProjectFromContentPlatform((Window)getTopLevelAncestor(), context.getModpack(), "curseforge", "add", contentTextField.getText());
        });

        JCheckBox forceCurrentFolder = new JCheckBox("Always install to current folder instead of auto detection");
        formPanel.addRow(2, forceCurrentFolder);

        KButton addButton = new KButton("Add Project");
        addButton.addActionListener(actionEvent -> {
            String[] argsToUse;
            if(forceCurrentFolder.isSelected()) {
                argsToUse = new String[]{"curseforge", "add", contentTextField.getText(), "--meta-folder", context.getModpack().getRootPath().relativize(fileEntry.path).toString()};
            } else {
                argsToUse = new String[]{"curseforge", "add", contentTextField.getText()};
            }

            AddContentPanel.addProjectFromContentPlatform((Window)getTopLevelAncestor(), context.getModpack(), argsToUse);
        });
        addButton.setMnemonic(KeyEvent.VK_A);
        addButton.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, addButton);

        contentTextField.getDocument().addDocumentListener(new DocumentChangedListener(() -> updateAddProjectButtonState(addButton, contentTextField)));

        updateAddProjectButtonState(addButton, contentTextField);


        rootPanel.add(formPanel);
        add(rootPanel, BorderLayout.CENTER);
    }

    private void updateAddProjectButtonState(JButton addButton, KTextField urlTextField) {
        boolean shouldEnable = !urlTextField.getText().isEmpty();
        addButton.setEnabled(shouldEnable);
    }
}
