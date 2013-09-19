/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.buildins

import de.leanovate.jbj.runtime.JbjExtension
import de.leanovate.jbj.runtime.types.PFunction
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

object BuildinsExtension extends JbjExtension {
  val name = "Buildins"

  override def functions: Seq[PFunction] = buildinFunctions
}
