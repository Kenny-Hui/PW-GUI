package com.lx862.pwgui.gui.popup;

import com.github.rjeschke.txtmark.Processor;
import com.lx862.pwgui.pwcore.PackFile;
import com.lx862.pwgui.pwcore.PackIndexFile;
import com.lx862.pwgui.pwcore.PackwizMetaFile;
import com.lx862.pwgui.gui.action.CloseWindowAction;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.gui.panel.editing.filetype.MarkdownPanel;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Dialog to view the program's log */
public class GenerateModlistDialog extends JDialog {
    private final PackFile packFile;
    private String plainTextModlist = null;

    private final MarkdownPanel.MarkdownPane previewPane;
    private final JRadioButton plainTextRadioButton;
    private final JRadioButton markdownRadioButton;

    private final JCheckBox projectLinkCheckBox;
    private final JCheckBox versionLinkCheckBox;
    private final JCheckBox fileNameCheckBox;
    private final JCheckBox separateSidesCheckBox;


    public GenerateModlistDialog(Window frame, PackFile packFile) {
        super(frame, Util.withTitlePrefix("Generate Modlist"));

        this.packFile = packFile;

        setSize(512, 400);
        setLocationRelativeTo(frame);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel headerRow = new JPanel();
        headerRow.setLayout(new BoxLayout(headerRow, BoxLayout.PAGE_AXIS));
        headerRow.setBorder(GUIHelper.getPaddedBorder(2));

        JPanel formatRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formatRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel formatLabel = new JLabel("Format:");
        formatRow.add(formatLabel);

        this.plainTextRadioButton = new JRadioButton("Plain Text");
        this.plainTextRadioButton.addItemListener(e -> updateModlist());
        formatRow.add(plainTextRadioButton);

        this.markdownRadioButton = new JRadioButton("Markdown");
        this.markdownRadioButton.setSelected(true);
        this.markdownRadioButton.addItemListener(e -> updateModlist());
        formatRow.add(markdownRadioButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(plainTextRadioButton);
        buttonGroup.add(markdownRadioButton);

        headerRow.add(formatRow);

        JPanel optionsRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        optionsRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel optionsLabel = new JLabel("Options:");
        optionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        optionsRow.add(optionsLabel);

        this.separateSidesCheckBox = new JCheckBox("Separate Side");
        separateSidesCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        separateSidesCheckBox.setSelected(true);
        separateSidesCheckBox.addItemListener(e -> updateModlist());
        optionsRow.add(separateSidesCheckBox);

        this.projectLinkCheckBox = new JCheckBox("Project Link");
        projectLinkCheckBox.setSelected(true);
        projectLinkCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        projectLinkCheckBox.addItemListener(e -> updateModlist());
        optionsRow.add(projectLinkCheckBox);

        this.versionLinkCheckBox = new JCheckBox("Version Link");
        versionLinkCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        versionLinkCheckBox.addItemListener(e -> {
            projectLinkCheckBox.setEnabled(!versionLinkCheckBox.isSelected());
            updateModlist();
        });
        optionsRow.add(versionLinkCheckBox);

        this.fileNameCheckBox = new JCheckBox("File Name");
        fileNameCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        fileNameCheckBox.addItemListener(e -> updateModlist());
        optionsRow.add(fileNameCheckBox);

        headerRow.add(optionsRow);
        add(headerRow, BorderLayout.NORTH);

        this.previewPane = new MarkdownPanel.MarkdownPane();
        JScrollPane previewScrollPane = new JScrollPane(previewPane);
        add(previewScrollPane);

        updateModlist();

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        KButton saveAsButton = new KButton("Save As...");
        saveAsButton.setMnemonic(KeyEvent.VK_S);
        saveAsButton.addActionListener(actionEvent -> {
            KFileChooser fileChooser = new KFileChooser();
            fileChooser.setCurrentDirectory(packFile.getPath().getParent().toFile());
            fileChooser.setSelectedFile(new File(markdownRadioButton.isSelected() ? "modlist.md" : "modlist.txt"));
            if (fileChooser.openSaveAsDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try(FileWriter fw = new FileWriter(file)) {
                    fw.write(plainTextModlist);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(this, String.format("Failed to save modlist:\n%s", e.getMessage()), Util.withTitlePrefix("Save Log"), JOptionPane.ERROR_MESSAGE);
                }
                dispose();
            }
        });
        actionRowPanel.add(saveAsButton);

        KButton copyButton = new KButton("Copy");
        copyButton.setMnemonic(KeyEvent.VK_O);
        copyButton.addActionListener(e -> Util.copyToClipboard(plainTextModlist));
        actionRowPanel.add(copyButton);

        KButton closeButton = new KButton(new CloseWindowAction(this, false));
        actionRowPanel.add(closeButton);

        add(actionRowPanel, BorderLayout.PAGE_END);
    }

    private void updateModlist() {
        boolean useMarkdown = markdownRadioButton.isSelected();
        plainTextModlist = getModlist(packFile, useMarkdown, projectLinkCheckBox.isSelected(), versionLinkCheckBox.isSelected(), separateSidesCheckBox.isSelected(), fileNameCheckBox.isSelected());
        previewPane.setInitialContent(useMarkdown ? Processor.process(this.plainTextModlist) : this.plainTextModlist.replace("\n", "<br>"));
    }

    private String getModlist(PackFile packFile, boolean useMarkdown, boolean projectLink, boolean versionLink, boolean separateSides, boolean showFileName) {
        PackIndexFile indexFile = packFile.packIndexFile.get();
        List<PackwizMetaFile> metaFiles = new ArrayList<>();
        for(PackIndexFile.FileEntry fileEntry : indexFile.getFileEntries()) {
            if(!fileEntry.metafile) continue;
            metaFiles.add(new PackwizMetaFile(packFile.resolveRelative(fileEntry.file)));
        }

        List<PackwizMetaFile> clientFiles = metaFiles.stream().filter(e -> e.isClientSide(true)).toList();
        List<PackwizMetaFile> serverFiles = metaFiles.stream().filter(e -> e.isServerSide(true)).toList();
        List<PackwizMetaFile> bothFiles = metaFiles.stream().filter(e -> e.isClientSide(false) && e.isServerSide(false)).toList();

        StringBuilder sb = new StringBuilder();
        sb.append(useMarkdown ? "# Modlist\n" : "");

        if(!separateSides) {
            for(PackwizMetaFile packwizMetaFile : metaFiles) {
                sb.append(getMetaLine(packwizMetaFile, useMarkdown, projectLink, versionLink, showFileName)).append("\n");
            }
        } else {
            if(!clientFiles.isEmpty()) {
                sb.append("\n").append(getSectionHeader("Client-only", useMarkdown)).append("\n");
                for(PackwizMetaFile packwizMetaFile : clientFiles) {
                    sb.append(getMetaLine(packwizMetaFile, useMarkdown, projectLink, versionLink, showFileName)).append("\n");
                }
            }

            if(!serverFiles.isEmpty()) {
                sb.append("\n").append(getSectionHeader("Server-only", useMarkdown)).append("\n");
                for(PackwizMetaFile packwizMetaFile : serverFiles) {
                    sb.append(getMetaLine(packwizMetaFile, useMarkdown, projectLink, versionLink, showFileName)).append("\n");
                }
            }

            if(!bothFiles.isEmpty()) {
                sb.append("\n").append(getSectionHeader("Common", useMarkdown)).append("\n");
                for(PackwizMetaFile packwizMetaFile : bothFiles) {
                    sb.append(getMetaLine(packwizMetaFile, useMarkdown, projectLink, versionLink, showFileName)).append("\n");
                }
            }
        }

        return sb.toString();
    }

    private String getSectionHeader(String str, boolean useMarkdown) {
        return useMarkdown ? "## " + str : "===== " + str + " =====";
    }

    private String getMetaLine(PackwizMetaFile packwizMetaFile, boolean isMarkdown, boolean projectLink, boolean versionLink, boolean showFileName) {
        if(!isMarkdown) {
            String line = packwizMetaFile.name;

            if(packwizMetaFile.optionDescription != null) {
                line += "\n" + packwizMetaFile.optionDescription;
            }

            if(showFileName) {
                line += String.format("\n%s", packwizMetaFile.fileName);
            }

            if(projectLink || versionLink) {
                final String url;
                if(versionLink) {
                    url = packwizMetaFile.getVersionPageURL();
                } else {
                    url = packwizMetaFile.getProjectPageURL();
                }
                line += "\n" + (url == null ? packwizMetaFile.downloadUrl : url);
            }

            line += "\n";
            return line;
        }

        String line = "- ";

        if(versionLink || projectLink) {
            final String url;
            if(versionLink) {
                url = packwizMetaFile.getVersionPageURL();
            } else {
                url = packwizMetaFile.getProjectPageURL();
            }
            line += String.format(("[%s](%s)"), packwizMetaFile.name, (url == null ? packwizMetaFile.downloadUrl : url));
        } else {
            line += String.format("%s", packwizMetaFile.name);
        }


        if(packwizMetaFile.optionDescription != null) {
            line += " - " + packwizMetaFile.optionDescription;
        }
        if(showFileName) {
            line += String.format(" (%s)", packwizMetaFile.fileName);
        }
        return line;
    }
}
