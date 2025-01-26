package com.lx862.pwgui.gui.dialog;

import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.components.ManualModEntryPanel;
import com.lx862.pwgui.data.ManualModInfo;
import com.lx862.pwgui.gui.components.fstree.FileSystemWatcher;
import com.lx862.pwgui.gui.components.kui.KButton;
import com.lx862.pwgui.gui.components.kui.KFileChooser;
import com.lx862.pwgui.gui.components.kui.KGridBagLayoutPanel;
import com.lx862.pwgui.util.Util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.nio.file.*;
import java.util.List;
import java.util.function.Consumer;

public class ManualDownloadDialog extends JDialog {
    private final List<ManualModInfo> modList;
    private final JPanel modListPanel;
    private final JLabel watchingPathLabel;
    private final KButton okButton;
    private Path watchingPath;
    private Thread fileWatcherThread;

    public ManualDownloadDialog(JDialog parentDialog, List<ManualModInfo> modList, Consumer<Path> finishCallback) {
        super(parentDialog, Util.withTitlePrefix("Manual Download"), true);
        this.modList = modList;

        setSize(560, 400);
        setLocationRelativeTo(parentDialog);

        JPanel rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.PAGE_AXIS));
        rootPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel titleLabel = new JLabel(String.format("%d file(s) to be manually downloaded", modList.size()));
        titleLabel.setFont(UIManager.getFont("h3.font"));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(titleLabel);

        JLabel descriptionLabel = new JLabel("Due to limitations imposed by API, you need to download the following file(s) manually:");
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(descriptionLabel);

        this.modListPanel = new JPanel();
        this.modListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.modListPanel.setLayout(new BoxLayout(this.modListPanel, BoxLayout.PAGE_AXIS));

        JScrollPane modListScrollPane = new JScrollPane(this.modListPanel);
        modListScrollPane.getVerticalScrollBar().setUnitIncrement(10); // Default is too slow
        rootPanel.add(modListScrollPane);

        JLabel watchingDescriptionLabel = new JLabel("We are watching for new files in the following folder:");
        watchingDescriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(watchingDescriptionLabel);

        KGridBagLayoutPanel monitorLocationPanel = new KGridBagLayoutPanel(2, 2);
        monitorLocationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        this.watchingPathLabel = new JLabel();

        KButton changeWatchPathButton = new KButton("Change...");
        changeWatchPathButton.addActionListener(actionEvent -> {
            KFileChooser fileChooser = new KFileChooser("manual-dl-watcher");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                startWatchDirectory(fileChooser.getSelectedFile().toPath());
            }
        });
        monitorLocationPanel.addRow(1, 0, watchingPathLabel, changeWatchPathButton);

        rootPanel.add(monitorLocationPanel);

        JLabel afterMathLabel = new JLabel("Once all files are downloaded, you can then click \"OK\" and we'll take care the rest!");
        afterMathLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        rootPanel.add(afterMathLabel);

        JPanel actionRowPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actionRowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        this.okButton = new KButton("OK");
        this.okButton.setMnemonic(KeyEvent.VK_O);
        this.okButton.addActionListener(actionEvent -> {
            dispose();
            finishCallback.accept(watchingPath);
        });
        actionRowPanel.add(this.okButton);

        KButton cancelButton = new KButton("Cancel");
        cancelButton.setMnemonic(KeyEvent.VK_C);
        cancelButton.addActionListener(actionEvent -> {
            dispose();
        });
        actionRowPanel.add(cancelButton);

        rootPanel.add(actionRowPanel);
        add(rootPanel);

        startWatchDirectory(getDefaultDownloadDirectory());
    }

    private void startWatchDirectory(Path path) {
        Main.LOGGER.info(String.format("Watching for manually downloaded mods in %s", path));
        if(this.fileWatcherThread != null) {
            this.fileWatcherThread.interrupt();
        }

        this.fileWatcherThread = new Thread(() -> {
            new FileSystemWatcher(path, false, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE).startWatching((wk, e) -> {
                SwingUtilities.invokeLater(this::refreshMissingModList);
            });
        });
        this.fileWatcherThread.start();
        this.watchingPathLabel.setText(String.format("<html>Download location: <b>%s</b></html>", path.toString()));
        this.watchingPath = path;
        refreshMissingModList();
    }

    private void refreshMissingModList() {
        this.modListPanel.removeAll();

        boolean allModFound = true;
        for(ManualModInfo manualModInfo : this.modList) {
            boolean modExists = this.watchingPath.resolve(manualModInfo.fileName).toFile().exists();
            if(!modExists) allModFound = false;
            this.modListPanel.add(new ManualModEntryPanel(manualModInfo, modExists));
        }
        this.okButton.setEnabled(allModFound);
        this.modListPanel.updateUI();
    }

    private static Path getDefaultDownloadDirectory() {
        return Paths.get(System.getProperty("user.home")).resolve("Downloads");
    }

    @Override
    public void dispose() {
        super.dispose();
        if(this.fileWatcherThread != null) this.fileWatcherThread.interrupt();
    }
}
