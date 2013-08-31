/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.{StaticInitializer, MemberModifier}
import de.leanovate.jbj.core.runtime.{PProperty, PClass}
import de.leanovate.jbj.core.runtime.value.{PVar, NullVal, ObjectVal}
import de.leanovate.jbj.core.runtime.context.{Context, StaticContext}
import de.leanovate.jbj.core.ast.stmt.StaticAssignment
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

case class ClassVarDecl(modifiers: Set[MemberModifier.Type], assignments: List[StaticAssignment])
  extends ClassMemberDecl with StaticInitializer {

  lazy val isStatic = modifiers.contains(MemberModifier.STATIC)

  lazy val isPrivate = modifiers.contains(MemberModifier.PRIVATE)

  lazy val isProtected = modifiers.contains(MemberModifier.PROTECTED)

  lazy val isPublic = modifiers.contains(MemberModifier.PUBLIC)

  override def initializeInstance(instance: ObjectVal, pClass: PClass)(implicit ctx: Context) {
    if (!isStatic) {
      if (modifiers.contains(MemberModifier.PROTECTED)) {
        assignments.foreach {
          case assignment if instance.getProperty(assignment.variableName, Some(pClass.name.toString)).isEmpty =>
            instance.defineProtectedProperty(assignment.variableName, assignment.initial.map(_.eval.asVal).getOrElse(NullVal))
          case _ =>
        }
      } else if (modifiers.contains(MemberModifier.PRIVATE)) {
        val className = pClass.name.toString

        assignments.foreach {
          assignment =>
            instance.definePrivateProperty(assignment.variableName, className, assignment.initial.map(_.eval.asVal).getOrElse(NullVal))
        }
      } else {
        assignments.foreach {
          case assignment if instance.getProperty(assignment.variableName, Some(pClass.name.toString)).isEmpty =>
            instance.definePublicProperty(assignment.variableName, assignment.initial.map(_.eval.asVal).getOrElse(NullVal))
          case _ =>
        }
      }
    }
  }

  override def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {
    pClass.superClass.foreach {
      superClass =>
        assignments.foreach {
          assignment =>
            superClass.properties.get(assignment.variableName).foreach {
              superProperty =>
                if (!isStatic && superProperty.isStatic && !superProperty.isPrivate)
                  throw new FatalErrorJbjException("Cannot redeclare static %s::$%s as non static %s::$%s".format(superClass.name.toString, superProperty.name, pClass.name.toString, assignment.variableName))
                if (isStatic && !superProperty.isStatic && !superProperty.isPrivate)
                  throw new FatalErrorJbjException("Cannot redeclare non static %s::$%s as static %s::$%s".format(superClass.name.toString, superProperty.name, pClass.name.toString, assignment.variableName))
                if (isPrivate) {
                  if (superProperty.isProtected)
                    throw new FatalErrorJbjException("Access level to %s::$%s must be protected (as in class %s) or weaker".format(pClass.name.toString, assignment.variableName, superClass.name.toString))
                  if (superProperty.isPublic)
                    throw new FatalErrorJbjException("Access level to %s::$%s must be public (as in class %s)".format(pClass.name.toString, assignment.variableName, superClass.name.toString))
                }
                if (isProtected) {
                  if (superProperty.isPublic)
                    throw new FatalErrorJbjException("Access level to %s::$%s must be public (as in class %s)".format(pClass.name.toString, assignment.variableName, superClass.name.toString))
                }
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

  protected[decl] def getProperties(pClass: PClass) = assignments.map {
    assignment => PProperty(modifiers, assignment.variableName, pClass)
  }
}
