package com.lx862.pwgui.gui.panel;

import com.lx862.pwgui.data.*;
import com.lx862.pwgui.gui.base.kui.KComboBox;
import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.data.Caches;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.base.WrapLayout;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModpackVersionPanel extends KGridBagLayoutPanel {
    private boolean modified = false;
    private final KComboBox<VersionMetadata> mcVersionComboBox;
    private final JLabel modloaderVersionLabel;
    private final KComboBox<VersionMetadata> modloaderVersionComboBox;

    private PackComponent selectedModloader;

    public ModpackVersionPanel(PackFile existingFile) {
        super(3, 2);

        modloaderVersionLabel = new JLabel("Modloader Version: ");
        modloaderVersionComboBox = new KComboBox<>();
        mcVersionComboBox = new KComboBox<>();
        mcVersionComboBox.setEditable(true);
        modloaderVersionComboBox.setEditable(true);

        if(existingFile != null) {
            mcVersionComboBox.setSelectedItem(existingFile.versionsMinecraft);
            modloaderVersionComboBox.setSelectedItem(existingFile.getModloaderVersion());
        }

        fetchComponentDatas(PackComponent.MINECRAFT, () -> {
            fillComboBoxes(null, mcVersionComboBox, PackComponent.MINECRAFT, false);
            // We add event listener after we fill the combobox, as we don't want the fill update to be triggered
            mcVersionComboBox.setAction(null);
            mcVersionComboBox.addActionListener(actionEvent -> {
                updateModloader(selectedModloader);
            });
        });
        addRow(1, new JLabel("Minecraft Version: ", new ImageIcon(GUIHelper.resizeImage(IconNamePair.MINECRAFT.image, 20)), SwingConstants.LEFT), mcVersionComboBox);

        JPanel modloaderChoicePanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 0, 0));

        modloaderChoicePanel.add(new JLabel("Modloader: "));
        ButtonGroup modloadersButtonGroup = new ButtonGroup();

        JRadioButton vanillaRadioButton = new JRadioButton("None (Vanilla)");
        vanillaRadioButton.addItemListener((itemListener) -> {
            updateModloader(null);
        });
        modloadersButtonGroup.add(vanillaRadioButton);
        modloadersButtonGroup.setSelected(vanillaRadioButton.getModel(), true);
        modloaderChoicePanel.add(vanillaRadioButton);

        for(PackComponent packComponent : PackComponent.values()) {
            if(!packComponent.choosable) continue;

            JRadioButton componentRadioButton = new JRadioButton(packComponent.iconName.name);
            componentRadioButton.addItemListener((itemListener) -> {
                updateModloader(packComponent);
            });
            componentRadioButton.setIcon(new ImageIcon(GUIHelper.fadeImage(GUIHelper.resizeImage(packComponent.iconName.image, 18), 0.5f)));
            componentRadioButton.setSelectedIcon(new ImageIcon(GUIHelper.resizeImage(packComponent.iconName.image, 18)));
            modloaderChoicePanel.add(componentRadioButton);
            modloadersButtonGroup.add(componentRadioButton);

            if(existingFile != null && existingFile.getComponents().stream().anyMatch(packComponentVersion -> packComponentVersion.component == packComponent)) {
                componentRadioButton.setSelected(true);
            }
        }

        addRow(2, modloaderChoicePanel);
        addRow(1, modloaderVersionLabel, modloaderVersionComboBox);
    }

    private void updateModloader(PackComponent packComponent) {
        selectedModloader = packComponent;
        if(packComponent == null) {
            modloaderVersionLabel.setVisible(false);
            modloaderVersionComboBox.setVisible(false);
        } else {
            modloaderVersionLabel.setVisible(true);
            modloaderVersionComboBox.setVisible(true);
            modloaderVersionLabel.setText(packComponent.iconName.name + " version:");

            modloaderVersionComboBox.setEnabled(false);
            fetchComponentDatas(packComponent, () -> {
                fillComboBoxes(mcVersionComboBox.getEditor().getItem().toString(), modloaderVersionComboBox, packComponent, false);
                modloaderVersionComboBox.setEnabled(true);
            });
        }
    }

    public List<String> getInitArguments() {
        List<String> st = new ArrayList<>();
        st.add("--mc-version");
        st.add(mcVersionComboBox.getEditor().getItem().toString());

        if(selectedModloader != null) {
            st.add("--modloader");
            st.add(selectedModloader.slug);
            st.add("--" + selectedModloader.slug + "-version");
            st.add(modloaderVersionComboBox.getEditor().getItem().toString());
        } else {
            st.add("--modloader");
            st.add("none");
        }

        return st;
    }

    public PackComponentVersion getModloader() {
        return selectedModloader == null ? null : new PackComponentVersion(selectedModloader, modloaderVersionComboBox.toString());
    }

    private void fillComboBoxes(String mcVersion, JComboBox<VersionMetadata> comboBox, PackComponent packComponent, boolean showAllReleaseType) {
        Object oldValue = comboBox.getEditor().getItem().toString();
        comboBox.removeAllItems();

        List<VersionMetadata> metadatas = Caches.componentCaches.get(packComponent);
        if(metadatas == null) {
            comboBox.setEditable(true);
            return;
        }

        List<VersionMetadata> filteredMetadatas = metadatas.stream()
                .filter(e -> {
                   boolean matchReleaseType = showAllReleaseType ? true : e.getState() == VersionMetadata.State.RELEASE;
                   boolean matchMinecraftVersion = e.getMinecraftVersion() == null || e.getMinecraftVersion().equals(mcVersion);
                   return matchReleaseType && matchMinecraftVersion;
                })
                .collect(Collectors.toList());

        int equivalentToOldValueIndex = 0;
        for(int i = 0; i < filteredMetadatas.size(); i++) {
            VersionMetadata metadata = filteredMetadatas.get(i);
            if(metadata.getVersionName().equals(oldValue == null ? "" : oldValue.toString())) equivalentToOldValueIndex = i;
            comboBox.addItem(metadata);
        }

        if(filteredMetadatas.isEmpty()) {
            comboBox.addItem(new VersionMetadata(null, "No version found :(", VersionMetadata.State.RELEASE));
        }

        comboBox.setSelectedIndex(equivalentToOldValueIndex);
    }

    private void fetchComponentDatas(PackComponent packComponent, Runnable callback) {
        if(Caches.componentCaches.get(packComponent) != null) {
            callback.run();
            return;
        }

        try {
            packComponent.versionGetter.get((versionList) -> {
                Caches.componentCaches.put(packComponent, versionList);
                callback.run();
            });
        } catch (Exception e) {
            Caches.componentCaches.put(packComponent, null);
            callback.run();
        }
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = getPreferredSize();
        size.width = Short.MAX_VALUE;
        return size;
    }
}
