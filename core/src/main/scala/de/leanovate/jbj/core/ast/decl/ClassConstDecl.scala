/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.runtime.context.{ClassContext, Context}
import de.leanovate.jbj.runtime.value.{ObjectVal, PVal, ConstVal, NullVal}
import de.leanovate.jbj.core.ast.stmt.StaticAssignment
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.ast.expr.ArrayCreateExpr
import de.leanovate.jbj.runtime.PClass


case class ClassConstDecl(assignments: List[StaticAssignment]) extends ClassMemberDecl {
  override def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {
    assignments.foreach {
      assignment =>
        val position = ctx.currentPosition
        if (pClass._classConstants.contains(assignment.variableName))
          throw new FatalErrorJbjException("Cannot redefine class constant %s::%s".format(pClass.name.toString, assignment.variableName))
        if (assignment.initial.exists(_.isInstanceOf[ArrayCreateExpr]))
          throw new FatalErrorJbjException("Arrays are not allowed in class constants")
        pClass._classConstants(assignment.variableName) = new ConstVal {
          private var value: Option[PVal] = None

          override def asVal(implicit ctx: Context) = value.getOrElse {
            value = Some(if (!assignment.initial.isDefined)
              NullVal
            else {
              val classContext = ClassContext(pClass, ctx, position)
              assignment.initial.get.eval(classContext).asVal
            })
            value.get
          }
        }
    }
  }

  override def initializeInstance(instance: ObjectVal, pClass: ClassDeclStmt)(implicit ctx: Context) {
    assignments.foreach {
      assignment =>
        pClass._classConstants.get(assignment.variableName).foreach(_.asVal)
    }
  }
}
