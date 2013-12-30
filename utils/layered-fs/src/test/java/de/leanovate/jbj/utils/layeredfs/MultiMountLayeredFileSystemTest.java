package de.leanovate.jbj.utils.layeredfs;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

public class MultiMountLayeredFileSystemTest {
    FileSystem fileSystem;
    FileSystem originalFileSystem1;
    FileSystem originalFileSystem2;

    @Before
    public void setup() throws IOException {
        originalFileSystem1 = MemoryFileSystemBuilder.newLinux().build(UUID.randomUUID().toString());
        originalFileSystem2 = MemoryFileSystemBuilder.newLinux().build(UUID.randomUUID().toString());
        Path root1 = originalFileSystem1.getPath("/");
        Path root2 = originalFileSystem2.getPath("/");
        fileSystem = LayeredFileSystemBuilder.newEmpty().mount("/mount1", root1).mount("/mnt/mount2", root2).build();
    }

    @After
    public void teardown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void testRoot() throws IOException {
        assertThat(Files.exists(fileSystem.getPath("/"))).isTrue();
        assertThat(Files.isDirectory(fileSystem.getPath("/"))).isTrue();
        assertThat(Files.exists(fileSystem.getPath("/mount1"))).isTrue();
        assertThat(Files.isDirectory(fileSystem.getPath("/mount1"))).isTrue();
        assertThat(Files.exists(fileSystem.getPath("/mnt"))).isTrue();
        assertThat(Files.isDirectory(fileSystem.getPath("/mnt"))).isTrue();

        List<Path> files = new ArrayList<>();
        for (Path path : Files.newDirectoryStream(fileSystem.getPath("/"))) {
            files.add(path);
        }
        assertThat(files).hasSize(2).contains(fileSystem.getPath("mnt"),fileSystem.getPath("mount1"));
    }
}
