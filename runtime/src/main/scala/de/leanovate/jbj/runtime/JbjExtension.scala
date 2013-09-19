/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.types.{PInterface, PClass, PFunction}

trait JbjExtension {
  def name: String

  def constants: Seq[(String, PVal)] = Seq.empty

  def functions: Seq[PFunction] = Seq.empty

  def classes: Seq[PClass] = Seq.empty

  def interfaces: Seq[PInterface] = Seq.empty
}
