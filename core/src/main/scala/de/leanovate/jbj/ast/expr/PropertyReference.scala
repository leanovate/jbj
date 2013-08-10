package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.value.StringVal

case class PropertyReference(reference: Reference, propertyName: Name) extends Reference {
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
                obj.pClass.findMethod("__get").map(_.invoke(ctx, position, obj, StringVal(name) :: Nil)).exists(_.value.isNull)
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
              obj.pClass.findMethod("__get").map(_.invoke(ctx, position, obj, StringVal(name) :: Nil)).map(_.value).getOrElse {
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

  override def assignRef(valueOrRef: ValueOrRef)(implicit ctx: Context) {
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
              obj.pClass.findMethod("__set").map(_.invoke(ctx, position, obj, StringVal(name) :: valueOrRef.value :: Nil)).getOrElse {
                obj.setProperty(name, valueOrRef.value)
              }
          }
        }
      case _ =>
        ctx.log.warn(position, "Attempt to assign property of non-object")
    }
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    reference.dump(out, ident + "  ")
    propertyName.dump(out, ident + "  ")
  }
}
