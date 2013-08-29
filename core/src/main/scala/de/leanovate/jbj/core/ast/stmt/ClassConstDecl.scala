package de.leanovate.jbj.core.ast.stmt

import de.leanovate.jbj.core.runtime.context.{ClassContext, Context}
import de.leanovate.jbj.core.runtime.value.{PVal, ConstVal, NullVal}


case class ClassConstDecl(assignments: List[StaticAssignment]) extends ClassMemberDecl {
  override def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {
    assignments.foreach {
      assignment =>
        val position = ctx.currentPosition
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
}
