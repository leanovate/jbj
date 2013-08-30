/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.utils.ramfs;

import java.io.IOException;
import java.net.URI;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simulate a filesystem in memory.
 */
public class RamFileSystemProvider extends FileSystemProvider {
    private final Map<String, RamFileSystem> fileSystems = new ConcurrentHashMap<>();

    public RamFileSystemProvider() {
        super();
    }

    @Override
    public String getScheme() {
        return "ram";
    }

    @Override
    public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
        final String name = uri.getAuthority();

        if (fileSystems.containsKey(name))
            throw new FileSystemAlreadyExistsException("Ram filesystem already exists: " + uri);

        RamFileSystem fileSystem = new RamFileSystem(this, name);
        fileSystems.put(name, fileSystem);
        return fileSystem;
    }

    @Override
    public FileSystem getFileSystem(URI uri) {
        final String name = uri.getAuthority();

        RamFileSystem fileSystem = fileSystems.get(name);

        if (fileSystem == null)
            throw new FileSystemNotFoundException("Ram filesystem does not exists: " + uri);
        return fileSystem;
    }

    @Override
    public Path getPath(URI uri) {
        final String name = uri.getAuthority();

        RamFileSystem fileSystem = fileSystems.get(name);

        if (fileSystem == null)
            throw new FileSystemNotFoundException("Ram filesystem does not exists: " + uri);

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public DirectoryStream<Path> newDirectoryStream(Path dir, DirectoryStream.Filter<? super Path> filter) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void delete(Path path) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void move(Path source, Path target, CopyOption... options) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSameFile(Path path, Path path2) throws IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isHidden(Path path) throws IOException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FileStore getFileStore(Path path) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void checkAccess(Path path, AccessMode... modes) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    protected void removeFileSystem(RamFileSystem ramFileSystem) {
        fileSystems.remove(ramFileSystem.getName());
    }
}
