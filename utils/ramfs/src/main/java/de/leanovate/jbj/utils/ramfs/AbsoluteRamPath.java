/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.utils.ramfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class AbsoluteRamPath implements Path {
    private final RamFileSystem fileSystem;
    private final List<String> names;

    AbsoluteRamPath(final RamFileSystem fileSystem, final List<String> names) {
        this.fileSystem = fileSystem;
        this.names = names;
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }

    @Override
    public Path getRoot() {
        return new AbsoluteRamPath(fileSystem, Collections.<String>emptyList());
    }

    @Override
    public Path getFileName() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Path getParent() {
        if (names.isEmpty())
            return null;
        return new AbsoluteRamPath(fileSystem, names.subList(0, names.size() - 1));
    }

    @Override
    public int getNameCount() {
        return names.size();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbsoluteRamPath other = (AbsoluteRamPath) o;

        return Objects.equals(fileSystem, other.fileSystem) && Objects.equals(names, other.names);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileSystem, names);
    }

    @Override
    public int compareTo(Path other) {
        return toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (names.isEmpty())
            sb.append("/");
        else
            for (String name : names)
                sb.append("/").append(name);

        return sb.toString();
    }
}
