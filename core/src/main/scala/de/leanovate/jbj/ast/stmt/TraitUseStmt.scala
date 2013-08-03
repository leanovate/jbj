package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{Stmt, NamespaceName}
import de.leanovate.jbj.runtime.Context

case class TraitUseStmt(traits:List[NamespaceName]) extends Stmt {
  override def exec(implicit ctx: Context) = ???
}
