/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class ClassConstantExpr(className: Name, constantName: String) extends Expr {
  override def eval(implicit ctx: Context) = {
    val cname = className.evalNamespaceName
    ctx.global.findClass(cname, autoload = false).map {
      pClass =>
        pClass.classConstants.get(constantName).map(_.asVal).getOrElse {
          throw new FatalErrorJbjException("Undefined class constant '%s'".format(constantName))
        }
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(cname.toString))
    }
  }

  override def phpStr = className + "::" + constantName
}
