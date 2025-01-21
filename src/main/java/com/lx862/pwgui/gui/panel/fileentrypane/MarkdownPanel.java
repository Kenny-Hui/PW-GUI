package com.lx862.pwgui.gui.panel.fileentrypane;

import com.github.rjeschke.txtmark.Processor;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.fileentry.MarkdownFileEntry;

import javax.swing.*;

public class MarkdownPanel extends FileTypePanel {
    private final JEditorPane editorPane;

    public MarkdownPanel(MarkdownFileEntry fileEntry) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        editorPane = new JEditorPane();
        editorPane.setEditable(false);

        try {
            String content = fileEntry.getContent();
            String html = Processor.process(content);
            editorPane.setContentType("text/html");
            editorPane.setText("<html>" + html + "</html>");
        } catch (Exception e) {
            e.printStackTrace();
            editorPane.setText(Util.withBracketPrefix("Error trying to read file: " + e.getMessage()));
        }

        add(new JScrollPane(editorPane));
    }
}
