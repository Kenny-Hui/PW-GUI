package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.PWGUI;
import com.lx862.pwgui.core.data.model.file.GenericFileModel;
import com.lx862.pwgui.gui.components.DocumentChangedListener;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.core.data.model.file.PlainTextFileModel;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class PlainTextPanel extends FileTypePanel {
    private final GenericFileModel fileEntry;
    private final JTextArea textArea;
    private String initialContent;

    public PlainTextPanel(FileEntryPaneContext context, PlainTextFileModel fileEntry) {
        super(context);
        this.fileEntry = fileEntry;
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setWrapStyleWord(true);
        textArea.setLineWrap(true);
        textArea.setTabSize(1);
        textArea.getDocument().addDocumentListener(new DocumentChangedListener(this::updateSaveState));

        try {
            String content = fileEntry.getContent();
            this.initialContent = content;
            textArea.setText(content);
        } catch (Exception e) {
            PWGUI.LOGGER.exception(e);
            textArea.setText(Util.withBracketPrefix(String.format("Error trying to read file: %s", e.getMessage())));
        }
        textArea.select(0, 0); // Jump cursor to start

        JScrollPane jScrollPane = new JScrollPane(textArea);
        jScrollPane.setAlignmentX(LEFT_ALIGNMENT);
        jScrollPane.setAlignmentY(BOTTOM_ALIGNMENT);
        add(jScrollPane, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.setAlignmentX(LEFT_ALIGNMENT);
        add(actionPanel, BorderLayout.SOUTH);
    }

    @Override
    public boolean savable() {
        return true;
    }

    @Override
    public boolean shouldSave() {
        return !initialContent.equals(textArea.getText());
    }


    @Override
    public void save() throws IOException {
        super.save();
        try(FileWriter fw = new FileWriter(fileEntry.path.toFile())) {
            fw.write(textArea.getText());
        }
    }
}
