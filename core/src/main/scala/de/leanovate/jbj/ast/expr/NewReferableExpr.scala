package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Name, Expr}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.{PVar, PAny, NullVal}
import java.io.PrintStream
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class NewReferableExpr(className: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = ctx.global.findClass(className.evalNamespaceName) match {
    case Some(pClass) =>
      pClass.newInstance(parameters)
    case None =>
      ctx.log.fatal(position, "Class '%s' not found".format(className.toString))
      NullVal
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = eval

    def asVal = result

    def asVar = PVar(result)

    def assign(pAny: PAny) = {
      throw new FatalErrorJbjException("Can't use new result in write context")
    }

    def unset() {
      throw new FatalErrorJbjException("Can't use new result in write context")
    }
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
