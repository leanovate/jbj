/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api.http;

import javax.annotation.Nonnull;
import java.io.InputStream;

/**
 * Generic request body.
 */
public interface RequestBody {
    @Nonnull
    String getContentType();

    @Nonnull
    InputStream getContent();
}
