package de.leanovate.jbj.utils.layeredfs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;
import java.util.regex.Pattern;

public class LayeredFileSystem extends FileSystem {
    private final Set<FileSystem> mountedFileSystems = new HashSet<>();
    private final LayeredFileSystemProvider provider;
    private final URI uri;
    private boolean opened = true;

    List<LayeredElement> elements;

    public LayeredFileSystem(LayeredFileSystemProvider provider, URI uri, Map<String, Path> mountPoints) {
        this.provider = provider;
        this.uri = uri;

        elements = new ArrayList<>();
        if (mountPoints != null && !mountPoints.isEmpty()) {
            for (Map.Entry<String, Path> mountPoint : mountPoints.entrySet()) {
                this.elements.add(new LayeredMountPoint((LayeredAbsolutePath) getPath(mountPoint.getKey()), mountPoint.getValue()));
                this.mountedFileSystems.add(mountPoint.getValue().getFileSystem());
            }
            for (LayeredElement element : new ArrayList<>(elements)) {
                addDirectories(element);
            }
        } else {
            elements.add(new LayeredDirectory(new LayeredAbsolutePath(this)));
        }
        Collections.sort(elements, new Comparator<LayeredElement>() {
            @Override
            public int compare(LayeredElement o1, LayeredElement o2) {
                return o2.getAbsolutePath().compareTo(o1.getAbsolutePath());
            }
        });
    }

    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    @Override
    public void close() throws IOException {
        for (FileSystem fileSystem : mountedFileSystems) {
            try {
                fileSystem.close();
            } catch (Exception e) {
                // ignore
            }
        }
        opened = false;
    }

    @Override
    public boolean isOpen() {
        return opened;
    }

    @Override
    public boolean isReadOnly() {
        for (FileSystem fileSystem : mountedFileSystems)
            if (!fileSystem.isReadOnly())
                return false;

        return true;
    }

    @Override
    public String getSeparator() {
        return "/";
    }

    @Override
    public Iterable<Path> getRootDirectories() {
        return Arrays.asList((Path) new LayeredAbsolutePath(this));
    }

    @Override
    public Iterable<FileStore> getFileStores() {
        List<FileStore> fileStores = new ArrayList<>();

        for (FileSystem fileSystem : mountedFileSystems)
            for (FileStore fileStore : fileSystem.getFileStores())
                fileStores.add(fileStore);
        return fileStores;
    }

    @Override
    public Set<String> supportedFileAttributeViews() {
        Set<String> fileAttributeViews = new HashSet<>();

        for (FileSystem fileSystem : mountedFileSystems) {
            fileAttributeViews.addAll(fileSystem.supportedFileAttributeViews());
        }
        return fileAttributeViews;
    }

    @Override
    public Path getPath(String first, String... more) {
        StringBuilder sb = new StringBuilder(first);
        for (String str : more)
            sb.append("/").append(str);
        List<String> nameElements = new ArrayList<>();
        for (String element : sb.toString().split("/")) {
            if (element.length() > 0)
                nameElements.add(element);
        }
        if (first.startsWith("/"))
            return new LayeredAbsolutePath(this, nameElements);
        else
            return new LayeredRelativePath(this, nameElements);
    }

    @Override
    public PathMatcher getPathMatcher(String syntaxAndPattern) {
        int colonIndex = syntaxAndPattern.indexOf(':');
        if (colonIndex <= 0 || colonIndex == syntaxAndPattern.length() - 1) {
            throw new IllegalArgumentException("syntaxAndPattern must have form \"syntax:pattern\" but was \"" + syntaxAndPattern + "\"");
        }

        String syntax = syntaxAndPattern.substring(0, colonIndex);
        String pattern = syntaxAndPattern.substring(colonIndex + 1);
        if (syntax.equalsIgnoreCase(GlobPathMatcher.NAME)) {
            Path patternPath = getPath(pattern);
            return new GlobPathMatcher(patternPath);
        }
        if (syntax.equalsIgnoreCase(RegexPathMatcher.NAME)) {
            Pattern regex = Pattern.compile(pattern);
            return new RegexPathMatcher(regex);
        }

        throw new UnsupportedOperationException("unsupported syntax \"" + syntax + "\"");
    }

    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        return null;
    }

    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException();
    }

    public URI getUri() {
        return uri;
    }

    private void addDirectories(LayeredElement element) {
        LayeredElement current = element;
        LayeredAbsolutePath parentPath = (LayeredAbsolutePath) current.getAbsolutePath().getParent();

        while (parentPath != null) {
            LayeredDirectory parent = findDirectory(parentPath);

            if (parent == null) {
                parent = new LayeredDirectory(parentPath);
                elements.add(parent);
            }

            parent.addElement(current);
            current = parent;
            parentPath = (LayeredAbsolutePath) current.getAbsolutePath().getParent();
        }
    }

    private LayeredDirectory findDirectory(Path path) {
        for (LayeredElement element : elements) {
            if (element.getAbsolutePath().equals(path)) {
                if (!(element instanceof LayeredDirectory)) {
                    throw new IllegalArgumentException(path + " is not a directory");
                }
                return (LayeredDirectory) element;
            }
        }
        return null;
    }
}
