package de.leanovate.jbj.utils.layeredfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LayeredFileSystemBuilder {
    private final URI uri;
    private final Map<String, Path> mountPoints = new HashMap<>();

    public LayeredFileSystemBuilder(URI uri) {
        this.uri = uri;
    }

    public LayeredFileSystemBuilder mount(String mountPoint, Path path) {
        if (!mountPoint.startsWith("/"))
            throw new IllegalArgumentException("Mount point must be absolute path");
        mountPoints.put(mountPoint, path);
        return this;
    }

    public FileSystem build() throws IOException {
        Map<String, Object> env = new HashMap<>();
        env.put("mountPoints", mountPoints);

        return FileSystems.newFileSystem(uri, env);
    }

    public static LayeredFileSystemBuilder newEmpty() {
        try {
            return newEmpty(new URI("layered", UUID.randomUUID().toString(), null, null));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static LayeredFileSystemBuilder newEmpty(URI uri) {
        return new LayeredFileSystemBuilder(uri);
    }
}
