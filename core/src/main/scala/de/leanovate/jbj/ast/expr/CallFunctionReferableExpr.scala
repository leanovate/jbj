package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr, Expr}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.{PAny, PVar}
import java.io.PrintStream

case class CallFunctionReferableExpr(functionName: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = callFunction.asVal

  override def evalRef(implicit ctx: Context) = new Reference {
    val result = callFunction

    def asVal = result.asVal

    def asVar = result

    def assign(pAny: PAny) = pAny

    def unset() {
      throw new FatalErrorJbjException("Can't use function return value in write context")
    }
  }

  private def callFunction(implicit ctx: Context): PAny = {
    val name = functionName.evalNamespaceName
    ctx.findFunction(name).map {
      func => func.call(parameters)
    }.getOrElse {
      throw new FatalErrorJbjException("Call to undefined function %s()".format(name.toString))
    }
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    functionName.dump(out, ident + "  ")
    parameters.foreach(_.dump(out, ident + "  "))
  }
}
