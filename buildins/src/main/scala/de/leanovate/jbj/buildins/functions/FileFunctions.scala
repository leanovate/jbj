/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins.functions

import de.leanovate.jbj.runtime.annotations.GlobalFunction

object FileFunctions {
  @GlobalFunction
  def dirname(fileName: String): String = {
    val idx = fileName.lastIndexOf('/')
    if (idx >= 0)
      fileName.substring(0, idx)
    else
      ""
  }
}
