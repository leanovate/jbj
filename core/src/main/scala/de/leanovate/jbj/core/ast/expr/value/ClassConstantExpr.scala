package de.leanovate.jbj.core.ast.expr.value

import de.leanovate.jbj.core.ast.{Name, Expr}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

case class ClassConstantExpr(className: Name, constantName: String) extends Expr {
  override def eval(implicit ctx: Context) = {
    val cname = className.evalNamespaceName
    ctx.global.findClass(cname).map {
      pClass =>
        pClass.classConstants.get(constantName).map(_.asVal).getOrElse {
          throw new FatalErrorJbjException("Undefined class constant '%s'".format(constantName))
        }
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(cname.toString))
    }
  }
}
