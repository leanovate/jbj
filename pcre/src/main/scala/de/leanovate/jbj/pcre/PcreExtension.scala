/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.pcre

import de.leanovate.jbj.runtime.JbjExtension
import de.leanovate.jbj.runtime.types.PFunction

object PcreExtension extends JbjExtension {
  val name = "pcre"

  override def functions: Seq[PFunction] = de.leanovate.jbj.pcre.functions.pcreFunctions
}
