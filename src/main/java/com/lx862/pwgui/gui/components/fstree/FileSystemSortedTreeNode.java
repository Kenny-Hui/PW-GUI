package com.lx862.pwgui.gui.components.fstree;

import com.lx862.pwgui.data.model.file.FileSystemEntityModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class FileSystemSortedTreeNode extends DefaultMutableTreeNode implements Comparable<FileSystemSortedTreeNode> {
    public final String name;
    public final Path path;

    public FileSystemSortedTreeNode(FileSystemEntityModel model) {
        this.path = model.path;
        this.name = model.name;
        setUserObject(model);
    }

    protected <T> List<T> addToList(List<T> existingList, T item) {
        existingList.add(item);
        return existingList;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void add(MutableTreeNode newChild) {
        super.add(newChild);
        Collections.sort(this.children);
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(!(other instanceof FileSystemSortedTreeNode)) return false;

        return path.equals(((FileSystemSortedTreeNode)other).path);
    }

    @Override
    public int compareTo(FileSystemSortedTreeNode other) {
        int bl1 = Boolean.compare(other.path.toFile().isDirectory(), path.toFile().isDirectory());
        if(bl1 == 0) {
            return name.compareToIgnoreCase(other.name);
        } else {
            return bl1;
        }
    }
}
