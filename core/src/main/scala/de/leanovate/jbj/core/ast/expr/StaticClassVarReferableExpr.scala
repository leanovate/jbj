/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, ReferableExpr}
import de.leanovate.jbj.core.runtime.value.NullVal
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.Context

case class StaticClassVarReferableExpr(className: Name, variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name, autoload = false).map {
      pClass =>
        val staticClassContext = ctx.global.staticContext(pClass)
        staticClassContext.findVariable(variableName.evalName).map(_.value).getOrElse(NullVal)
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def evalRef(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    val pClass = ctx.global.findClass(name, autoload = false).getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
    val staticClassContext = ctx.global.staticContext(pClass)

    staticClassContext.getVariable(variableName.evalName)
  }
}
