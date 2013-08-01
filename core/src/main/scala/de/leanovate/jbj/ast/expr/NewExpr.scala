package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NamespaceName, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.UndefinedVal

case class NewExpr(className: NamespaceName, parameters: List[Expr]) extends Expr {
  def eval(ctx: Context) = ctx.findClass(className) match {
    case Some(pClass) =>
      pClass.newInstance(ctx, position, parameters.map(_.eval(ctx)))
    case None =>
      ctx.log.fatal(position, "Class '%s' not found".format(className.toString))
      UndefinedVal
  }
}
