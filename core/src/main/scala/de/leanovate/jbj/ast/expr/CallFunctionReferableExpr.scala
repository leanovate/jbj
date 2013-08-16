package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.{PAny, PVar}
import java.io.PrintStream

case class CallFunctionReferableExpr(functionName: Name, parameters: List[Expr]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = callFunction.value

  override def evalVar(implicit ctx: Context) = callFunction

  override def assignVar(valueOrRef: PAny)(implicit ctx: Context) {
    callFunction match {
      case valueRef: PVar => valueRef.value = valueOrRef.value
      case _ => throw new RuntimeException("Function does not have reference result")
    }
  }

  override def unsetVar(implicit ctx: Context) {
    throw new FatalErrorJbjException("Can't use function return value in write context")
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
