package de.leanovate.jbj.utils.layeredfs;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LayeredRelativePath extends LayeredAbstractPath {
    public LayeredRelativePath(LayeredFileSystem fileSystem, String... nameELements) {
        this(fileSystem, Arrays.asList(nameELements));
    }

    public LayeredRelativePath(LayeredFileSystem fileSystem, List<String> nameElements) {
        super(fileSystem, nameElements);
    }

    @Override
    public boolean isAbsolute() {
        return false;
    }

    @Override
    public Path getRoot() {
        return null;
    }

    @Override
    public Path getParent() {
        if (nameElements.size() <= 1)
            return null;

        List<String> subList = nameElements.subList(0, nameElements.size() - 1);
        return new LayeredRelativePath(fileSystem, subList);
    }

    @Override
    public boolean startsWith(Path other) {
        if (other.isAbsolute()) {
            return false;
        }

        // named roots under windows
        if (!this.getRoot().startsWith(other.getRoot())) {
            return false;
        }

        if (other instanceof LayeredAbstractPath) {
            LayeredAbstractPath otherPath = (LayeredAbstractPath) other;
            int otherNameCount = otherPath.getNameCount();
            if (otherNameCount > this.getNameCount()) {
                return false;
            }
            // otherNameCount smaller or equal to this.getNameCount()
            for (int i = 0; i < otherNameCount; ++i) {
                String thisElement = nameElements.get(i);
                String otherElement = otherPath.nameElements.get(i);
                if (!thisElement.equals(otherElement)) {
                    return false;
                }
            }
            return true;
        } else {
            throw new IllegalArgumentException("can't check for #startsWith against " + other);
        }
    }

    @Override
    public boolean endsWith(Path other) {
        if (other.isAbsolute()) {
            return false;
        }

        if (other instanceof LayeredAbstractPath) {
            LayeredAbstractPath otherPath = (LayeredAbstractPath) other;
            int otherNameCount = otherPath.getNameCount();
            int thisNameCount = this.getNameCount();
            if (otherNameCount == 0) {
                // empty path
                return false;
            }

            if (otherNameCount > thisNameCount) {
                return false;
            }
            // otherNameCount smaller or equal to this.getNameCount()
            int offset = thisNameCount - otherNameCount;
            for (int i = 0; i < otherNameCount; ++i) {
                String thisElement = nameElements.get(i + offset);
                String otherElement = otherPath.nameElements.get(i);
                if (!thisElement.equals(otherElement)) {
                    return false;
                }
            }
            return true;
        } else {
            throw new IllegalArgumentException("can't check for #startsWith against " + other);
        }
    }

    @Override
    public Path relativize(Path other) {
        if (other.isAbsolute()) {
            // only support relativization against relative paths
            throw new IllegalArgumentException("can only relativize a relative path against a relative path");
        }
        return super.relativize(other);
    }

    @Override
    public Path toAbsolutePath() {
        return new LayeredAbsolutePath(fileSystem, nameElements);
    }

    @Override
    protected Path newInstance(List<String> pathElements) {
        return new LayeredRelativePath(fileSystem, pathElements);
    }

    @Override
    protected List<String> handleDotDotNormalizationNotYetModified(List<String> nameElements, int nameElementsSize, int i) {
        // copy everything preceding the element before ".." unless it's ".."
        if (i > 0 && !nameElements.get(i - 1).equals("..")) {
            List<String> normalized = new ArrayList<>(nameElementsSize - 1);
            if (i > 1) {
                normalized.addAll(nameElements.subList(0, i - 1));
            }
            return normalized;
        } else {
            return nameElements;
        }
    }

    @Override
    protected List<String> handleSingleDotDot(List<String> normalized) {
        return normalized;
    }

    @Override
    protected void handleDotDotNormalizationAlreadyModified(List<String> normalized) {
        int lastIndex = normalized.size() - 1;
        if (!normalized.get(lastIndex).equals("..")) {
            // "../.." has to be preserved
            normalized.remove(lastIndex);
        } else {
            // if there is already a ".." just add a ".."
            normalized.add("..");
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileSystem, nameElements);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final LayeredAbstractPath other = (LayeredAbstractPath) obj;
        return Objects.equals(this.fileSystem, other.fileSystem) && Objects.equals(this.nameElements, other.nameElements);
    }

    @Override
    public String toString() {
        String delim = "";
        StringBuilder sb = new StringBuilder();
        for (String name : nameElements) {
            sb.append(delim).append(name);
            delim = "/";
        }
        return sb.toString();
    }
}
