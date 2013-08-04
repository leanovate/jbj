package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{StringArrayKey, Value, Context}
import de.leanovate.jbj.runtime.value.{StringVal, NullVal, ObjectVal}
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.MethodContext

case class PropertyReference(reference: Reference, propertyName: Name) extends Reference {
  override def eval(implicit ctx: Context) = {
    reference.eval match {
      case obj: ObjectVal =>
        val name = propertyName.evalName
        obj.getProperty(name).getOrElse {
          ctx match {
            case MethodContext(inst, _, _, _) if inst.pClass == obj.pClass =>
              ctx.log.notice(position, "Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
              NullVal
            case _ =>
              obj.pClass.findMethod("__get").map(_.call(ctx, position, obj, StringVal(name) :: Nil)).map {
                case Left(v) => v
                case Right(ref) => ref.value
              }.getOrElse {
                ctx.log.notice(position, "Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                NullVal
              }
          }
        }
      case _ =>
        ctx.log.notice(position, "Trying to get property of non-object")
        NullVal
    }
  }

  override def assign(value: Value)(implicit ctx: Context) {
    reference.eval match {
      case obj: ObjectVal =>
        val name = propertyName.evalName
        if (obj.getProperty(name).isDefined) {
          obj.setProperty(propertyName.evalName, value)
        } else {
          ctx match {
            case MethodContext(inst, _, _, _) if inst.pClass == obj.pClass =>
              obj.setProperty(name, value)
            case _ =>
              obj.pClass.findMethod("__set").map(_.call(ctx, position, obj, StringVal(name) :: value :: Nil)).getOrElse {
                obj.setProperty(name, value)
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
