package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NoNodePosition, Name, ReferableExpr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.ast.expr.value.ScalarExpr
import de.leanovate.jbj.runtime.buildin.StdClass

case class PropertyReferableExpr(reference: ReferableExpr, propertyName: Name) extends ReferableExpr {
  override def isDefined(implicit ctx: Context) = {
    if (reference.isDefined) {
      reference.eval match {
        case obj: ObjectVal =>
          val name = propertyName.evalName
          if (obj.getProperty(name).isDefined) {
            true
          } else {
            obj.pClass.findMethod("__get").isDefined
          }
        case _ => false
      }
    } else {
      false
    }
  }

  override def eval(implicit ctx: Context) = {
    reference.eval match {
      case obj: ObjectVal =>
        val name = propertyName.evalName
        obj.getProperty(name).map(_.asVal).getOrElse {
          ctx match {
            case MethodContext(inst, methodName, _, _) if inst.pClass == obj.pClass && methodName == "__get" =>
              ctx.log.notice(position, "Undefined property: %s::%s".format(obj.pClass.name.toString, name))
              NullVal
            case _ =>
              obj.pClass.findMethod("__get").map(_.invoke(ctx, position, obj, ScalarExpr(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                ctx.log.notice(position, "Undefined property: %s::%s".format(obj.pClass.name.toString, name))
                NullVal
              }
          }
        }
      case _ =>
        ctx.log.notice(position, "Trying to get property of non-object")
        NullVal
    }
  }

  def evalVar(implicit ctx: Context) = {
    parentObject match {
      case Some(obj) =>
        val name = propertyName.evalName
        obj.getProperty(name).map {
          case valueRef: PVar =>
            valueRef
          case value: PVal =>
            val result = PVar(value)
            obj.setProperty(name, None, result)
            result
        }.getOrElse {
          val result = PVar()
          obj.setProperty(name, None, result)
          result
        }
      case None =>
        ctx.log.notice(position, "Trying to get property of non-object")
        PVar(NullVal)
    }
  }

  override def assignVar(valueOrRef: PAny)(implicit ctx: Context) {
    parentObject match {
      case Some(obj) =>
        val name = propertyName.evalName
        if (obj.getProperty(name).isDefined) {
          obj.setProperty(propertyName.evalName, None, valueOrRef.asVal)
        } else {
          ctx match {
            case MethodContext(inst, methodName, _, _) if inst.pClass == obj.pClass && methodName == "__set" =>
              obj.setProperty(name, None, valueOrRef.asVal)
            case _ =>
              obj.pClass.findMethod("__set").map(_.invoke(ctx, position, obj, ScalarExpr(StringVal(name)) :: ScalarExpr(valueOrRef.asVal) :: Nil)).getOrElse {
                obj.setProperty(name, None, valueOrRef.asVal)
              }
          }
        }
      case None =>
        ctx.log.warn(position, "Attempt to assign property of non-object")
    }
  }

  override def unsetVar(implicit ctx: Context) {
    reference.eval match {
      case obj: ObjectVal => obj.unsetProperty(propertyName.evalName)
      case _ =>
    }
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    reference.dump(out, ident + "  ")
    propertyName.dump(out, ident + "  ")
  }

  private def parentObject(implicit ctx: Context) = if (!reference.isDefined) {
    val obj = StdClass.newInstance(Nil)(ctx, NoNodePosition)
    reference.assignVar(obj)
    Some(obj)
  } else {
    reference.eval.asVal match {
      case obj: ObjectVal =>
        Some(obj)
      case NullVal =>
        val obj = StdClass.newInstance(Nil)(ctx, NoNodePosition)
        reference.assignVar(obj)
        Some(obj)
      case _ =>
        None
    }
  }
}
