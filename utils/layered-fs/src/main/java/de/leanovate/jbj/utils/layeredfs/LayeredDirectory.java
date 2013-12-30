package de.leanovate.jbj.utils.layeredfs;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

public class LayeredDirectory implements LayeredElement {
    private final LayeredAbsolutePath absolutePath;
    private final List<LayeredElement> elements = new ArrayList<>();

    public LayeredDirectory(LayeredAbsolutePath absolutePath) {
        this.absolutePath = absolutePath;
    }

    @Override
    public LayeredAbsolutePath getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        checkPath(path);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>[] attrs) throws IOException {
        checkPath(path);
        throw new FileSystemException(absolutePath.toString(), null, "is not a file");
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        final List<Path> result = new ArrayList<>();
        for (LayeredElement element : elements) {
            Path path = absolutePath.relativize(element.getAbsolutePath());
            if (filter != null && filter.accept(path))
                result.add(path);
        }
        return new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                return result.iterator();
            }

            @Override
            public void close() throws IOException {
            }
        };
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>[] attrs) throws IOException {
        checkPath(dir.getParent());
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Path path) throws IOException {
        checkPath(path);
        throw new UnsupportedOperationException();
    }

    @Override
    public void copy(Path source, Path target, CopyOption[] options) throws IOException {
        checkPath(source);
        checkPath(target);
        throw new IllegalArgumentException("Cannot copy from " + source + " to " + target);
    }

    @Override
    public void move(Path source, Path target, CopyOption[] options) throws IOException {
        checkPath(source);
        checkPath(target);
        throw new IllegalArgumentException("Cannot move from " + source + " to " + target);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        checkPath(path);
        checkPath(path2);
        return true;
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        checkPath(path);
        return false;
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return new LayeredFileStore();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption[] options) {
        if (type.equals(BasicFileAttributeView.class))
            return (V) new DirectoryAttributeView();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        checkPath(path);
        if (type.equals(BasicFileAttributes.class))
            return (A) new DirectoryAttributes();
        else
            throw new IllegalArgumentException("unsupported attributes type " + type);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        checkPath(path);
        Map<String, Object> result = new HashMap<>();
        result.put("regularFile", false);
        result.put("directory", true);
        result.put("symbolicLink", false);
        result.put("other", false);
        result.put("size", 0L);
        result.put("hidden", false);
        return result;
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption[] options) throws IOException {
        checkPath(path);
        throw new UnsupportedOperationException();
    }

    private void checkPath(Path path) throws NoSuchFileException {
        Path relativePath = absolutePath.relativize(path);

        if (relativePath.getNameCount() > 0)
            throw new NoSuchFileException(absolutePath.resolve(path).toString());
    }

    public void addElement(LayeredElement current) {
        elements.add(current);
    }

    static class DirectoryAttributes implements BasicFileAttributes {

        @Override
        public FileTime lastModifiedTime() {
            return null;
        }

        @Override
        public FileTime lastAccessTime() {
            return null;
        }

        @Override
        public FileTime creationTime() {
            return null;
        }

        @Override
        public boolean isRegularFile() {
            return false;
        }

        @Override
        public boolean isDirectory() {
            return true;
        }

        @Override
        public boolean isSymbolicLink() {
            return false;
        }

        @Override
        public boolean isOther() {
            return false;
        }

        @Override
        public long size() {
            return 0;
        }

        @Override
        public Object fileKey() {
            return null;
        }
    }

    static class DirectoryAttributeView implements BasicFileAttributeView {

        @Override
        public String name() {
            return "basic";
        }

        @Override
        public BasicFileAttributes readAttributes() throws IOException {
            return new DirectoryAttributes();
        }

        @Override
        public void setTimes(FileTime lastModifiedTime, FileTime lastAccessTime, FileTime createTime) throws IOException {
        }
    }
}
