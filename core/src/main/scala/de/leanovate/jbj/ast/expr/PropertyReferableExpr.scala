package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, ReferableExpr}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.value.StringVal
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.ast.expr.value.ScalarExpr

case class PropertyReferableExpr(reference: ReferableExpr, propertyName: Name) extends ReferableExpr {
  override def isDefined(implicit ctx: Context) = {
    if (reference.isDefined) {
      reference.eval match {
        case obj: ObjectVal =>
          val name = propertyName.evalName
          if (obj.getProperty(name).isDefined) {
            true
          } else {
            ctx match {
              case MethodContext(inst, methodName, _, _) if inst.pClass == obj.pClass && methodName == "__set" =>
                false
              case _ =>
                obj.pClass.findMethod("__get").map(_.invoke(ctx, position, obj, ScalarExpr(StringVal(name)) :: Nil)).exists(_.value.isNull)
            }
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
        obj.getProperty(name).map(_.value).getOrElse {
          ctx match {
            case MethodContext(inst, methodName, _, _) if inst.pClass == obj.pClass && methodName == "__get" =>
              ctx.log.notice(position, "Undefined property: %s::%s".format(obj.pClass.name.toString, name))
              NullVal
            case _ =>
              obj.pClass.findMethod("__get").map(_.invoke(ctx, position, obj, ScalarExpr(StringVal(name)) :: Nil)).map(_.value).getOrElse {
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
    reference.eval match {
      case obj: ObjectVal =>
        val name = propertyName.evalName
        obj.getProperty(name).map {
          case valueRef: PVar =>
            valueRef
          case value: PVal =>
            val result = PVar(value)
            obj.setProperty(name, result)
            result
        }.getOrElse {
          val result = PVar()
          obj.setProperty(name, result)
          result
        }
      case _ =>
        ctx.log.notice(position, "Trying to get property of non-object")
        PVar(NullVal)
    }
  }

  override def assignVar(valueOrRef: PAny)(implicit ctx: Context) {
    reference.eval match {
      case obj: ObjectVal =>
        val name = propertyName.evalName
        if (obj.getProperty(name).isDefined) {
          obj.setProperty(propertyName.evalName, valueOrRef.value)
        } else {
          ctx match {
            case MethodContext(inst, methodName, _, _) if inst.pClass == obj.pClass && methodName == "__set" =>
              obj.setProperty(name, valueOrRef.value)
            case _ =>
              obj.pClass.findMethod("__set").map(_.invoke(ctx, position, obj, ScalarExpr(StringVal(name)) :: ScalarExpr(valueOrRef.value) :: Nil)).getOrElse {
                obj.setProperty(name, valueOrRef.value)
              }
          }
        }
      case _ =>
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
}
