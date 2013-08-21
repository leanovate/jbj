package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, MemberModifier}
import de.leanovate.jbj.runtime.{PVisibility, Context}
import de.leanovate.jbj.runtime.value.{NullVal, ObjectVal, PVar}
import de.leanovate.jbj.runtime.context.StaticContext
import de.leanovate.jbj.runtime

case class ClassVarDecl(modifieres: Set[MemberModifier.Type],
                            assignments: List[StaticAssignment]) extends ClassMemberDecl with StaticInitializer {
  lazy val isStatic = modifieres.contains(MemberModifier.STATIC)

  override def initializeInstance(instance: ObjectVal)(implicit ctx:Context) {
    assignments.foreach {
      assignment =>
        instance.setProperty(assignment.variableName, Some(getVisibility), assignment.initial.map(_.evalOld).getOrElse(NullVal))
    }
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    if (isStatic) {
      assignments.foreach {
        assignment =>
          staticCtx.defineVariable(assignment.variableName, PVar(assignment.initial.map(_.evalOld)))
      }
    }
  }

  private val getVisibility : PVisibility.Type = {
    if ( modifieres.contains(MemberModifier.PROTECTED))
      PVisibility.PROTECTED
    else if ( modifieres.contains(MemberModifier.PRIVATE))
      PVisibility.PRIVATE
    else
      PVisibility.PUBLIC
  }
}
