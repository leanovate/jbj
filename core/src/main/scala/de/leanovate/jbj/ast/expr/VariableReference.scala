package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.Context
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.{ValueOrRef, ValueRef, NullVal}

case class VariableReference(variableName: Name) extends Reference {
  override def isDefined(implicit ctx: Context) = ctx.findVariable(variableName.evalName).isDefined

  override def eval(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).map(_.value).getOrElse {
      ctx.log.notice(position, "Undefined variable: %s".format(name))
      NullVal
    }
  }

  override def evalRef(implicit ctx: Context) = {
    val name = variableName.evalName
    ctx.findVariable(name).getOrElse {
      val result = ValueRef()
      ctx.defineVariable(name, result)
      result
    }
  }

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
    val name = variableName.evalName
    ctx.findVariable(name) match {
      case Some(valueRef) => valueRef.value = valueOrRef.value
      case None => ctx.defineVariable(name, ValueRef(valueOrRef.value))
      case _ =>
    }
  }

  override def unsetRef(implicit ctx: Context) {
    ctx.undefineVariable(variableName.evalName)
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    variableName.dump(out, ident + "  ")
  }
}
