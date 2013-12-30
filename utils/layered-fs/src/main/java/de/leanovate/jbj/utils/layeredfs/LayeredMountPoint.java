package de.leanovate.jbj.utils.layeredfs;

import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

public class LayeredMountPoint implements LayeredElement {
    private final LayeredAbsolutePath absolutePath;
    private final Path originalPath;
    private final FileSystemProvider originalProvider;

    public LayeredMountPoint(LayeredAbsolutePath absolutePath, Path originalPath) {
        this.absolutePath = absolutePath;
        this.originalPath = originalPath;
        this.originalProvider = originalPath.getFileSystem().provider();
    }

    @Override
    public LayeredAbsolutePath getAbsolutePath() {
        return absolutePath;
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        originalProvider.checkAccess(toOriginal(path), modes);
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>[] attrs) throws IOException {
        return originalProvider.newByteChannel(toOriginal(path), options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        final Path originalDir = toOriginal(dir);
        final DirectoryStream<Path> originalStream = originalProvider.newDirectoryStream(originalDir, filter);
        return new DirectoryStream<Path>() {
            @Override
            public Iterator<Path> iterator() {
                final Iterator<Path> originalIt = originalStream.iterator();

                return new Iterator<Path>() {
                    @Override
                    public boolean hasNext() {
                        return originalIt.hasNext();
                    }

                    @Override
                    public Path next() {
                        return toLayered(originalDir, originalIt.next());
                    }

                    @Override
                    public void remove() {
                        originalIt.remove();
                    }
                };
            }

            @Override
            public void close() throws IOException {
                originalStream.close();
            }
        };
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>[] attrs) throws IOException {
        originalProvider.createDirectory(toOriginal(dir), attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        originalProvider.delete(toOriginal(path));
    }

    @Override
    public void copy(Path source, Path target, CopyOption[] options) throws IOException {
        originalProvider.copy(toOriginal(source), toOriginal(target), options);
    }

    @Override
    public void move(Path source, Path target, CopyOption[] options) throws IOException {
        originalProvider.move(toOriginal(source), toOriginal(target), options);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return originalProvider.isSameFile(toOriginal(path), toOriginal(path2));
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return originalProvider.isHidden(toOriginal(path));
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return originalProvider.getFileStore(toOriginal(path));
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption[] options) {
        return originalProvider.getFileAttributeView(toOriginal(path), type, options);
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return originalProvider.readAttributes(toOriginal(path), type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return originalProvider.readAttributes(toOriginal(path), attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption[] options) throws IOException {
        originalProvider.setAttribute(toOriginal(path), attribute, value, options);
    }

    private Path toOriginal(Path path) {
        LayeredAbstractPath relative = (LayeredAbstractPath) absolutePath.relativize(path);
        Path current = originalPath;

        for (String name : relative.nameElements)
            current = current.resolve(name);
        return current;
    }

    private Path toLayered(Path base, Path path) {
        Path relative = base.relativize(path);
        List<String> nameElements = new ArrayList<>();

        for (int i = 0; i < relative.getNameCount(); i++) {
            nameElements.add(relative.getName(i).toString());
        }

        return new LayeredRelativePath(absolutePath.fileSystem, nameElements);
    }
}
