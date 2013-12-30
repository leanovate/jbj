package de.leanovate.jbj.utils.layeredfs;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.fest.assertions.api.Assertions.assertThat;

public class ChrootLayeredFileSystemTest {
    FileSystem fileSystem;
    FileSystem originalFileSystem;

    @Before
    public void setup() throws IOException {
        originalFileSystem = MemoryFileSystemBuilder.newLinux().build(UUID.randomUUID().toString());
        Path chroot = originalFileSystem.getPath("/path1/path2/path3");
        Files.createDirectories(chroot);
        fileSystem = LayeredFileSystemBuilder.newEmpty().mount("/", chroot).build();
    }

    @After
    public void teardown() throws IOException {
        fileSystem.close();
    }

    @Test
    public void testRoot() throws IOException {
        assertThat(Files.exists(fileSystem.getPath("/"))).isTrue();
        assertThat(Files.isDirectory(fileSystem.getPath("/"))).isTrue();

        Files.createDirectory(fileSystem.getPath("/path4"));

        assertThat(Files.exists(fileSystem.getPath("/path4"))).isTrue();
        assertThat(Files.exists(originalFileSystem.getPath("/path1/path2/path3/path4")));

        List<Path> files = new ArrayList<>();
        for (Path path : Files.newDirectoryStream(fileSystem.getPath("/"))) {
            files.add(path);
        }
        assertThat(files).hasSize(1).contains(fileSystem.getPath("path4"));
    }

    @Test
    public void testCreateFile() throws IOException {
        Files.createDirectory(fileSystem.getPath("/path4"));
        SeekableByteChannel file1 = Files.newByteChannel(fileSystem.getPath("/path4/file1"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

        file1.write(ByteBuffer.wrap(new byte[]{1, 2, 3}));
        file1.close();

        SeekableByteChannel file2 = Files.newByteChannel(fileSystem.getPath("/file2"), StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

        file2.write(ByteBuffer.wrap(new byte[]{4, 5, 6}));
        file2.close();

        assertThat(Files.exists(originalFileSystem.getPath("/path1/path2/path3/path4/file1"))).isTrue();
        assertThat(Files.isReadable(originalFileSystem.getPath("/path1/path2/path3/path4/file1"))).isTrue();
        assertThat(Files.isRegularFile(originalFileSystem.getPath("/path1/path2/path3/path4/file1"))).isTrue();
        assertThat(Files.exists(originalFileSystem.getPath("/path1/path2/path3/file2"))).isTrue();
        assertThat(Files.isReadable(originalFileSystem.getPath("/path1/path2/path3/file2"))).isTrue();
        assertThat(Files.isRegularFile(originalFileSystem.getPath("/path1/path2/path3/file2"))).isTrue();
    }
}
