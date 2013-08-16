package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, Stmt, MemberModifier}
import de.leanovate.jbj.runtime.{SuccessExecResult, Context}
import de.leanovate.jbj.runtime.value.VarRef
import de.leanovate.jbj.runtime.context.StaticContext

case class ClassVarDeclStmt(modifieres: Set[MemberModifier.Type],
                            assignments: List[StaticAssignment]) extends Stmt with StaticInitializer {
  lazy val isStatic = modifieres.contains(MemberModifier.STATIC)

  override def exec(implicit ctx: Context) = {
    if (!isStatic) {
      assignments.foreach {
        assignment =>
          ctx.defineVariable(assignment.variableName, VarRef(assignment.initial.map(_.eval)))
      }
    }
    SuccessExecResult
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    if (isStatic) {
      assignments.foreach {
        assignment =>
          staticCtx.defineVariable(assignment.variableName, VarRef(assignment.initial.map(_.eval)))
      }
    }
  }
}
