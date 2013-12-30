package de.leanovate.jbj.utils.layeredfs;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

import static java.lang.Math.min;

public abstract class LayeredAbstractPath implements Path {
    protected final LayeredFileSystem fileSystem;
    final List<String> nameElements;

    public LayeredAbstractPath(LayeredFileSystem fileSystem, List<String> nameElements) {
        this.fileSystem = fileSystem;
        this.nameElements = nameElements;
    }

    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    @Override
    public Path getFileName() {
        if (nameElements.isEmpty())
            return null;
        String lastElement = nameElements.get(nameElements.size() - 1);
        return new LayeredRelativePath(fileSystem, lastElement);
    }

    @Override
    public int getNameCount() {
        return this.nameElements.size();
    }

    @Override
    public Path getName(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be positive but was " + index);
        }
        if (index >= this.getNameCount()) {
            throw new IllegalArgumentException("index must not be bigger than " + (this.getNameCount() - 1) + " but was " + index);
        }
        return new LayeredRelativePath(fileSystem, nameElements.get(index));
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        if (endIndex - beginIndex == this.getNameCount()) {
            return new LayeredRelativePath(fileSystem, nameElements);
        } else {
            return new LayeredRelativePath(fileSystem, nameElements.subList(beginIndex, endIndex));
        }
    }

    @Override
    public boolean startsWith(String other) {
        Path path = fileSystem.getPath(other);
        return startsWith(path);
    }

    @Override
    public boolean endsWith(String other) {
        Path path = fileSystem.getPath(other);
        return endsWith(path);
    }

    @Override
    public Path normalize() {
        int nameElementsSize = nameElements.size();
        List<String> normalized = nameElements;
        boolean modified = false;

        for (int i = 0; i < nameElementsSize; ++i) {
            String each = nameElements.get(i);

            if (each.equals(".")) {
                if (!modified) {
                    if (nameElementsSize == 1) {
                        // path is just "."
                        normalized = Collections.emptyList();
                        modified = true;
                        break;
                    }
                    if (nameElementsSize == 2) {
                        // path is either "/a/." or "/./a"
                        String element = i == 0 ? nameElements.get(1) : nameElements.get(0);
                        normalized = Collections.singletonList(element);
                        modified = true;
                        break;
                    }

                    // copy everything preceding a
                    normalized = new ArrayList<>(nameElementsSize - 1);
                    if (i > 0) {
                        normalized.addAll(nameElements.subList(0, i));
                    }
                    modified = true;
                }

                // ignore
                continue;
            }

            if (each.equals("..")) {
                if (modified) {
                    // just remove the last entry if possible
                    if (!normalized.isEmpty()) {
                        this.handleDotDotNormalizationAlreadyModified(normalized);
                    }
                } else {
                    if (nameElementsSize == 1) {
                        // path is just "/.."
                        normalized = this.handleSingleDotDot(normalized);
                        modified = normalized != nameElements;
                        break;
                    } else {
                        normalized = this.handleDotDotNormalizationNotYetModified(nameElements, nameElementsSize, i);
                        modified = normalized != nameElements;
                    }
                }
                continue;
            }

            if (modified) {
                normalized.add(each);
            }

        }
        if (modified) {
            return newInstance(normalized);
        } else {
            return this;
        }
    }

    @Override
    public Path resolve(Path other) {

        if (!(other instanceof LayeredAbstractPath))
            throw new IllegalArgumentException("can't resolve  against " + other);

        LayeredAbstractPath otherPath = (LayeredAbstractPath) other;
        if (other.isAbsolute()) {
            return other;
        } else if (otherPath.getNameCount() == 0) {
            return this;
        }

        List<String> newNameElements = new ArrayList<>();
        newNameElements.addAll(nameElements);
        newNameElements.addAll(otherPath.nameElements);
        return newInstance(newNameElements);
    }

    @Override
    public Path resolve(String other) {
        Path path = fileSystem.getPath(other);
        return resolve(path);
    }

    @Override
    public Path resolveSibling(Path other) {
        if (other.isAbsolute()) {
            return other;
        }
        return resolve(other);
    }

    @Override
    public Path resolveSibling(String other) {
        Path path = fileSystem.getPath(other);
        return resolveSibling(path);
    }

    @Override
    public Path relativize(Path other) {
        if (other instanceof LayeredAbstractPath) {
            LayeredAbstractPath otherPath = (LayeredAbstractPath) other;
            int firstDifferenceIndex = firstDifferenceIndex(nameElements, otherPath.nameElements);
            List<String> newNameElements = new ArrayList<>();
            if (firstDifferenceIndex < this.getNameCount()) {
                for (int i = 0; i < getNameCount() - firstDifferenceIndex; i++)
                    newNameElements.add("..");
            }
            if (firstDifferenceIndex < other.getNameCount()) {
                for (String name : otherPath.nameElements.subList(firstDifferenceIndex, otherPath.getNameCount()))
                    newNameElements.add(name);
            }
            return new LayeredRelativePath(fileSystem, newNameElements);
        } else {
            // unknown case
            throw new IllegalArgumentException("unsupported path argument");
        }
    }

    @Override
    public URI toUri() {
        URI base = fileSystem.getUri();

        try {
            return new URI(base.getScheme(), base.getUserInfo(), base.getHost(), base.getPort(), base.getPath(), null, toString());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("invalid base uri: " + base);
        }
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return toAbsolutePath();
    }

    @Override
    public File toFile() {
        return null;
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return null;
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return null;
    }

    @Override
    public Iterator<Path> iterator() {
        return null;
    }

    @Override
    public int compareTo(Path other) {
        if (other == null || !(other instanceof LayeredAbstractPath))
            return -1;

        int thisSize = nameElements.size();
        List<String> otherElements = ((LayeredAbstractPath) other).nameElements;
        int otherSize = otherElements.size();
        for (int i = 0; i < thisSize; i++) {
            if (i == otherSize) {
                // bail out before accessing
                return 1;
            }
            String thisElement = this.nameElements.get(i);
            String otherElement = otherElements.get(i);
            int comparison = thisElement.compareTo(otherElement);
            if (comparison != 0) {
                return comparison;
            }
        }
        if (otherSize > thisSize) {
            return -1;
        } else {
            return 0;
        }
    }

    protected abstract Path newInstance(List<String> pathElements);

    protected abstract List<String> handleDotDotNormalizationNotYetModified(List<String> nameElements, int nameElementsSize, int i);

    protected abstract List<String> handleSingleDotDot(List<String> normalized);

    protected abstract void handleDotDotNormalizationAlreadyModified(List<String> normalized);

    protected int firstDifferenceIndex(List<?> l1, List<?> l2) {
        int endIndex = min(l1.size(), l2.size());
        for (int i = 0; i < endIndex; ++i) {
            if (!l1.get(i).equals(l2.get(i))) {
                return i;
            }
        }
        return endIndex;
    }
}
