package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{StringArrayKey, Value, Context}
import de.leanovate.jbj.runtime.value.{StringVal, ObjectVal, UndefinedVal}
import java.io.PrintStream

case class PropertyReference(reference: Reference, propertyName: Name) extends Reference {
  override def eval(ctx: Context) = {
    reference.eval(ctx) match {
      case obj: ObjectVal =>
        obj.getAt(StringArrayKey(propertyName.evalName(ctx)))
      case _ =>
        ctx.log.notice(position, "Trying to get property of non-object")
        UndefinedVal
    }
  }

  override def assign(ctx: Context, value: Value) {
    reference.eval(ctx) match {
      case obj: ObjectVal =>
        obj.setAt(StringArrayKey(propertyName.evalName(ctx)), value)
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
