package com.lx862.pwgui.gui.panel;

import com.lx862.pwgui.data.*;
import com.lx862.pwgui.gui.base.kui.KComboBox;
import com.lx862.pwgui.gui.base.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.util.GUIHelper;
import com.lx862.pwgui.data.Caches;
import com.lx862.pwgui.core.PackFile;
import com.lx862.pwgui.gui.base.WrapLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModpackVersionPanel extends KGridBagLayoutPanel {
    private final KComboBox<VersionMetadata> mcVersionComboBox;
    private final JLabel modloaderVersionLabel;
    private final KComboBox<VersionMetadata> modloaderVersionComboBox;

    private PackComponentVersion initialMinecraft;
    private PackComponentVersion initialModloader;

    private PackComponent selectedModloader;

    public ModpackVersionPanel(PackFile existingFile, Runnable updateSaveState) {
        super(3, 2);

        if(existingFile != null) {
            this.initialMinecraft = existingFile.getComponent(PackComponent.MINECRAFT);
            this.initialModloader = existingFile.getModloader();
            if(this.initialModloader != null) this.selectedModloader = this.initialModloader.getComponent();
        }

        modloaderVersionLabel = new JLabel("Modloader Version: ");
        modloaderVersionComboBox = new KComboBox<>();
        modloaderVersionComboBox.setEditable(true);
        modloaderVersionComboBox.addItemListener(e -> {
            if(existingFile != null) {
                existingFile.setModloader(getModloader());
                updateSaveState.run();
            }
        });

        mcVersionComboBox = new KComboBox<>();
        mcVersionComboBox.setEditable(true);
        mcVersionComboBox.addItemListener(e -> {
            if(existingFile != null) {
                existingFile.setMinecraft(getMinecraft());
                updateSaveState.run();
            }
        });

        if(this.initialMinecraft != null) mcVersionComboBox.setSelectedItem(this.initialMinecraft.getVersion());
        if(this.initialModloader != null) modloaderVersionComboBox.setSelectedItem(initialModloader.getVersion());

        updateMinecraftUI(false);

        addRow(1, new JLabel("Minecraft Version: ", new ImageIcon(GUIHelper.resizeImage(IconNamePair.MINECRAFT.image, 20)), SwingConstants.LEFT), mcVersionComboBox);

        JCheckBox showSnapshotCheckBox = new JCheckBox("Show Snapshot");
        showSnapshotCheckBox.addActionListener(actionEvent -> updateMinecraftUI(showSnapshotCheckBox.isSelected()));
        addRow(1, null, showSnapshotCheckBox);

        JPanel modloaderChoicePanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 0, 0));

        modloaderChoicePanel.add(new JLabel("Modloader: "));
        ButtonGroup modloadersButtonGroup = new ButtonGroup();

        JRadioButton vanillaRadioButton = new JRadioButton("None (Vanilla)");
        vanillaRadioButton.addActionListener((itemListener) -> {
            updateModloaderUI(null);
            if(existingFile != null) {
                existingFile.setModloader(null);
                updateSaveState.run();
            }
        });
        modloadersButtonGroup.add(vanillaRadioButton);
        modloadersButtonGroup.setSelected(vanillaRadioButton.getModel(), true);
        modloaderChoicePanel.add(vanillaRadioButton);

        for(PackComponent packComponent : PackComponent.values()) {
            if(!packComponent.choosable) continue;

            JRadioButton componentRadioButton = new JRadioButton(packComponent.iconName.name);
            componentRadioButton.addActionListener((itemListener) -> {
                updateModloaderUI(packComponent);
            });
            componentRadioButton.setIcon(new ImageIcon(GUIHelper.fadeImage(GUIHelper.resizeImage(packComponent.iconName.image, 18), 0.5f)));
            componentRadioButton.setSelectedIcon(new ImageIcon(GUIHelper.resizeImage(packComponent.iconName.image, 18)));
            modloaderChoicePanel.add(componentRadioButton);
            modloadersButtonGroup.add(componentRadioButton);

            if(initialModloader != null && initialModloader.getComponent() == packComponent) {
                componentRadioButton.setSelected(true);
            }
        }

        addRow(2, modloaderChoicePanel);
        addRow(1, modloaderVersionLabel, modloaderVersionComboBox);

        updateModloaderUI(this.initialModloader == null ? null : this.initialModloader.getComponent());
    }

    private void updateMinecraftUI(boolean showSnapshot) {
        fetchComponentDatas(PackComponent.MINECRAFT, () -> {
            fillComboBoxes(null, mcVersionComboBox, PackComponent.MINECRAFT, showSnapshot);
            // We add event listener after we fill the combobox, as we don't want the fill update to be triggered
            mcVersionComboBox.setAction(null);
            mcVersionComboBox.addActionListener(actionEvent -> {
                updateModloaderUI(selectedModloader);
            });
        });
    }

    private void updateModloaderUI(PackComponent modloader) {
        selectedModloader = modloader;

        if(modloader == null) {
            modloaderVersionLabel.setVisible(false);
            modloaderVersionComboBox.setVisible(false);
        } else {
            modloaderVersionLabel.setVisible(true);
            modloaderVersionComboBox.setVisible(true);
            modloaderVersionLabel.setText(modloader.iconName.name + " version:");

            modloaderVersionComboBox.setEnabled(false);
            fetchComponentDatas(modloader, () -> {
                fillComboBoxes(mcVersionComboBox.getEditor().getItem().toString(), modloaderVersionComboBox, modloader, false);
                modloaderVersionComboBox.setEnabled(true);
            });
        }
    }

    public List<String> getInitArguments() {
        List<String> st = new ArrayList<>();
        st.add("--mc-version");
        st.add(getMinecraft().getVersion());

        PackComponentVersion modloader = getModloader();
        if(modloader != null) {
            st.add("--modloader");
            st.add(modloader.getComponent().slug);
            st.add("--" + modloader.getComponent().slug + "-version");
            st.add(modloader.getVersion());
        } else {
            st.add("--modloader");
            st.add("none");
        }

        return st;
    }

    public PackComponentVersion getMinecraft() {
        return new PackComponentVersion(PackComponent.MINECRAFT, mcVersionComboBox.getEditor().getItem().toString());
    }

    public PackComponentVersion getModloader() {
        return selectedModloader == null ? null : new PackComponentVersion(selectedModloader, modloaderVersionComboBox.getEditor().getItem().toString());
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

    public boolean minecraftVersionChanged() {
        return !Objects.equals(initialMinecraft, getMinecraft());
    }

    public boolean shouldSave() {
        return !Objects.equals(getMinecraft(), this.initialMinecraft) || !Objects.equals(getModloader(), this.initialModloader);
    }

    @Override
    public Dimension getMaximumSize() {
        Dimension size = getPreferredSize();
        size.width = Short.MAX_VALUE;
        return size;
    }
}
