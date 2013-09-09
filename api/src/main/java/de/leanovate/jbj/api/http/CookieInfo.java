/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api.http;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Generic cookie information.
 * <p/>
 * Implement this interface to adapt the Cookie representation of your web container of choice.
 */
public interface CookieInfo {
    @Nonnull
    String getName();

    @Nonnull
    String getValue();

    @Nonnull
    Integer getMaxAge();

    @Nullable
    String getPath();

    @Nullable
    String getDomain();

    boolean isSecure();
}
