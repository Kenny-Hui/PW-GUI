package com.lx862.pwgui.gui.panel;

import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.base.kui.KTextField;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ModpackInfoPanel extends KGridBagLayoutPanel {
    private boolean modified = false;
    private final KTextField nameTextField;
    private final KTextField authorTextField;
    private final KTextField versionTextField;

    public ModpackInfoPanel(PackFile existingFile) {
        super(3, 2);

        nameTextField = new KTextField();
        nameTextField.setText(existingFile == null ? "My Epic Modpack" : existingFile.name);
        addRow(1, new JLabel("Name: "), nameTextField);

        authorTextField = new KTextField();
        authorTextField.setText(existingFile == null ? "Me!" : existingFile.author);
        addRow(1, new JLabel("Author: "), authorTextField);

        versionTextField = new KTextField();
        versionTextField.setText(existingFile == null ? "1.0.0" : existingFile.version);
        addRow(1, new JLabel("Version: "), versionTextField);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = getPreferredSize();
        size.width = Short.MAX_VALUE;
        return size;
    }

    public String getModpackName() {
        return nameTextField.getText();
    }

    public List<String> getInitArguments() {
        List<String> st = new ArrayList<>();

        st.add("--name");
        st.add(nameTextField.getText());

        if(!authorTextField.getText().isEmpty()) {
            st.add("--author");
            st.add(authorTextField.getText());
        }

        st.add("--version");
        st.add(versionTextField.getText());
        return st;
    }
}
