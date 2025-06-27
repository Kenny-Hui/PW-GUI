package com.lx862.pwgui.gui.components.fstree;

import com.lx862.pwgui.core.data.model.file.FileSystemEntityModel;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.nio.file.Path;

public class FileSystemSortedTreeNode extends DefaultMutableTreeNode implements Comparable<FileSystemSortedTreeNode> {
    public final String name;
    public final Path path;

    public FileSystemSortedTreeNode(FileSystemEntityModel model) {
        this.path = model.path;
        this.name = model.name;
        setUserObject(model);
    }

    @Override
    public String toString() {
        return String.format("FileSystemSortedTreeNode[name=%s, path=%s]", name, path);
    }

    public void addAndSort(MutableTreeNode newChild) {
        super.add(newChild);
        sort();
    }

    public boolean containsNode(MutableTreeNode node) {
        return children != null && children.contains(node);
    }

    public void sort() {
        if(this.children != null) {
            this.children.sort(null);
        }
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
