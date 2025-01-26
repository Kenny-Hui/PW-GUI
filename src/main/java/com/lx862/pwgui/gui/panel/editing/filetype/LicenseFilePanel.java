package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.model.file.PlainTextFileModel;

import javax.swing.*;

public class LicenseFilePanel extends FileTypePanel {
    public LicenseFilePanel(FileEntryPaneContext context, PlainTextFileModel fileEntry) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        try {
            String content = fileEntry.getContent();
            textArea.setText(content);
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            textArea.setText(Util.withBracketPrefix(String.format("Error trying to read file: %s", e.getMessage())));
        }
        textArea.select(0, 0);

        JScrollPane jScrollPane = new JScrollPane(textArea);
        add(jScrollPane);
    }
}
