/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.InterfaceContext
import de.leanovate.jbj.core.ast.expr.ArrayCreateExpr
import de.leanovate.jbj.runtime.context.ClassContext
import de.leanovate.jbj.core.ast.stmt.StaticAssignment
import scala.Some


case class ClassConstDecl(assignments: List[StaticAssignment]) extends ClassMemberDecl {
  override def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {
    assignments.foreach {
      assignment =>
        val position = ctx.currentPosition
        if (pClass._classConstants.contains(assignment.variableName))
          throw new FatalErrorJbjException("Cannot redefine class constant %s::%s".format(pClass.name.toString, assignment.variableName))
        if (assignment.initial.exists(_.isInstanceOf[ArrayCreateExpr]))
          throw new FatalErrorJbjException("Arrays are not allowed in class constants")
        pClass.interfaces.foreach {
          interface =>
            if (interface.interfaceConstants.get(assignment.variableName).isDefined)
              throw new FatalErrorJbjException("Cannot inherit previously-inherited or override constant %s from interface %s".
                format(assignment.variableName, interface.name.toString))
        }
        pClass._classConstants(assignment.variableName) = new LazyVal {
          private var _value: Option[PConcreteVal] = None

          override def value = _value.getOrElse {
            _value = Some(if (!assignment.initial.isDefined)
              NullVal
            else {
              val classContext = ClassContext(pClass, ctx, position)
              assignment.initial.get.eval(classContext).asVal.concrete
            })
            _value.get
          }
        }
    }
  }

  override def initializeInterface(pInterface: InterfaceDeclStmt)(implicit ctx: Context) {
    assignments.foreach {
      assignment =>
        val position = ctx.currentPosition
        if (pInterface._interfaceConstants.contains(assignment.variableName))
          throw new FatalErrorJbjException("Cannot redefine class constant %s::%s".format(pInterface.name.toString, assignment.variableName))
        if (assignment.initial.exists(_.isInstanceOf[ArrayCreateExpr]))
          throw new FatalErrorJbjException("Arrays are not allowed in class constants")
        pInterface.interfaces.foreach {
          interface =>
            if (interface.interfaceConstants.get(assignment.variableName).isDefined)
              throw new FatalErrorJbjException("Cannot inherit previously-inherited or override constant %s from interface %s".
                format(assignment.variableName, interface.name.toString))
        }
        pInterface._interfaceConstants(assignment.variableName) = new LazyVal {
          private var _value: Option[PConcreteVal] = None

          override def value = _value.getOrElse {
            _value = Some(if (!assignment.initial.isDefined)
              NullVal
            else {
              val classContext = InterfaceContext(pInterface, ctx, position)
              assignment.initial.get.eval(classContext).asVal.concrete
            })
            _value.get
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
