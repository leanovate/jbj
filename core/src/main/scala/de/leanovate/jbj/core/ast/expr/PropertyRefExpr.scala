/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, RefExpr}
import de.leanovate.jbj.runtime.context.Context

case class PropertyRefExpr(reference: RefExpr, propertyName: Name) extends RefExpr {
  override def eval(implicit ctx: Context) = evalRef.asVal

  override def evalRef(implicit ctx: Context) = reference.evalRef.prop(propertyName.evalName)

  override def phpStr = reference + "->$" + propertyName.phpStr
}
