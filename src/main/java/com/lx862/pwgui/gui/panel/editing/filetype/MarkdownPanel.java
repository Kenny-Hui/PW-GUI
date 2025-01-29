package com.lx862.pwgui.gui.panel.editing.filetype;

import com.github.rjeschke.txtmark.Processor;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.model.file.MarkdownFileModel;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

public class MarkdownPanel extends FileTypePanel {

    public MarkdownPanel(FileEntryPaneContext context, MarkdownFileModel fileEntry) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        MarkdownPane editorPane = new MarkdownPane();

        try {
            String content = fileEntry.getContent();
            String html = Processor.process(content);
            editorPane.setText(html);
            editorPane.setCaretPosition(0);
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            editorPane.setText(Util.withBracketPrefix(String.format("Error trying to read file: %s", e.getMessage())));
        }

        add(new JScrollPane(editorPane));
    }

    public static class MarkdownPane extends JEditorPane {
        public MarkdownPane() {
            setEditable(false);
            setContentType("text/html");

            addHyperlinkListener(e -> {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(e.getEventType())) {
                    Util.tryBrowse(e.getURL().toString());
                }
            });
        }
    }
}