package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{StaticInitializer, Stmt, Expr}
import de.leanovate.jbj.runtime.context.{Context, StaticContext}
import de.leanovate.jbj.ast.stmt.BlockLike

case class IfStmt(condition: Expr, thenStmts: List[Stmt], elseIfs: List[ElseIfBlock], elseStmts: List[Stmt])
  extends Stmt with StaticInitializer with BlockLike {

  private val staticInitializers =
    thenStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer]) ++
      elseIfs.map(_.themStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])).flatten ++
      elseStmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])


  override def exec(implicit ctx: Context) = {
    if (condition.evalOld.toBool.asBoolean) {
      execStmts(thenStmts)
    } else {
      elseIfs.find(_.condition.evalOld.toBool.asBoolean).map {
        elseIf => execStmts(elseIf.themStmts)
      }.getOrElse {
        execStmts(elseStmts)
      }
    }
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    staticInitializers.foreach(_.initializeStatic(staticCtx))
  }
}
