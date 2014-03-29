/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.pcre

import de.leanovate.jbj.runtime.types.PFunction

package object functions {
  val pcreFunctions: Seq[PFunction] =
    PcreFunctions.functions

}
