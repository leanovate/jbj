package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, NamespaceName, Reference, Expr}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef, Value}

case class CallFunctionReference(functionName: Name, parameters: List[Expr]) extends Reference {
  override def eval(implicit ctx: Context) = callFunction.value

  override def evalRef(implicit ctx: Context) = callFunction

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
    callFunction match {
      case valueRef: ValueRef => valueRef.value = valueOrRef.value
      case _ => throw new RuntimeException("Function does not have reference result")
    }
  }

  private def callFunction(implicit ctx: Context): ValueOrRef = {
    val name = functionName.evalNamespaceName
    ctx.findFunction(name).map {
      func => func.call(ctx, position, parameters.map(_.eval))
    }.getOrElse {
      throw new FatalErrorJbjException("Call to undefined function %s()".format(name.toString))
    }
  }
}
