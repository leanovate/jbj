package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{Name, Reference}
import de.leanovate.jbj.runtime.{StringArrayKey, Value, Context}
import de.leanovate.jbj.runtime.value.{StringVal, ObjectVal, UndefinedVal}
import java.io.PrintStream

case class PropertyReference(reference: Reference, propertyName: Name) extends Reference {
  override def eval(implicit ctx: Context) = {
    reference.eval match {
      case obj: ObjectVal =>
        obj.getAt(StringArrayKey(propertyName.evalName))
      case _ =>
        ctx.log.notice(position, "Trying to get property of non-object")
        UndefinedVal
    }
  }

  override def assign(value: Value)(implicit ctx: Context) {
    reference.eval match {
      case obj: ObjectVal =>
        obj.setAt(Some(StringArrayKey(propertyName.evalName)), value)
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
