/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.utils.ramfs;

import org.junit.Test;

import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.Collections;

import static org.fest.assertions.api.Assertions.assertThat;

public class RamFileSystemProviderTest {
    @Test
    public void testCreateDestroy() throws Exception{
        FileSystem fileSystem = FileSystems.newFileSystem(new URI("ram://base"), Collections.<String, Object>emptyMap());

        assertThat(fileSystem).isNotNull().isInstanceOf(RamFileSystem.class);

        FileSystem other = FileSystems.getFileSystem(new URI("ram://base"));

        assertThat(other).isSameAs(fileSystem);

        FileSystem unknown = FileSystems.getFileSystem(new URI("ram://something"));

        assertThat(unknown).isNull();

        fileSystem.close();

        FileSystem unknownAgain = FileSystems.getFileSystem(new URI("ram://base"));

        assertThat(unknownAgain).isNull();
    }
}
