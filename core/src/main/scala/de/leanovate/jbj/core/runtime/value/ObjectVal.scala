/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.value

import de.leanovate.jbj.core.runtime._
import scala.collection.mutable
import de.leanovate.jbj.core.runtime.context.Context
import ObjectPropertyKey.{Key, IntKey, PublicKey, ProtectedKey, PrivateKey}

class ObjectVal(var pClass: PClass, var instanceNum: Long, private val keyValueMap: mutable.LinkedHashMap[Key, PAny])
  extends PVal {
  private var _refCount = 0

  private var iteratorState: Option[BufferedIterator[(Any, PAny)]] = None

  def refCount = _refCount

  def keyValues(implicit ctx: Context): Seq[(Key, PAny)] = keyValueMap.toSeq

  override def toOutput(implicit ctx: Context) = "Array"

  override def toStr = StringVal("object".getBytes("UTF-8"))

  override def toNum = toInteger

  override def toDouble = DoubleVal(0.0)

  override def toInteger = IntegerVal(0)

  override def toBool = BooleanVal.TRUE

  override def toArray(implicit ctx: Context) = new ArrayVal(keyValueMap.map {
    case (IntKey(key), value) => key -> value
    case (ProtectedKey(key), value) => "\0*\0" + key -> value
    case (PrivateKey(key, className), value) => "\0" + className + "\0" + key -> value
    case (PublicKey(key), value) => key -> value
  })

  override def isNull = false

  override def copy = this

  override def clone(implicit ctx: Context) = new ObjectVal(pClass, ctx.global.instanceCounter.incrementAndGet(), keyValueMap.clone())

  override def incr = this

  override def decr = this

  override def typeName = "object"

  override def compare(other: PVal): Int = other match {
    case otherObj: ObjectVal =>
      if (pClass != otherObj.pClass)
        return Int.MinValue
      val keyIt = keyValueMap.keysIterator
      while (keyIt.hasNext) {
        val key = keyIt.next()
        val thisVal = keyValueMap(key)
        val otherVal = otherObj.keyValueMap.get(key)

        if (otherVal.isEmpty) {
          if (keyIt.hasNext)
            return Int.MinValue
          else
            return -1
        } else {
          val comp = thisVal.asVal.compare(otherVal.get.asVal)

          if (comp != null)
            return comp
        }
      }
      0
    case _ => 1
  }

  override def retain() {
    _refCount += 1
  }

  override def release()(implicit ctx: Context) {
    _refCount -= 1
    if (_refCount == 0)
      pClass.destructInstance(this)
  }

  final def instanceOf(other: PInterface): Boolean = other.isAssignableFrom(pClass)

  final def instanceOf(other: PClass): Boolean = other.isAssignableFrom(pClass)

  def getProperty(name: String, className: Option[String])(implicit ctx: Context): Option[PAny] = {
    if (className.isDefined) {
      keyValueMap.get(PrivateKey(name, className.get)).map(Some.apply).getOrElse {
        keyValueMap.get(ProtectedKey(name)).map(Some.apply).getOrElse {
          keyValueMap.get(PublicKey(name))
        }
      }
    } else {
      keyValueMap.get(PublicKey(name))
    }
  }

  def definePrivateProperty(name: String, className: String, value: PAny)(implicit ctx: Context) {
    val key = PrivateKey(name, className)
    value.retain()
    keyValueMap.get(key).foreach(_.release())
    keyValueMap.put(key, value)
  }

  def defineProtectedProperty(name: String, value: PAny)(implicit ctx: Context) {
    val key = ProtectedKey(name)
    value.retain()
    keyValueMap.get(key).foreach(_.release())
    keyValueMap.put(key, value)
  }

  def definePublicProperty(name: String, value: PAny)(implicit ctx: Context) {
    val key = PublicKey(name)
    keyValueMap.get(key).foreach(_.release())
    keyValueMap.put(key, value)
  }

  def setProperty(name: String, className: Option[String], value: PAny)(implicit ctx: Context) {
    if (className.isDefined) {
      val privateKey = PrivateKey(name, className.get)
      if (keyValueMap.contains(privateKey)) {
        value.retain()
        keyValueMap.get(privateKey).foreach(_.release())
        keyValueMap.put(privateKey, value)
      } else {
        val protectedKey = ProtectedKey(name)
        if (keyValueMap.contains(protectedKey)) {
          value.retain()
          keyValueMap.get(protectedKey).foreach(_.release())
          keyValueMap.put(protectedKey, value)
        } else {
          val key = PublicKey(name)
          value.retain()
          keyValueMap.get(key).foreach(_.release())
          keyValueMap.put(key, value)
        }
      }
    } else {
      val key = PublicKey(name)
      value.retain()
      keyValueMap.get(key).foreach(_.release())
      keyValueMap.put(key, value)
    }
  }

  def unsetProperty(name: String, className: Option[String])(implicit ctx: Context) = {
    keyValueMap.remove(PublicKey(name)).foreach(_.release())
    if (className.isDefined) {
      keyValueMap.remove(ProtectedKey(name)).foreach(_.release())
      keyValueMap.remove(PrivateKey(name, className.get)).foreach(_.release())
    }
  }

  def iteratorHasNext: Boolean = {
    if (iteratorState.isEmpty) {
      val it = keyValueMap.iterator
      iteratorState = Some(it.buffered)
    }
    iteratorState.get.hasNext
  }

  def iteratorCurrent(implicit ctx: Context): PVal =
    if (!iteratorHasNext) {
      BooleanVal.FALSE
    } else {
      iteratorState.get.head match {
        case (key: Long, value) =>
          ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
            Some(IntegerVal(0)) -> IntegerVal(key), Some(StringVal("key")) -> IntegerVal(key))
        case (key: String, value) =>
          ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
            Some(IntegerVal(0)) -> StringVal(key), Some(StringVal("key")) -> StringVal(key))
      }
    }

  def iteratorNext(implicit ctx: Context): PVal =
    if (!iteratorHasNext) {
      BooleanVal.FALSE
    } else {
      iteratorState.get.next() match {
        case (key: Long, value) =>
          ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
            Some(IntegerVal(0)) -> IntegerVal(key), Some(StringVal("key")) -> IntegerVal(key))
        case (key: String, value) =>
          ArrayVal(Some(IntegerVal(1)) -> value, Some(StringVal("value")) -> value,
            Some(IntegerVal(0)) -> StringVal(key), Some(StringVal("key")) -> StringVal(key))
      }
    }

  def iteratorReset() {
    iteratorState = None
  }

  def cleanup()(implicit ctx: Context) {
    keyValueMap.values.foreach(_.release())
  }
}

object ObjectVal {
  def apply(pClass: PClass, keyValues: (Option[PVal], PVal)*)(implicit ctx: Context): ObjectVal = {
    var nextIndex: Long = -1

    val result = new ObjectVal(pClass, ctx.global.instanceCounter.incrementAndGet,
      keyValues.foldLeft(mutable.LinkedHashMap.newBuilder[Key, PAny]) {
        (builder, keyValue) =>
          val key: Key = keyValue._1.map {
            case IntegerVal(value) =>
              if (value > nextIndex)
                nextIndex = value
              IntKey(value)
            case NumericVal(value) =>
              if (value > nextIndex)
                nextIndex = value.toLong
              IntKey(value.toLong)
            case value =>
              PublicKey(value.toStr.asString)
          }.getOrElse {
            nextIndex += 1
            IntKey(nextIndex)
          }

          builder += (key -> keyValue._2)
      }.result())
    ctx.poolAutoRelease(result)
    result
  }

  def unapply(obj: ObjectVal)(implicit ctx: Context) = Some(obj.pClass, obj.instanceNum, obj.keyValues)
}