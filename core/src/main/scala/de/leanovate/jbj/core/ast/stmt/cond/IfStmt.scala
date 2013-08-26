package de.leanovate.jbj.core.ast.stmt.cond

import de.leanovate.jbj.core.ast._
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.ast.stmt.BlockLike

case class IfStmt(condition: Expr, thenStmts: List[Stmt], elseIfs: List[ElseIfBlock], elseStmts: List[Stmt])
  extends Stmt  with BlockLike {

  override def exec(implicit ctx: Context) = {
    if (condition.eval.asVal.toBool.asBoolean) {
      execStmts(thenStmts)
    } else {
      elseIfs.find(_.condition.eval.asVal.toBool.asBoolean).map {
        elseIf => execStmts(elseIf.themStmts)
      }.getOrElse {
        execStmts(elseStmts)
      }
    }
  }

  override def toXml =
    <IfStmt line={position.line.toString} file={position.fileName}>
      <condition>
        {condition.toXml}
      </condition>
      <then>
        {thenStmts.map(_.toXml)}
      </then>
      {elseIfs.map(_.toXml)}
      <else>
        {elseStmts.map(_.toXml)}
      </else>
    </IfStmt>

  override def visit[R](visitor: NodeVisitor[R]) =
    visitor(this).thenChild(condition).thenChildren(thenStmts).thenChildren(elseIfs).thenChildren(elseStmts)
}
