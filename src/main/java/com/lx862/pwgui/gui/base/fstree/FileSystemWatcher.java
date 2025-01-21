package com.lx862.pwgui.gui.base.fstree;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.*;

public class FileSystemWatcher {
    private final Path path;
    private WatchService ws;
    private final WatchEvent.Kind<?>[] watchKinds;
    private final boolean recursive;

    public FileSystemWatcher(Path path, boolean recursive, WatchEvent.Kind<?> ...watchKinds) {
        this.path = path;
        this.recursive = recursive;
        this.watchKinds = watchKinds;
    }

    public void startWatching(Consumer<WatchKey> callback) {
        FileSystem fs = FileSystems.getDefault();
        try {
            this.ws = fs.newWatchService();

            if(recursive) {
                Files.walkFileTree(this.path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if(dir.toFile().getName().equals(".git")) return FileVisitResult.SKIP_SUBTREE; // git has way too many files, we are not interested in it anyway

                    dir.register(ws, watchKinds);
                    return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                this.path.register(this.ws, this.watchKinds);
            }

            while(true) {
                WatchKey wk = ws.take();
                callback.accept(wk);
                wk.reset();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException ignored) {
        }
    }
}
