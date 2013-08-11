package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Reference, Stmt}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}

case class UnsetStmt(references: List[Reference]) extends Stmt {
  def exec(implicit ctx: Context) = {
    references.foreach(_.unsetRef)
    SuccessExecResult
  }
}
