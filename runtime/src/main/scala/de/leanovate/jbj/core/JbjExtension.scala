/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import de.leanovate.jbj.core.runtime.{PClass, PFunction}
import de.leanovate.jbj.core.runtime.value.PVal

trait JbjExtension {
  def name: String

  def constants: Seq[(String, PVal)] = Seq.empty

  def function: Seq[PFunction] = Seq.empty

  def classes: Seq[PClass] = Seq.empty
}
