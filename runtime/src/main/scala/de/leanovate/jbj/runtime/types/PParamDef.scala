/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.types


trait PParamDef {
  def name: String

  def default: Option[PParamDefault]

  def byRef: Boolean

  def typeHint: Option[TypeHint]

  def isCompatible(other: PParamDef): Boolean = typeHint.map {
    thisTypeHint =>
      other.typeHint.exists {
        otherTypeHint =>
          thisTypeHint.isCompatible(otherTypeHint)
      }
  }.getOrElse(byRef == other.byRef && default.isDefined == other.default.isDefined)

  def display: String =
    typeHint.map(_.display + " ").getOrElse("") + (if (byRef) "&" else "") + "$" + name + default.map(" = " + _.display).getOrElse("")
}
