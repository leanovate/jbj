/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api.http;

/**
 * Base of all exceptions thrown by JBJ.
 */
public class JbjException extends RuntimeException {
    public JbjException(String message) {
        super(message);
    }

    public JbjException(String message, Throwable cause) {
        super(message, cause);
    }

    public JbjException(Throwable cause) {
        super(cause);
    }

    public String getPosition() {
        return "<no position>";
    }
}
