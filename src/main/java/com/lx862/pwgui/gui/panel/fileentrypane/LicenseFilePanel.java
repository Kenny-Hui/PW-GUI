package com.lx862.pwgui.gui.panel.fileentrypane;

import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.fileentry.PlainTextFileEntry;

import javax.swing.*;

public class LicenseFilePanel extends FileTypePanel {
    public LicenseFilePanel(PlainTextFileEntry fileEntry) {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);

        try {
            String content = fileEntry.getContent();
            textArea.setText(content);
        } catch (Exception e) {
            e.printStackTrace();
            textArea.setText(Util.withBracketPrefix("Error trying to read file: " + e.getMessage()));
        }
        textArea.select(0, 0);

        JScrollPane jScrollPane = new JScrollPane(textArea);
        add(jScrollPane);
    }
}
