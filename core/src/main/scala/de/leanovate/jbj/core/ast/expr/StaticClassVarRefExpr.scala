/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, RefExpr}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.Context

case class StaticClassVarRefExpr(className: Name, variableName: Name) extends RefExpr {
  override def eval(implicit ctx: Context) = evalRef.byVal

  override def evalRef(implicit ctx: Context) = {
    val cname = className.evalNamespaceName
    val pClass = ctx.global.findClass(cname, autoload = false).getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(cname.toString))
    }
    pClass.$(variableName.evalName)
  }
}
