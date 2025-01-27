package com.lx862.pwgui.gui.dialog;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.github.rjeschke.txtmark.Processor;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.gui.components.kui.KTabbedPane;
import com.lx862.pwgui.gui.panel.editing.filetype.MarkdownPanel;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;

public class AboutDialog extends JDialog {
    public AboutDialog(Window parent) {
        super(parent, String.format("About %s", Constants.PROGRAM_NAME), ModalityType.DOCUMENT_MODAL);

        setSize(325, 500);
        setLocationRelativeTo(parent);

        JPanel rootPanel = new JPanel(new BorderLayout(0, 4));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));

        JLabel logoLabel;
        try {
            logoLabel = new JLabel(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/logo.png"), 200)));
        } catch (Exception e) {
            logoLabel = new JLabel(Constants.PROGRAM_NAME);
            logoLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h1.font")));
            Main.LOGGER.exception(e);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        mainPanel.add(logoLabel);

        JLabel titleLabel = new JLabel(Constants.PROGRAM_NAME);
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);

        JLabel descriptionLabel = new JLabel("<html><div style='text-align:center'>GUI Wrapper for packwiz, a tool to edit & distribute Minecraft modpacks.</div></html>", SwingConstants.CENTER);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(descriptionLabel);

        JLabel versionLabel = new JLabel(Constants.VERSION);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(versionLabel);

        mainPanel.add(GUIHelper.createVerticalPadding(8));

        KTabbedPane tabbedPane = new KTabbedPane();
        tabbedPane.addTab("External Links", new JScrollPane(new AboutTabPane("links.md")));
        tabbedPane.addTab("Details", new JScrollPane(new AboutTabPane("details.md")));
        tabbedPane.addTab("Credits", new JScrollPane(new AboutTabPane("credits.md")));
        tabbedPane.addTab("Duke", new JScrollPane(new AboutTabPane("duke.md")));

        mainPanel.add(tabbedPane);

        rootPanel.add(mainPanel, BorderLayout.CENTER);
        rootPanel.add(new JLabel("LX86 2025 <3", SwingConstants.CENTER), BorderLayout.SOUTH);
        add(rootPanel);
    }

    static class AboutTabPane extends MarkdownPanel.MarkdownPane {
        public AboutTabPane(String resource) {
            super();

            try {
                setText(Processor.process(Util.getAssets("/assets/about/" + resource)));
            } catch (IOException e) {
                Main.LOGGER.exception(e);
                setText(String.format("Error trying to read file: %s", e.getMessage()));
            }
        }
    }
}