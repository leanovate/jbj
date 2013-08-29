/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * HTTP request information.
 * <p/>
 * Implement this interface to adapt the HTTP requests of your web container of choice.
 */
public interface RequestInfo {
    enum Method {
        GET, POST
    }

    @Nonnull
    Method getMethod();

    @Nonnull
    String getUri();

    @Nonnull
    Map<String, List<String>> getQuery();

    @Nonnull
    String getRawQuery();

    @Nullable
    RequestBody getBody();
}
