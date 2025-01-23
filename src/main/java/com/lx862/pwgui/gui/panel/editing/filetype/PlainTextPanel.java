package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.data.fileentry.GenericFileEntry;
import com.lx862.pwgui.gui.base.DocumentChangedListener;
import com.lx862.pwgui.gui.base.kui.KButton;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.fileentry.PlainTextFileEntry;

import javax.swing.*;
import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;

public class PlainTextPanel extends FileTypePanel {
    private final GenericFileEntry fileEntry;
    private final JTextArea textArea;
    private String initialContent;

    public PlainTextPanel(PlainTextFileEntry fileEntry, FileEntryPaneContext context) {
        super(context);
        this.fileEntry = fileEntry;
        setLayout(new BorderLayout());

        KButton revertButton = new KButton("Revert...");
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.getDocument().addDocumentListener(new DocumentChangedListener(() -> {
            updateSaveState();
            revertButton.setEnabled(shouldSave());
        }));

        try {
            String content = fileEntry.getContent();
            this.initialContent = content;
            textArea.setText(content);
            revertButton.setEnabled(shouldSave());
        } catch (Exception e) {
            e.printStackTrace();
            textArea.setText(Util.withBracketPrefix(String.format("Error trying to read file: %s", e.getMessage())));
        }
        textArea.select(0, 0); // Jump cursor to start

        JScrollPane jScrollPane = new JScrollPane(textArea);
        jScrollPane.setAlignmentX(LEFT_ALIGNMENT);
        jScrollPane.setAlignmentY(BOTTOM_ALIGNMENT);
        add(jScrollPane, BorderLayout.CENTER);

        revertButton.addActionListener(e -> {
            textArea.setText(initialContent);
            int oldSelStart = textArea.getSelectionStart();
            int oldSelEnd = textArea.getSelectionEnd();
            textArea.select(oldSelStart, oldSelEnd);
        });
        revertButton.setAlignmentX(LEFT_ALIGNMENT);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionPanel.add(revertButton);
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
    public void save(Component parent) throws IOException {
        super.save(parent);
        try(FileWriter fw = new FileWriter(fileEntry.path.toFile())) {
            fw.write(textArea.getText());
        }
    }
}
