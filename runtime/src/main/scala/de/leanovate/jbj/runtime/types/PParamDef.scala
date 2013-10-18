/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.PVal

trait PParamDef {
  def name: String

  def default: Option[Context => PVal]

  def byRef: Boolean

  def typeHint: Option[TypeHint]

  def isCompatible(other: PParamDef): Boolean = typeHint.map {
    thisTypeHint =>
      other.typeHint.exists {
        otherTypeHint =>
          thisTypeHint.isCompatible(otherTypeHint)
      }
  }.getOrElse(byRef == other.byRef)

  def display: String =
    typeHint.map(_.display + " $").getOrElse("$") + name
}
