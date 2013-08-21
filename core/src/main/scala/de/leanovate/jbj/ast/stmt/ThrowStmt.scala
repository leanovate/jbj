package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Expr, Stmt}
import de.leanovate.jbj.runtime.{buildin, Context}
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.exception.{FatalErrorJbjException, RuntimeJbjException}

case class ThrowStmt(expr: Expr) extends Stmt {
  override def exec(implicit ctx: Context) = expr.evalOld match {
    case obj: ObjectVal if obj.instanceOf(buildin.Exception) =>
      throw new RuntimeJbjException(obj)
    case obj: ObjectVal =>
      throw new FatalErrorJbjException("Exceptions must be valid objects derived from the Exception base class")
    case _ =>
      throw new FatalErrorJbjException("Can only throw objects")
  }
}
