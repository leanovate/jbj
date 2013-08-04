package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime._
import java.io.PrintStream
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import scala.util.parsing.input.NoPosition
import de.leanovate.jbj.ast.{NodePosition, NoNodePosition}

class ObjectVal(var pClass: PClass, var keyValues: mutable.LinkedHashMap[ArrayKey, Value]) extends Value {
  override def toOutput(out: PrintStream) {
    out.print("Array")
  }

  override def toDump(out: PrintStream, ident: String = "") {
    val nextIdent = ident + "  "
    out.println("%sarray(%d) {".format(ident, keyValues.size))
    keyValues.foreach {
      case (IntArrayKey(key), value) =>
        out.println("%s[%d]=>".format(nextIdent, key))
        value.toDump(out, ident + "  ")
      case (StringArrayKey(key), value) =>
        out.println( """%s["%s"]=>""".format(nextIdent, key))
        value.toDump(out, ident + "  ")
    }
    out.println("%s}".format(ident))
  }

  override def toStr = StringVal("object")

  override def toNum = toInteger

  override def toInteger = IntegerVal(0)

  override def toBool = BooleanVal.FALSE

  override def toArray = new ArrayVal(keyValues.clone())

  override def isNull = false

  override def isUndefined = false

  override def copy = new ObjectVal(pClass, keyValues.clone())

  override def incr = this

  override def decr = this

  override def getAt(index: ArrayKey) = keyValues.getOrElse(index, UndefinedVal)

  override def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition) {
    if (index.isDefined)
      keyValues.put(index.get, value)
    else
      throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }
}

object ObjectVal {
  def apply(pClass: PClass, keyValues: List[(Option[Value], Value)]): ObjectVal = {
    var nextIndex: Long = -1

    new ObjectVal(pClass, keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[ArrayKey, Value]) {
      (builder, keyValue) =>
        val key = keyValue._1.map {
          case IntegerVal(value) =>
            if (value > nextIndex)
              nextIndex = value
            IntArrayKey(value)
          case NumericVal(value) =>
            if (value > nextIndex)
              nextIndex = value.toInt
            IntArrayKey(value.toInt)
          case value =>
            StringArrayKey(value.toStr.value)
        }.getOrElse {
          nextIndex += 1
          IntArrayKey(nextIndex)
        }

        builder += (key -> keyValue._2)
    }.result())
  }
}