package de.leanovate.jbj.utils.layeredfs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.fest.assertions.api.Assertions.assertThat;

public class EmptyLayeredFileSystemTest {
    FileSystem fileSystem;

    @Before
    public void setup() throws IOException {
        fileSystem = LayeredFileSystemBuilder.newEmpty().build();
    }

    @After
    public void teardown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void testRootExists() {
        assertThat(Files.exists(fileSystem.getPath(""))).isTrue();
        assertThat(Files.exists(fileSystem.getPath("/"))).isTrue();
        assertThat(Files.isDirectory(fileSystem.getPath("/"))).isTrue();
        assertThat(Files.isRegularFile(fileSystem.getPath("/"))).isFalse();
        assertThat(Files.isReadable(fileSystem.getPath("/"))).isTrue();
    }

    @Test
    public void testRootList() throws IOException {
        DirectoryStream<Path> unfiltered = Files.newDirectoryStream(fileSystem.getPath("/"));

        assertThat(unfiltered).isNotNull();
        assertThat(unfiltered.iterator().hasNext()).isFalse();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateDirectory() throws IOException {
        Files.createDirectories(fileSystem.getPath("/path"));
    }

    @Test
    public void testOtherExists() {
        assertThat(Files.exists(fileSystem.getPath("/path"))).isFalse();
        assertThat(Files.isDirectory(fileSystem.getPath("/path"))).isFalse();
    }
}
