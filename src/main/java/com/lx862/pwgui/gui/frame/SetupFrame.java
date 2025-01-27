package com.lx862.pwgui.gui.frame;

import com.formdev.flatlaf.ui.FlatUIUtils;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.core.Constants;
import com.lx862.pwgui.gui.action.DownloadPackwizAction;
import com.lx862.pwgui.gui.action.LocatePackwizAction;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KSeparator;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/** The greeting splash screen if the packwiz executable is not found */
public class SetupFrame extends BaseFrame {
    public SetupFrame(Component parent) {
        super(String.format("Welcome to %s!", Constants.PROGRAM_NAME));

        setSize(400, 550);
        setLocationRelativeTo(parent);

        JPanel rootPanel = new JPanel();
        rootPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));

        JLabel logoLabel;
        try {
            logoLabel = new JLabel(new ImageIcon(GUIHelper.convertImage(Util.getAssets("/assets/logo.png"), 200)));
        } catch (Exception e) {
            logoLabel = new JLabel(Constants.PROGRAM_NAME);
            logoLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h1.font")));
            Main.LOGGER.exception(e);
        }

        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(Box.createVerticalGlue());
        rootPanel.add(logoLabel);

        rootPanel.add(GUIHelper.createVerticalPadding(8));

        JLabel titleLabel = new JLabel(String.format("Heya! Welcome to %s!", Constants.PROGRAM_NAME));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(FlatUIUtils.nonUIResource(UIManager.getFont("h2.font")));
        rootPanel.add(titleLabel);

        rootPanel.add(GUIHelper.createVerticalPadding(8));

        JLabel descriptionLabel = new JLabel("<html><div style=\"text-align:center\">PW-GUI aims to simplify modpack management by providing a GUI around the command-line program packwiz.</div></html>", SwingConstants.CENTER);
        descriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(descriptionLabel);

        rootPanel.add(GUIHelper.createVerticalPadding(10));

        rootPanel.add(new KSeparator());

        rootPanel.add(GUIHelper.createVerticalPadding(10));

        JLabel whatToDoNextDescriptionLabel = new JLabel("<html><p style=\"text-align:center\">Don't have packwiz yet? Just click the download button below and we'll take care of it!</p><p style=\"margin-top:7px;text-align:center;\">Otherwise, please give me a favor by pointing me to the packwiz executable~</p></html>", SwingConstants.CENTER);
        whatToDoNextDescriptionLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        whatToDoNextDescriptionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(whatToDoNextDescriptionLabel);
        rootPanel.add(Box.createRigidArea(new Dimension(0, 16)));

        KButton downloadButton = new KButton(new DownloadPackwizAction(this, () -> {
            JOptionPane.showMessageDialog(parent, "Packwiz has been downloaded and configured!", Util.withTitlePrefix("Download Success!"), JOptionPane.INFORMATION_MESSAGE);
            WelcomeFrame welcomeFrame = new WelcomeFrame(parent);
            welcomeFrame.setVisible(true);
            dispose();
        }));
        downloadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(downloadButton);

        rootPanel.add(Box.createRigidArea(new Dimension(0, 8)));

        KButton locateButton = new KButton(new LocatePackwizAction(this, () -> {
            WelcomeFrame welcomeFrame = new WelcomeFrame(parent);
            welcomeFrame.setVisible(true);
            dispose();
        }));
        locateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        rootPanel.add(locateButton);

        rootPanel.add(Box.createVerticalGlue());

        add(rootPanel);
        this.jMenuBar.add(super.getHelpMenu());
    }
}
