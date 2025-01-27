package com.lx862.pwgui.gui.components.fstree;

import com.lx862.pwgui.data.model.GitIgnoreRules;
import com.lx862.pwgui.data.model.file.FileSystemEntityModel;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.io.File;
import java.nio.file.*;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static java.nio.file.StandardWatchEventKinds.*;

/* A JTree representing a directory view/file browser. */
public class FileSystemTree extends JTree {
    private final Function<File, FileSystemEntityModel> getModel;
    private GitIgnoreRules ignorePattern;
    public boolean fsLock; // A slight hack to signal to others when a file is changed

    public FileSystemTree(Path root, Function<File, FileSystemEntityModel> getModel) {
        super();
        this.getModel = getModel;
        setModel(new DefaultTreeModel(generateRecursiveTree(root), false));
        setRootVisible(false);
        setShowsRootHandles(true);
    }

    public GitIgnoreRules getIgnorePattern() {
        return ignorePattern;
    }

    public void setIgnorePattern(GitIgnoreRules pattern) {
        this.ignorePattern = pattern;
        repaint();
    }

    private FileSystemSortedTreeNode generateRecursiveTree(Path root) {
        return generateRecursiveTree(new FileSystemSortedTreeNode(getModel.apply(root.toFile())));
    }

    private FileSystemSortedTreeNode generateRecursiveTree(FileSystemSortedTreeNode rootNode) {
        File[] files = Objects.requireNonNull(rootNode.path.toFile().listFiles());

        for(File file : files) {
            if(file.isDirectory() && !file.getName().equals(".git")) {
                FileSystemEntityModel child = getModel.apply(file);
                FileSystemSortedTreeNode node = generateRecursiveTree(new FileSystemSortedTreeNode(child));
                rootNode.add(node);
            } else {
                FileSystemEntityModel child = getModel.apply(file);
                rootNode.add(new FileSystemSortedTreeNode(child));
            }
        }
        return rootNode;
    }

    /* This is used to notify that a file has been created/modified/removed for live update purposes.
    * You are expected to bring your own FileWatcher to the table :) */
    public void onFileChange(WatchEvent.Kind<?> kind, Path filePath) {
        if(kind == ENTRY_CREATE) {
            addNode(filePath);
        } else if(kind == ENTRY_MODIFY) {
            FileSystemEntityModel newNode = getModel.apply(filePath.toFile());
            modifyNode(filePath, newNode);
        } else {
            removeNode(filePath);
        }
    }

    private void addNode(Path target) {
        Path parent = target.getParent();
        FileSystemSortedTreeNode newNode = new FileSystemSortedTreeNode(getModel.apply(target.toFile()));

        iterateTree((node) -> {
            if(node.path.equals(parent)) {
                node.add(newNode);
                int idx = node.getIndex(newNode);
                ((DefaultTreeModel)getModel()).nodesWereInserted(node, new int[]{idx});
            }
        });
    }

    private void modifyNode(Path target, FileSystemEntityModel newFileType) {
        iterateTree((node) -> {
            if(node.path.equals(target)) {
                TreePath treePath = new TreePath(node.getPath());
                node.setUserObject(newFileType);

                if(treePath.equals(getSelectionPath())) { // Currently selected, we need to reload
                    TreeSelectionEvent selectionEvent = new TreeSelectionEvent(this, getSelectionPath(), false, null, null);
                    fsLock = true;
                    fireValueChanged(selectionEvent); // Re-trigger selection even to update main UI
                    fsLock = false;
                }
            }
        });
    }

    private void removeNode(Path target) {
        iterateTree((node) -> {
            if(node.path.equals(target)) {
                ((DefaultTreeModel)getModel()).removeNodeFromParent(node);
            }
        });
    }

    private void iterateTree(Consumer<FileSystemSortedTreeNode> callback) {
        iterateTree((FileSystemSortedTreeNode)getModel().getRoot(), callback);
    }

    private void iterateTree(FileSystemSortedTreeNode node, Consumer<FileSystemSortedTreeNode> callback) {
        callback.accept(node); // This should be directory
        int childNodeCount = node.getChildCount();

        for(int i = 0; i < childNodeCount; i++) {
            if(i >= node.getChildCount()) break;
            FileSystemSortedTreeNode childNode = (FileSystemSortedTreeNode) node.getChildAt(i);
            if (childNode.getChildCount() > 0) {
                iterateTree(childNode, callback);
            } else {
                callback.accept(childNode);
            }
        }
    }
}
