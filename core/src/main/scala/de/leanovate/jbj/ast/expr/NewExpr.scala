package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NamespaceName, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.NullVal
import java.io.PrintStream

case class NewExpr(className: NamespaceName, parameters: List[Expr]) extends Expr {
  override def eval(implicit ctx: Context) = ctx.findClass(className) match {
    case Some(pClass) =>
      pClass.newInstance(ctx, position, parameters.map(_.eval))
    case None =>
      ctx.log.fatal(position, "Class '%s' not found".format(className.toString))
      NullVal
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    out.println(ident + "  " + className.toString)
    parameters.foreach {
      parameter =>
        parameter.dump(out, ident + "  ")
    }
  }
}
