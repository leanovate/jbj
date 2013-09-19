/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api.http;

import de.leanovate.jbj.api.http.RequestBody;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Form request body.
 * <p/>
 * Use this in for content type "application/form-url-encoded".
 */
public interface FormRequestBody extends RequestBody {
    @Nonnull
    Map<String, List<String>> getFormData();
}
