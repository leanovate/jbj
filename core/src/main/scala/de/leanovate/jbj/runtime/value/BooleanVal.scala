package de.leanovate.jbj.runtime.value

import java.io.PrintStream
import de.leanovate.jbj.runtime.{Context, ArrayKey, Value}
import de.leanovate.jbj.ast.NodePosition

abstract class BooleanVal extends Value {
  def value: Boolean

  override def toBool = this

  override def toNum = toInteger

  override def toArray = ArrayVal(None -> this)

  override def isNull = false

  override def copy = this

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey) = None

  override def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition) {}
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val value = true

    override def toOutput(out: PrintStream) {
      out.print("1")
    }

    override def toDump(out: PrintStream, ident: String = "") {
      out.println("%sbool(true)".format(ident))
    }

    override val toInteger = IntegerVal(1)

    override val toStr = StringVal("1")
  }

  val FALSE = new BooleanVal {
    val value = false

    override def toOutput(out: PrintStream) {}

    override def toDump(out: PrintStream, ident: String = "") {
      out.println("%sbool(false)".format(ident))
    }

    override val toInteger = IntegerVal(0)

    override val toStr = StringVal("")
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE

  def unapply(boolean: BooleanVal) = Some(boolean.value)
}
