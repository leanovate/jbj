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
import java.util.*;

public class RamFileSystem extends FileSystem {
    private final RamFileSystemProvider provider;
    private final String name;
    private final RamFileStore fileStore;
    private boolean open = true;

    RamFileSystem(RamFileSystemProvider provider, String name) {
        this.provider = provider;
        this.name = name;
        this.fileStore = new RamFileStore(this);
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
        return Arrays.asList((Path)new AbsoluteRamPath(this, Collections.<String>emptyList()));
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        return Arrays.asList((FileStore)fileStore);
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        return Collections.singleton("basic");
    }

    @Override
    public Path getPath(String first, String... more) {
        final List<String> names = new ArrayList<>();

        for (String name : first.split("/"))
            if (name.length() > 0)
                names.add(name);
        for (String element : more)
            for (String name : element.split("/"))
                if (name.length() > 0)
                    names.add(name);

        return new AbsoluteRamPath(this, names);
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
