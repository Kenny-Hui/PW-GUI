package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.gui.base.DocumentChangedListener;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.FileIgnoreRules;
import com.lx862.pwgui.data.fileentry.GenericFileEntry;
import com.lx862.pwgui.data.fileentry.GitIgnoreFileEntry;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class GitIgnorePanel extends FileTypePanel {
    private final GenericFileEntry fileEntry;
    private final JTextArea textArea;
    private String initialContent;

    public GitIgnorePanel(FileEntryPaneContext context, GitIgnoreFileEntry fileEntry) {
        super(context);
        this.fileEntry = fileEntry;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel descriptionLabel = new JLabel("<html>All paths in this file will be ignored by Git and will not be included when commiting.<br>Files that are grayed out on the left pane represents files that are ignored.</html>"); // Use html tag to wrap text
        descriptionLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(descriptionLabel);

        this.textArea = new JTextArea();
        this.textArea.setLineWrap(true);
        this.textArea.getDocument().addDocumentListener(new DocumentChangedListener(this::updateSaveState));

        try {
            String content = fileEntry.getContent();
            FileIgnoreRules fileIgnoreRules = new FileIgnoreRules(content);
            context.invokeSetTreeIgnorePattern(fileIgnoreRules);

            this.initialContent = content;
            this.textArea.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
            this.textArea.setText(Util.withBracketPrefix(String.format("Error: %s", e.getMessage())));
        }
        this.textArea.select(0, 0);
        add(new JScrollPane(this.textArea));
    }

    @Override
    public boolean savable() {
        return true;
    }

    @Override
    public boolean shouldSave() {
        return !this.initialContent.equals(textArea.getText());
    }

    @Override
    public void save() throws IOException {
        super.save();
        try(FileWriter fw = new FileWriter(fileEntry.path.toFile())) {
            fw.write(textArea.getText());
        }
    }
}
