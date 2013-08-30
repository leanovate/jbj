/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.decl

import de.leanovate.jbj.core.ast.{StaticInitializer, MemberModifier}
import de.leanovate.jbj.core.runtime.PClass
import de.leanovate.jbj.core.runtime.value.{PVar, NullVal, ObjectVal}
import de.leanovate.jbj.core.runtime.context.{Context, StaticContext}
import de.leanovate.jbj.core.ast.stmt.StaticAssignment
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

case class ClassVarDecl(modifieres: Set[MemberModifier.Type],
                        assignments: List[StaticAssignment]) extends ClassMemberDecl with StaticInitializer {
  lazy val isStatic = modifieres.contains(MemberModifier.STATIC)

  override def initializeInstance(instance: ObjectVal, pClass: PClass)(implicit ctx: Context) {
    if (!isStatic) {
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
  }

  override def initializeInterface(pInterface: InterfaceDeclStmt)(implicit ctx: Context) {
    throw new FatalErrorJbjException("Interfaces may not include member variables")
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
