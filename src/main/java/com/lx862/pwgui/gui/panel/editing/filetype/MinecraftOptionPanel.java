package com.lx862.pwgui.gui.panel.editing.filetype;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.data.fileentry.MinecraftOptionsFileEntry;

import javax.swing.*;

public class MinecraftOptionPanel extends FileTypePanel {

    public MinecraftOptionPanel(FileEntryPaneContext context, MinecraftOptionsFileEntry fileEntry) {
        super(context);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel descriptionLabel = new JLabel("<html>This file contains the in-game settings managed by Minecraft.</html>");
        descriptionLabel.setAlignmentX(CENTER_ALIGNMENT);
        add(descriptionLabel);

        JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
        try {
            String content = fileEntry.getContent();
            textPane.setText(syntaxHighlighting(content));
        } catch (Exception e) {
            Main.LOGGER.exception(e);
            textPane.setText(Util.withBracketPrefix("Error trying to read file: " + e.getMessage()));
        }
        textPane.select(0, 0);
        textPane.setEditable(false);
        textPane.setAlignmentX(CENTER_ALIGNMENT);
        add(new JScrollPane(textPane));
    }

    private String syntaxHighlighting(String str) {
        StringBuilder stringBuilder = new StringBuilder();
        for(String line : str.split("\n")) {
            final int firstColonIdx = line.indexOf(":");
            final String key = line.substring(0, firstColonIdx);
            final String value = line.substring(firstColonIdx+1);

            stringBuilder.append("<span style=\"color:#FF2E98\">").append(key).append("</span>: ")
                    .append("<span style=\"color:#009900\">").append(value).append("</span><br>");
        }
        return stringBuilder.toString();
    }
}
