/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.FileNodePosition
import de.leanovate.jbj.api.http.JbjException

class ParseJbjException(var msg: String, var pos: FileNodePosition) extends JbjException(msg) {
  override def getPosition = pos.toString
}
