package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference, Expr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef}
import java.io.PrintStream

case class CallFunctionReference(functionName: Name, parameters: List[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = callFunction.value

  override def evalRef(implicit ctx: Context) = callFunction

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
    callFunction match {
      case valueRef: ValueRef => valueRef.value = valueOrRef.value
      case _ => throw new RuntimeException("Function does not have reference result")
    }
  }

  override def unsetRef(implicit ctx:Context) {
    throw new FatalErrorJbjException("Can't use function return value in write context")
  }

  private def callFunction(implicit ctx: Context): ValueOrRef = {
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
