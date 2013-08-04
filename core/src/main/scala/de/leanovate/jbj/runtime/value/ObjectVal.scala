package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime._
import java.io.PrintStream
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.ast.NodePosition

class ObjectVal(var pClass: PClass, var instanceNum: Long, var keyValues: mutable.LinkedHashMap[ArrayKey, Value]) extends Value {
  override def toOutput(out: PrintStream) {
    out.print("Array")
  }

  override def toDump(out: PrintStream, ident: String = "") {
    val nextIdent = ident + "  "
    out.println("%sobject(%s)#%d (%d) {".format(ident, pClass.name.toString, instanceNum, keyValues.size))
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

  override def copy = new ObjectVal(pClass, pClass.instanceCounter.incrementAndGet(), keyValues.clone())

  override def incr = this

  override def decr = this

  def getProperty(name: String)(implicit ctx: Context, position: NodePosition): Option[Value] =
    keyValues.get(StringArrayKey(name))

  def setProperty(name: String, value: Value)(implicit ctx: Context, position: NodePosition) {
    keyValues.put(StringArrayKey(name), value)
  }

  override def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) =
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))

  override def setAt(index: Option[ArrayKey], value: Value)(implicit ctx: Context, position: NodePosition) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }
}

object ObjectVal {
  def apply(pClass: PClass, keyValues: List[(Option[Value], Value)]): ObjectVal = {
    var nextIndex: Long = -1

    new ObjectVal(pClass, pClass.instanceCounter.incrementAndGet,
      keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[ArrayKey, Value]) {
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