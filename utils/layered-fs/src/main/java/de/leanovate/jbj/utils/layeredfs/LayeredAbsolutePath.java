package de.leanovate.jbj.utils.layeredfs;

import java.nio.file.Path;
import java.util.*;

public class LayeredAbsolutePath extends LayeredAbstractPath {
    public LayeredAbsolutePath(LayeredFileSystem fileSystem, String... nameELements) {
        this(fileSystem, Arrays.asList(nameELements));
    }

    public LayeredAbsolutePath(LayeredFileSystem fileSystem, List<String> nameElements) {
        super(fileSystem, nameElements);
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }

    @Override
    public Path getRoot() {
        return new LayeredAbsolutePath(fileSystem);
    }

    @Override
    public Path getParent() {
        if (nameElements.isEmpty())
            return null;

        List<String> subList = nameElements.subList(0, nameElements.size() - 1);
        return new LayeredAbsolutePath(fileSystem, subList);
    }

    @Override
    public boolean startsWith(Path other) {
        if (!other.isAbsolute()) {
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
            return this.equals(other);
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
    public Path toAbsolutePath() {
        return this;
    }

    @Override
    protected Path newInstance(List<String> pathElements) {
        return new LayeredAbsolutePath(fileSystem, pathElements);
    }

    @Override
    protected List<String> handleDotDotNormalizationNotYetModified(List<String> nameElements, int nameElementsSize, int i) {
        List<String> normalized = new ArrayList<>(nameElementsSize - 1);
        if (i > 1) {
            normalized.addAll(nameElements.subList(0, i - 1));
        }
        return normalized;
    }

    @Override
    protected List<String> handleSingleDotDot(List<String> normalized) {
        return Collections.emptyList();
    }

    @Override
    protected void handleDotDotNormalizationAlreadyModified(List<String> normalized) {
        normalized.remove(normalized.size() - 1);
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
        if (nameElements.isEmpty())
            return "/";

        StringBuilder sb = new StringBuilder();

        for (String name : nameElements)
            sb.append("/").append(name);

        return sb.toString();
    }
}
