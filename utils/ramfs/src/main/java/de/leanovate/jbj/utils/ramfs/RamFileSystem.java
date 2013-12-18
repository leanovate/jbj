/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.utils.ramfs;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.Set;

public class RamFileSystem extends FileSystem {
    private final RamFileSystemProvider provider;
    private final String name;
    private boolean open = true;

    RamFileSystem(RamFileSystemProvider provider, String name) {
        this.provider = provider;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public void close() throws IOException {
        open = false;
        provider.removeFileSystem(this);
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return Collections.singleton("basic");
    }

    @Override
    public Path getPath(String first, String... more) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public WatchService newWatchService() throws IOException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
