package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.gui.panel.ImportModpackPanel;

import javax.swing.*;

public class ImportModpackDialog extends JDialog {
    public ImportModpackDialog(JFrame parentFrame) {
        super(parentFrame, Util.withTitlePrefix("Import Modpack"), true);

        setSize(300, 400);
        setLocationRelativeTo(parentFrame);

        add(new ImportModpackPanel(false, (path) -> {
            JOptionPane.showMessageDialog(this, "Modpack imported successfully!", Util.withTitlePrefix("Modpack imported"), JOptionPane.INFORMATION_MESSAGE);
        }));
    }
}
