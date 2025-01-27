package com.lx862.pwgui.gui.frame;

import com.lx862.pwgui.core.Modpack;
import com.lx862.pwgui.executable.Executables;
import com.lx862.pwgui.gui.components.fstree.FileSystemWatcher;
import com.lx862.pwgui.gui.panel.editing.HeaderPanel;
import com.lx862.pwgui.util.Util;
import com.lx862.pwgui.Main;
import com.lx862.pwgui.gui.panel.editing.EditPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.function.BiConsumer;

import static java.nio.file.StandardWatchEventKinds.*;

/** The main Pack Editing UI */
public class EditFrame extends BaseFrame {
    private final EditPanel editPanel;
    private Thread fileWatcherThread;

    public EditFrame(Component parent, Modpack modpack) {
        setTitle(getTitle(modpack));
        setSize(900, 650);
        setLocationRelativeTo(parent);

        Main.getConfig().setLastModpackPath(modpack.getPackFilePath());
        Executables.packwiz.setPackFileLocation(modpack.getRootPath().relativize(modpack.getPackFilePath()).toString());
        Executables.packwiz.changeWorkingDirectory(modpack.getRootPath());

        JPanel rootPanel = new JPanel(new BorderLayout(0, 10));
        rootPanel.setBorder(new EmptyBorder(7, 15, 15, 15));

        HeaderPanel headerBarPanel = new HeaderPanel(modpack.packFile.get());
        rootPanel.add(headerBarPanel, BorderLayout.NORTH);

        editPanel = new EditPanel(modpack);
        rootPanel.add(editPanel, BorderLayout.CENTER);

        add(rootPanel);

        jMenuBar.add(getFileMenu(modpack, editPanel::saveChanges));
        jMenuBar.add(getEditMenu(modpack));
        jMenuBar.add(getToolMenu());
        jMenuBar.add(getHelpMenu());

        startWatchFile(modpack, (kind, path) -> {
            editPanel.onFileChange(kind, path);

            if(kind == ENTRY_CREATE || kind == ENTRY_MODIFY) {
                if(path.equals(modpack.getPackFilePath())) {
                    modpack.packFile.clearCache();
                    headerBarPanel.initialize(modpack.packFile.get()); // Update header with new info
                    setTitle(getTitle(modpack));
                } else if(path.equals(modpack.packFile.get().getIndexPath())) {
                    modpack.packFile.get().packIndexFile.clearCache();
                }
            }
        });

        registerKeyboardShortcut();
    }

    private String getTitle(Modpack modpack) {
        return Util.withTitlePrefix(String.format("Editing %s", modpack.packFile.get().name));
    }

    private void registerKeyboardShortcut() {
        getRootPane().registerKeyboardAction(actionEvent -> {
            editPanel.saveChanges(false);
        }, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    private void startWatchFile(Modpack modpack, BiConsumer<WatchEvent.Kind<?>, Path> callback) {
        this.fileWatcherThread = new Thread(() -> {
            FileSystemWatcher watcher = new FileSystemWatcher(modpack.getRootPath(), true, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            watcher.startWatching((watchKey, e) -> {
                Path directory = (Path)watchKey.watchable();
                WatchEvent.Kind<?> kind = e.kind();
                final Path path = directory.resolve(((WatchEvent<Path>)e).context());

                SwingUtilities.invokeLater(() -> callback.accept(kind, path));
            });
        });
        fileWatcherThread.start();
    }

    @Override
    public void dispose() {
        editPanel.saveChanges(true);
        super.dispose();
        fileWatcherThread.interrupt();
        Executables.dispose();
    }
}
