/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.api;

/**
 * Execute external processes.
 *
 * All external processes shall be spawned through this interface.
 */
public interface JbjProcessExecutor {
    /**
     * Execute a shell command.
     */
    public String execShell(String shellCommand);
}
