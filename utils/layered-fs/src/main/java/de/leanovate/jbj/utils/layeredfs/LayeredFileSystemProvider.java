package de.leanovate.jbj.utils.layeredfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class LayeredFileSystemProvider extends FileSystemProvider {
    private final Map<URI, LayeredFileSystem> fileSystems = new ConcurrentHashMap<>();

    @Override
    public String getScheme() {
        return "layered";
    }

    @Override
    @SuppressWarnings("unchecked")
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        if (fileSystems.containsKey(uri))
            throw new FileSystemAlreadyExistsException();

        LayeredFileSystem fileSystem = new LayeredFileSystem(this, uri, (Map<String, Path>) env.get("mountPoints"));
        fileSystems.put(uri, fileSystem);
        return fileSystem;
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        if (!fileSystems.containsKey(uri))
            throw new FileSystemNotFoundException();
        return fileSystems.get(uri);
    }

    @Override
    public Path getPath(URI uri) {

        try {
            URI fileSystemUri = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), uri.getPath(), null, null);
            FileSystem fileSystem = getFileSystem(fileSystemUri);
            return fileSystem.getPath(uri.getFragment());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return findElement(path).newByteChannel(path, options, attrs);
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return findElement(dir).newDirectoryStream(dir, filter);
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        findElement(dir).createDirectory(dir, attrs);
    }

    @Override
    public void delete(Path path) throws IOException {
        findElement(path).delete(path);
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        LayeredElement sourceElement = findElement(source);
        LayeredElement targetElement = findElement(target);

        if (!sourceElement.equals(targetElement))
            throw new IllegalArgumentException("Cannot copy from " + source + " to " + target);

        sourceElement.copy(source, target, options);
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        LayeredElement sourceElement = findElement(source);
        LayeredElement targetElement = findElement(target);

        if (!sourceElement.equals(targetElement))
            throw new IllegalArgumentException("Cannot move from " + source + " to " + target);

        sourceElement.move(source, target, options);
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        LayeredElement sourceElement = findElement(path);
        LayeredElement targetElement = findElement(path2);

        if (!sourceElement.equals(targetElement))
            return false;

        return sourceElement.isSameFile(path, path2);
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return findElement(path).isHidden(path);
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return findElement(path).getFileStore(path);
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        findElement(path).checkAccess(path, modes);
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        try {
            return findElement(path).getFileAttributeView(path, type, options);
        } catch (NoSuchFileException e) {
            return null;
        }
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return findElement(path).readAttributes(path, type, options);
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return findElement(path).readAttributes(path, attributes, options);
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        findElement(path).setAttribute(path, attribute, value, options);
    }

    private LayeredElement findElement(Path path) throws NoSuchFileException {
        Path absolute = path.toAbsolutePath();
        FileSystem fileSystem = path.getFileSystem();

        if (!(fileSystem instanceof LayeredFileSystem))
            throw new IllegalArgumentException(path + " has wrong filesystem");

        for (LayeredElement element : ((LayeredFileSystem) fileSystem).elements) {
            if (absolute.startsWith(element.getAbsolutePath()))
                return element;
        }
        throw new NoSuchFileException(path.toString());
    }
}
