/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api.http;

import javax.annotation.Nullable;

public interface JbjScriptLocator {
    @Nullable
    String getETag(String filename);

    @Nullable
    Script readScript(String filename);

    class Script {
        private final String filename;
        private final String etag;
        private final String content;

        public Script(String filename, String etag, String content) {
            this.filename = filename;
            this.etag = etag;
            this.content = content;
        }

        public String getFilename() {
            return filename;
        }

        public String getEtag() {
            return etag;
        }

        public String getContent() {
            return content;
        }
    }
}
