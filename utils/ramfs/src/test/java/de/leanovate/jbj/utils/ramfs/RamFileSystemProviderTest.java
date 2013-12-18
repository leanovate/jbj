/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.utils.ramfs;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;

public class RamFileSystemProviderTest {
    private FileSystem fileSystem;

    @Before
    public void setup() throws Exception {
        fileSystem = FileSystems.newFileSystem(new URI("ram://base"), Collections.<String, Object>emptyMap());
    }

    @After
    public void teardown() throws Exception {
        fileSystem.close();
    }

    @Test
    public void testCreateDestroy() throws Exception{

        assertThat(fileSystem).isNotNull().isInstanceOf(RamFileSystem.class);

        FileSystem other = FileSystems.getFileSystem(new URI("ram://base"));

        assertThat(other).isSameAs(fileSystem);
    }

    @Test
    public void testGetPath() throws Exception {
        Path path = fileSystem.getPath("/path1");

        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("/path1");

        Path root = path.getRoot();
        Path parent = path.getParent();

        assertThat(root).isNotNull().isEqualTo(parent);
        assertThat(root.toString()).isEqualTo("/");
    }
}
