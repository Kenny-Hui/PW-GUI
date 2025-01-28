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

public class ModrinthPanel extends JPanel {
    public ModrinthPanel(FileEntryPaneContext context, ContentDirectoryModel fileEntry) {
        setLayout(new BorderLayout());

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        KGridBagLayoutPanel formPanel = new KGridBagLayoutPanel(3, 2);
        formPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("You can add file to packwiz with projects from Modrinth");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, descriptionLabel);

        KTextField contentTextField = new KTextField("Cloth Config API");
        contentTextField.addActionListener(actionEvent -> {
            if(!contentTextField.getText().isEmpty()) AddContentPanel.addProjectFromContentPlatform((Window)getTopLevelAncestor(), context.getModpack(), "modrinth", "add", contentTextField.getText());
        });
        formPanel.addRow(1, new JLabel("URL/Search Term: "), contentTextField);

        KButton addButton = new KButton("Add Project");
        addButton.addActionListener(actionEvent -> AddContentPanel.addProjectFromContentPlatform((Window)getTopLevelAncestor(), context.getModpack(), "modrinth", "add", contentTextField.getText()));
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
