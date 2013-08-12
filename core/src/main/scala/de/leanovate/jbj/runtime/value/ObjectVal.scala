package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime._
import scala.collection.mutable
import de.leanovate.jbj.runtime.IntArrayKey
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.ast.NodePosition

class ObjectVal(var pClass: PClass, var instanceNum: Long, var keyValues: mutable.LinkedHashMap[ArrayKey, ValueOrRef])
  extends Value with ArrayLike {
  override def toOutput(implicit ctx: Context) = "Array"

  override def toStr(implicit ctx: Context) = StringVal("object".getBytes(ctx.settings.charset))

  override def toNum(implicit ctx: Context) = toInteger

  override def toDouble(implicit ctx: Context) = DoubleVal(0.0)

  override def toInteger(implicit ctx:Context) = IntegerVal(0)

  override def toBool(implicit ctx:Context) = BooleanVal.FALSE

  override def toArray(implicit ctx:Context) = new ArrayVal(keyValues.clone())

  override def isNull = false

  override def copy = new ObjectVal(pClass, pClass.instanceCounter.incrementAndGet(), keyValues.clone())

  override def incr = this

  override def decr = this

  final def instanceOf(other: PClass): Boolean = other.isAssignableFrom(pClass)

  def getProperty(name: String)(implicit ctx:Context): Option[ValueOrRef] = keyValues.get(StringArrayKey(name))

  def setProperty(name: String, value: ValueOrRef) {
    val key = StringArrayKey(name)
    keyValues.get(key).foreach(_.decrRefCount())
    keyValues.put(key, value)
    value.incrRefCount()
  }

  def unsetProperty(name: String) = {
    keyValues.remove(StringArrayKey(name)).foreach(_.decrRefCount())
  }

  override def getAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) =
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))

  override def setAt(index: Option[ArrayKey], value: ValueOrRef)(implicit ctx: Context, position: NodePosition) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }

  override def unsetAt(index: ArrayKey)(implicit ctx: Context, position: NodePosition) {
    throw new FatalErrorJbjException("Cannot use object of type %s as array".format(pClass.name.toString))
  }
}

object ObjectVal {
  def apply(pClass: PClass, keyValues: (Option[Value], Value)*)(implicit ctx:Context): ObjectVal = {
    var nextIndex: Long = -1

    new ObjectVal(pClass, pClass.instanceCounter.incrementAndGet,
      keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[ArrayKey, ValueOrRef]) {
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
              StringArrayKey(value.toStr.asString)
          }.getOrElse {
            nextIndex += 1
            IntArrayKey(nextIndex)
          }

          keyValue._2.incrRefCount()
          builder += (key -> keyValue._2)
      }.result())
  }

  def unapply(obj: ObjectVal) = Some(obj.pClass, obj.instanceNum, obj.keyValues)
}