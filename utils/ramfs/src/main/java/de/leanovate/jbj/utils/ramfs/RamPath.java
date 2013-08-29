package de.leanovate.jbj.utils.ramfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Iterator;

public class RamPath implements Path {
    private final RamFileSystem fileSystem;

    RamPath(RamFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public boolean isAbsolute() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getRoot() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getFileName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getParent() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getNameCount() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getName(int index) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean startsWith(Path other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean startsWith(String other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean endsWith(Path other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean endsWith(String other) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path normalize() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolve(Path other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolve(String other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolveSibling(Path other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path resolveSibling(String other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path relativize(Path other) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public URI toUri() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path toAbsolutePath() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public File toFile() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterator<Path> iterator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int compareTo(Path other) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
