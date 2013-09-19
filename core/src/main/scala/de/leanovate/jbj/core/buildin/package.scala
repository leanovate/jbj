/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.adapter.GlobalFunctions
import de.leanovate.jbj.runtime.types._
import de.leanovate.jbj.buildins.FunctionFunctions

package object buildin {
  val buildinFunctions: Seq[PFunction] = Seq.empty

  val buildinConstants: Seq[(String, PVal)] = Seq.empty

  val buildinInterfaces: Seq[PInterface] = Seq(PArrayAccess)

  val buildinClasses: Seq[PClass] = Seq(PStdClass, PException, PArrayObject)
}
