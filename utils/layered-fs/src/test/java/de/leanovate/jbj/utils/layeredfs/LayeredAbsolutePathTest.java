package de.leanovate.jbj.utils.layeredfs;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.nio.file.Path;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class LayeredAbsolutePathTest {
    @Mock
    LayeredFileSystem layeredFileSystem;

    @Test
    public void testStartsWith() {
        LayeredAbsolutePath path1 = new LayeredAbsolutePath(layeredFileSystem, "path1", "path2", "path3");
        LayeredAbsolutePath path2 = new LayeredAbsolutePath(layeredFileSystem, "path1", "path2", "path3", "path4", "path5");

        assertThat(path2.startsWith(path1)).isTrue();
        assertThat(path1.startsWith(path2)).isFalse();
    }

    @Test
    public void testRelativize() {
        LayeredAbsolutePath path1 = new LayeredAbsolutePath(layeredFileSystem, "path1", "path2", "path3");
        LayeredAbsolutePath path2 = new LayeredAbsolutePath(layeredFileSystem, "path1", "path2", "path3", "path4", "path5");

        Path path12 = path1.relativize(path2);
        assertThat(path12).isNotNull().isInstanceOf(LayeredRelativePath.class);
        assertThat(path12.toString()).isEqualTo("path4/path5");

        Path path21 = path2.relativize(path1);
        assertThat(path21).isNotNull().isInstanceOf(LayeredRelativePath.class);
        assertThat(path21.toString()).isEqualTo("../..");
    }
}
