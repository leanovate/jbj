package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.ast.{StaticInitializer, MemberModifier}
import de.leanovate.jbj.core.runtime.PClass
import de.leanovate.jbj.core.runtime.value.{NullVal, ObjectVal, PVar}
import de.leanovate.jbj.core.runtime.context.{Context, StaticContext}

case class ClassVarDecl(modifieres: Set[MemberModifier.Type],
                        assignments: List[StaticAssignment]) extends ClassMemberDecl with StaticInitializer {
  lazy val isStatic = modifieres.contains(MemberModifier.STATIC)

  override def initializeInstance(instance: ObjectVal, pClass: PClass)(implicit ctx: Context) {
    if (modifieres.contains(MemberModifier.PROTECTED)) {
      assignments.foreach {
        assignment =>
          instance.defineProtectedProperty(assignment.variableName, assignment.initial.map(_.eval.asVal).getOrElse(NullVal))
      }
    } else if (modifieres.contains(MemberModifier.PRIVATE)) {
      val className = pClass.name.toString

      assignments.foreach {
        assignment =>
          instance.definePrivateProperty(assignment.variableName, className, assignment.initial.map(_.eval.asVal).getOrElse(NullVal))
      }
    } else {
      assignments.foreach {
        assignment =>
          instance.definePublicProperty(assignment.variableName, assignment.initial.map(_.eval.asVal).getOrElse(NullVal))
      }
    }
  }

  override def initializeStatic(staticCtx: StaticContext)(implicit ctx: Context) {
    if (isStatic) {
      assignments.foreach {
        assignment =>
          staticCtx.defineVariable(assignment.variableName, PVar(assignment.initial.map(_.eval.asVal)))
      }
    }
  }
}
