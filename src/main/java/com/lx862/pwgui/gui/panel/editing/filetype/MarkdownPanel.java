package com.lx862.pwgui.gui.panel.editing.filetype;

import com.github.rjeschke.txtmark.Processor;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.fileentry.MarkdownFileEntry;

import javax.swing.*;

public class MarkdownPanel extends FileTypePanel {

    public MarkdownPanel(FileEntryPaneContext context, MarkdownFileEntry fileEntry) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JEditorPane editorPane = new JEditorPane();
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
