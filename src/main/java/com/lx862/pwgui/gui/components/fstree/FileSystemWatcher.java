package com.lx862.pwgui.gui.components.fstree;

import com.lx862.pwgui.Main;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.function.BiConsumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;

public class FileSystemWatcher {
    private final Path path;
    private final WatchEvent.Kind<?>[] watchKinds;
    private final boolean recursive;
    private final HashMap<Path, WatchKey> watchKeys;
    private WatchService ws;

    public FileSystemWatcher(Path path, boolean recursive, WatchEvent.Kind<?> ...watchKinds) {
        this.path = path;
        this.recursive = recursive;
        this.watchKinds = watchKinds;
        this.watchKeys = new HashMap<>();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void startWatching(BiConsumer<WatchKey, WatchEvent<?>> callback) {
        FileSystem fs = FileSystems.getDefault();
        try {
            this.ws = fs.newWatchService();

            if(recursive) {
                Files.walkFileTree(this.path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if(dir.toFile().getName().equals(".git")) return FileVisitResult.SKIP_SUBTREE; // git directory has way too many files, we are not interested in it anyway

                    watchKeys.put(dir, dir.register(ws, watchKinds));
                    return FileVisitResult.CONTINUE;
                    }
                });
            } else {
                watchKeys.put(path, path.register(this.ws, this.watchKinds));
            }

            while(true) {
                WatchKey wk = ws.take();
                Thread.sleep(40); // A bit of a hack since some files may not have finished writing by other programs.

                for (WatchEvent<?> e : wk.pollEvents())
                {
                    WatchEvent.Kind<?> kind = e.kind();
                    Path parentDirectory = (Path)wk.watchable();
                    Path filePath = parentDirectory.resolve(((WatchEvent<Path>)e).context());

                    if(recursive && kind == ENTRY_CREATE) {
                        if(Files.isDirectory(filePath)) {
                            watchKeys.put(filePath, filePath.register(ws, watchKinds)); // Watch for our new folder
                        }
                    }

                    if(kind == ENTRY_DELETE) {
                        WatchKey existingWatchKey = watchKeys.get(filePath);
                        if(existingWatchKey != null) {
                            existingWatchKey.cancel(); // Unregister our old entry
                            watchKeys.remove(path);
                        }
                    }
                    callback.accept(wk, e);
                }
                wk.reset();
            }
        } catch (IOException e) {
            Main.LOGGER.exception(e);
        } catch (InterruptedException ignored) {
        }
    }
}
