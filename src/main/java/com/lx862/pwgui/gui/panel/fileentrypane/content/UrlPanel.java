package com.lx862.pwgui.gui.panel.fileentrypane.content;

import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.base.DocumentChangedListener;
import com.lx862.pwgui.data.fileentry.ContentManagementFolderFileEntry;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.gui.base.kui.KTextField;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.gui.panel.fileentrypane.FileEntryPaneContext;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

public class UrlPanel extends JPanel {
    private static final HashMap<String, String> alternativeForDomain = new HashMap<>();

    static {
        alternativeForDomain.put("modrinth.com", "Modrinth");
        //alternativeForDomain.put("github.com", "GitHub"); // Doesn't seems finished
        alternativeForDomain.put("curseforge.com", "Curseforge");
        alternativeForDomain.put("forgecdn.net", "Curseforge");
    }

    public UrlPanel(FileEntryPaneContext context, ContentManagementFolderFileEntry fileEntry) {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        KGridBagLayoutPanel formPanel = new KGridBagLayoutPanel(3, 2);
        formPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel("You can add file to packwiz based on a direct URL link.");
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, descriptionLabel);

        KTextField nameTextField = new KTextField("testmod");
        formPanel.addRow(1, new JLabel("Name: "), nameTextField);

        KTextField urlTextField = new KTextField("https://example.com");
        formPanel.addRow(1, new JLabel("URL: "), urlTextField);

        JLabel invalidUrlLabel = new JLabel("Please enter a valid URL!");
        invalidUrlLabel.setForeground(Color.RED);
        formPanel.addRow(2, invalidUrlLabel);

        KButton addButton = new KButton("Add File");
        addButton.setMnemonic(KeyEvent.VK_A);
        addButton.setAlignmentX(LEFT_ALIGNMENT);
        formPanel.addRow(2, addButton);

        nameTextField.getDocument().addDocumentListener(new DocumentChangedListener(() -> updateInstallButtonState(addButton, nameTextField, urlTextField, invalidUrlLabel)));
        urlTextField.getDocument().addDocumentListener(new DocumentChangedListener(() -> updateInstallButtonState(addButton, nameTextField, urlTextField, invalidUrlLabel)));

        updateInstallButtonState(addButton, nameTextField, urlTextField, invalidUrlLabel);

        addButton.addActionListener(actionEvent -> {
            try {
                URI url = new URI(urlTextField.getText());
                if(alternativeForDomain.containsKey(url.getHost())) {
                    String alternativeName = alternativeForDomain.get(url.getHost());
                    if(JOptionPane.showConfirmDialog(context.getParent(), String.format("Adding content from %s is supported natively in this application.\nBy continuing, you won't get auto-update and other %s-specific features.\nContinue anyway?", alternativeName, alternativeName), Util.withTitlePrefix("Alternative installation method exists"), JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION) {
                        return;
                    }
                }
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }

            ProgramExecution programExecution = Main.packwiz.buildCommand("url", "add", nameTextField.getText(), urlTextField.getText(), "--force"); // We already did a domain check before, so forcibly add it anyway.
            programExecution.whenExit((exitCode) -> {
               if(exitCode == 0) {
                   JOptionPane.showMessageDialog(context.getParent(), nameTextField.getText() + " has been added!", Util.withTitlePrefix("File added"), JOptionPane.INFORMATION_MESSAGE);
               }
            });

            new ExecutableProgressDialog(null, "Adding mod...", "Triggered by user", programExecution).setVisible(true);
        });

        panel.add(formPanel);
        add(panel, BorderLayout.CENTER);
    }

    private void updateInstallButtonState(JButton addButton, KTextField nameTextField, KTextField urlTextField, JLabel urlInvalidLabel) {
        boolean shouldEnable = true;
        urlInvalidLabel.setVisible(false);
        if(nameTextField.getText().isEmpty() || urlTextField.getText().isEmpty()) {
            shouldEnable = false;
        } else {
            try {
                new URI(urlTextField.getText());
            } catch (URISyntaxException e) {
                urlInvalidLabel.setVisible(true);
                shouldEnable = false;
            }
        }

        addButton.setEnabled(shouldEnable);
    }
}
