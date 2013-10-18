/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Expr, Name}
import de.leanovate.jbj.runtime.value.PAny
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.{MethodContext, Context}

case class CallStaticMethodRefExpr(className: Name, methodName: Name, parameters: List[Expr])
  extends CallRefExpr {

  override def call(implicit ctx: Context): PAny = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name, autoload = false).map {
      pClass =>
        ctx match {
          case MethodContext(instance, currentMethod, _) if pClass.isAssignableFrom(instance.pClass) =>
            pClass.invokeMethod(Some(instance), methodName.evalName, parameters.map(ExprParam.apply))
          case _ =>
            pClass.invokeMethod(None, methodName.evalName, parameters.map(ExprParam.apply))
        }
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def phpStr = className.phpStr + "::" + methodName.phpStr + parameters.map(_.phpStr).mkString("(", ", ", ")")
}
