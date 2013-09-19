/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api.http;

/**
 * Default implementation of the {@link JbjProcessExecutor} interface.
 * <p/>
 * By default jbj does not execute any kind of external processes. You might want to change this for maximal
 * compatibility.
 */
public class DefaultJbjProcessExecutor implements JbjProcessExecutor {
    @Override
    public String execShell(String shellCommand) {
        throw new JbjException("Execution of external processes is forbidden");
    }
}
