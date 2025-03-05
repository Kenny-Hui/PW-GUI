package com.lx862.pwgui.gui.panel.editing.filetype;

import com.formdev.flatlaf.ui.*;
import com.lx862.pwgui.data.model.file.PackMetadataFileModel;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.components.DocumentChangedListener;
import com.lx862.pwgui.gui.components.kui.*;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.executable.ProgramExecution;
import com.lx862.pwgui.core.PackwizMetaFile;
import com.lx862.pwgui.gui.dialog.ExecutableProgressDialog;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class PackwizMetaPanel extends FileTypePanel {
    private final String initialDescription;
    private final String initialSide;
    private final boolean initialOptional;
    private final boolean initialPinned;

    private final KTextField descriptionTextField;
    private final JCheckBox optionalCheckbox;
    private final JCheckBox clientCheckbox;
    private final JCheckBox serverCheckbox;
    private final JCheckBox pinnedCheckbox;

    private final PackwizMetaFile packwizMetaFile;

    public PackwizMetaPanel(FileEntryPaneContext context, PackMetadataFileModel fileEntry) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.packwizMetaFile = new PackwizMetaFile(fileEntry.getPackMetadata().getPath(), fileEntry.getPackMetadata().getToml());
        this.initialDescription = packwizMetaFile.optionDescription;
        this.initialOptional = packwizMetaFile.optionOptional;
        this.initialSide = packwizMetaFile.side == null ? "both" : packwizMetaFile.side;
        this.initialPinned = packwizMetaFile.pinned;

        JLabel titleLabel = new JLabel(packwizMetaFile.name);
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        add(titleLabel);

        JLabel fileNameLabel = new JLabel(packwizMetaFile.fileName);
        fileNameLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("small.font")));
        add(fileNameLabel);

        add(new KSeparator());

        KGridBagLayoutPanel editableContentPanel = new KGridBagLayoutPanel(1, 2);
        editableContentPanel.setBorder(new EmptyBorder(5, 0, 0, 0));

        descriptionTextField = new KTextField("(None)", true);
        descriptionTextField.setText(packwizMetaFile.optionDescription);
        descriptionTextField.getDocument().addDocumentListener(new DocumentChangedListener(this::updateSaveState));
        editableContentPanel.addRow(1, new JLabel("Description: "), descriptionTextField);

        JPanel sidePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 4));
        clientCheckbox = new JCheckBox("Client");
        clientCheckbox.setMnemonic(KeyEvent.VK_C);
        clientCheckbox.addActionListener(actionEvent -> updateSaveState());
        serverCheckbox = new JCheckBox("Server");
        serverCheckbox.addActionListener(actionEvent -> updateSaveState());
        serverCheckbox.setMnemonic(KeyEvent.VK_S);

        clientCheckbox.setSelected(fileEntry.getPackMetadata().isClientSide(false));
        serverCheckbox.setSelected(fileEntry.getPackMetadata().isServerSide(false));

        sidePanel.add(clientCheckbox);
        sidePanel.add(serverCheckbox);

        editableContentPanel.addRow(1, new JLabel("Sides: "), sidePanel);

        optionalCheckbox = new JCheckBox("Optional for player");
        optionalCheckbox.addActionListener(actionEvent -> updateSaveState());
        optionalCheckbox.setMnemonic(KeyEvent.VK_O);
        optionalCheckbox.setSelected(packwizMetaFile.optionOptional);
        editableContentPanel.addRow(2, optionalCheckbox);

        pinnedCheckbox = new JCheckBox("Pin file (Prevent update)", packwizMetaFile.pinned);
        pinnedCheckbox.addActionListener(actionEvent -> updateSaveState());
        pinnedCheckbox.setMnemonic(KeyEvent.VK_P);
        pinnedCheckbox.setAlignmentX(LEFT_ALIGNMENT);
        editableContentPanel.addRow(2, pinnedCheckbox);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        KButton updateButton = new KButton("Check for update");
        updateButton.setMnemonic(KeyEvent.VK_C);
        updateButton.addActionListener(actionEvent -> checkForUpdate(getTopLevelAncestor()));
        actionPanel.add(updateButton);

        KButton removeButton = new KButton("Remove");
        removeButton.setMnemonic(KeyEvent.VK_R);
        removeButton.addActionListener(actionEvent -> removeMod());
        actionPanel.add(removeButton);

        editableContentPanel.addRow(2, actionPanel);

        pinnedCheckbox.addActionListener(actionEvent -> updateUpdateButtonState(updateButton, pinnedCheckbox));
        updateUpdateButtonState(updateButton, pinnedCheckbox);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BorderLayout());
        wrapperPanel.add(editableContentPanel, BorderLayout.NORTH);
        wrapperPanel.setAlignmentX(LEFT_ALIGNMENT);

        KCollapsibleToggle showDetailButton = new KCollapsibleToggle("Show Detail", "Hide Detail");

        JPanel detailPanel = new JPanel();
        detailPanel.setLayout(new BorderLayout());
        detailPanel.add(showDetailButton, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(new DetailPanel(packwizMetaFile));
        detailPanel.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setVisible(false);

        showDetailButton.addItemListener(actionEvent -> scrollPane.setVisible(!scrollPane.isVisible()));
        wrapperPanel.add(detailPanel, BorderLayout.CENTER);
        add(wrapperPanel);
    }

    private void updateUpdateButtonState(KButton updateButton, JCheckBox pinnedCheckbox) {
        updateButton.setEnabled(!pinnedCheckbox.isSelected(), "Version is pinned");
    }

    private void removeMod() {
        if(JOptionPane.showConfirmDialog(getTopLevelAncestor(), String.format("Are you sure you want to remove %s?", packwizMetaFile.name), Util.withTitlePrefix("Remove Confirmation"), JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            Executables.packwiz.buildCommand("remove", packwizMetaFile.getSlug()).execute(Constants.REASON_TRIGGERED_BY_USER);
        }
    }

    private void checkForUpdate(Component parent) {
        ProgramExecution programExecution = Executables.packwiz.buildCommand("update", packwizMetaFile.getSlug());
        ExecutableProgressDialog dialog = new ExecutableProgressDialog((Window)getTopLevelAncestor(), String.format("Updating %s...", packwizMetaFile.name), Constants.REASON_TRIGGERED_BY_USER, programExecution);

        AtomicReference<String> updateString = new AtomicReference<>(null);
        programExecution.whenStdout((line) -> {
            if(line.startsWith("Update available:")) {
               updateString.set(line.split("Update available: ")[1]);
            }
        });

        programExecution.whenExit(exitCode -> {
            if(exitCode == 0) {
                String updateMsg = updateString.get();
                if(updateMsg != null) {
                    JOptionPane.showMessageDialog(parent, String.format("%s has been updated!\n%s", packwizMetaFile.name, updateMsg), Util.withTitlePrefix("File Updated!"), JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parent, String.format("%s is already up to date!", packwizMetaFile.name), Util.withTitlePrefix("Up to Date!"), JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        dialog.setVisible(true);
    }

    @Override
    public boolean savable() {
        return true;
    }

    @Override
    public boolean shouldSave() {
        String newSide = !serverCheckbox.isSelected() && !clientCheckbox.isSelected() ? "both" : serverCheckbox.isSelected() && clientCheckbox.isSelected() ? "both" : serverCheckbox.isSelected() ? "server" : "client";  // TODO: We really need a way to not allow switching off both server and client. A lot of work is also plummeted into modpack exporting.
        String descriptionText = descriptionTextField.getText().isEmpty() ? null : descriptionTextField.getText();

        boolean descriptionChanged = !Objects.equals(initialDescription, descriptionText);
        boolean optionalChanged = initialOptional != optionalCheckbox.isSelected();
        boolean sidesChanged = !newSide.equals(initialSide);
        boolean pinnedChanged = initialPinned != pinnedCheckbox.isSelected();

        return descriptionChanged || optionalChanged || sidesChanged || pinnedChanged;
    }

    @Override
    public void save() throws IOException {
        super.save();
        packwizMetaFile.optionOptional = optionalCheckbox.isSelected();
        packwizMetaFile.pinned = pinnedCheckbox.isSelected();
        packwizMetaFile.side = !serverCheckbox.isSelected() && !clientCheckbox.isSelected() ? "both" : serverCheckbox.isSelected() && clientCheckbox.isSelected() ? "both" : serverCheckbox.isSelected() ? "server" : "client";
        packwizMetaFile.optionDescription = descriptionTextField.getText().isEmpty() ? null : descriptionTextField.getText();

        packwizMetaFile.write(Constants.REASON_TRIGGERED_BY_USER);
    }

    static class DetailPanel extends KGridBagLayoutPanel {
        public DetailPanel(PackwizMetaFile packwizMetaFile) {
            super(0, 1);
            setMinimumSize(new Dimension(0, 0));

            KListEntryPanel downloadPanel = new KListEntryPanel("Download");

            if(packwizMetaFile.downloadUrl != null) {
                JPanel urlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
                urlPanel.setOpaque(false);
                urlPanel.add(new JLabel("URL: "));
                urlPanel.add(new KLinkButton(packwizMetaFile.downloadUrl));
                downloadPanel.add(urlPanel);
            }
            downloadPanel.add(new JLabel(String.format("Hash (%s): %s", packwizMetaFile.downloadHashFormat, packwizMetaFile.downloadHash)));

            addRow(1, downloadPanel);

            if(packwizMetaFile.updateMrModId != null && packwizMetaFile.updateMrVersion != null) {
                JPanel updatePanel = new KListEntryPanel("Updates - Modrinth");
                updatePanel.add(new JLabel(String.format("Project ID: %s", packwizMetaFile.updateMrModId)));
                updatePanel.add(new JLabel(String.format("Version ID: %s", packwizMetaFile.updateMrVersion)));

                KLinkButton browseProjectPageLink = new KLinkButton("Browse Project Page", packwizMetaFile.getProjectPageURL());
                KLinkButton browseVersionPageLink = new KLinkButton("Browse Version Page", packwizMetaFile.getVersionPageURL());

                updatePanel.add(browseProjectPageLink);
                updatePanel.add(browseVersionPageLink);

                addRow(1, updatePanel);
            }

            if(packwizMetaFile.updateCfFileId != -1 && packwizMetaFile.updateCfProjectId != -1) {
                JPanel updatePanel = new KListEntryPanel("Updates - CurseForge");
                updatePanel.add(new JLabel(String.format("Project ID: %s", packwizMetaFile.updateCfProjectId)));
                updatePanel.add(new JLabel(String.format("File ID: %s", packwizMetaFile.updateCfFileId)));

                KLinkButton browseProjectPageLink = new KLinkButton("Browse Project Page", packwizMetaFile.getProjectPageURL());
                KLinkButton browseVersionPageLink = new KLinkButton("Browse Version Page", packwizMetaFile.getVersionPageURL());
                updatePanel.add(browseProjectPageLink);
                updatePanel.add(browseVersionPageLink);

                addRow(1, updatePanel);
            }

            if(packwizMetaFile.updateGhSlug != null) {
                JPanel updatePanel = new KListEntryPanel("Updates - GitHub");
                updatePanel.add(new JLabel(String.format("Repository: %s", packwizMetaFile.updateGhSlug)));
                updatePanel.add(new JLabel(String.format("Branch: %s", packwizMetaFile.updateGhBranch)));
                updatePanel.add(new JLabel(String.format("Tag: %s", packwizMetaFile.updateGhTag)));

                KLinkButton browseProjectPageLink = new KLinkButton("Browse Repository", packwizMetaFile.getProjectPageURL());
                updatePanel.add(browseProjectPageLink);

                addRow(1, updatePanel);
            }

            addVerticalFiller();
        }
    }
}
