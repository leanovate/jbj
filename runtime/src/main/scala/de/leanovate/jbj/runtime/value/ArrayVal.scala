/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

class ArrayVal(private var keyValueMap: ExtendedLinkedHashMap[Any]) extends PConcreteVal with ArrayLike {

  private var maxIndex: Long = (-1L :: keyValueMap.keys.map {
    case idx: Long => idx
    case _ => -1L
  }.toList).max

  private var _refCount = 0

  private val iteratorStateHolder = new Holder[IteratorState](keyValueMap.iteratorState(new KeyFilter[Any] {
    def accept(key: Any) = true

    def mapKey(key: Any)(implicit ctx: Context) = key match {
      case key: Long => IntegerVal(key)
      case key: String => StringVal(key)
    }
  }))

  def refCount = _refCount

  def keyValues(implicit ctx: Context): Seq[(PVal, PAny)] = keyValueMap.toSeq.map {
    case (key: Long, value) => IntegerVal(key) -> value
    case (key: String, value) => StringVal(key) -> value
  }

  override def toOutput(implicit ctx: Context) = "Array"

  override def toStr(implicit ctx: Context) = StringVal("Array".getBytes("UTF-8"))

  override def toNum = toInteger

  override def toDouble = DoubleVal(0.0)

  override def toInteger = IntegerVal(keyValueMap.size)

  override def toBool = BooleanVal(!keyValueMap.isEmpty)

  override def toArray(implicit ctx: Context) = this

  override def isNull = false

  def isEmpty = keyValueMap.isEmpty

  override def copy = {
    iteratorStateHolder.clear()
    new ArrayVal(keyValueMap.mapDirect {
      case (key, pVar: PVar) =>
        pVar.retain()
        key -> pVar
      case (key, pVal: PVal) => key -> pVal.copy
    })
  }

  override def incr = this

  override def decr = this

  override def typeName: String = "array"

  override def size: Int = keyValueMap.size

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case otherArray: ArrayVal =>
      val keyIt = keyValueMap.keysIterator
      while (keyIt.hasNext) {
        val key = keyIt.next()
        val thisVal = keyValueMap(key)
        val otherVal = otherArray.keyValueMap.get(key)

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

  override def getAt(index: Long)(implicit ctx: Context): Option[PAny] =
    keyValueMap.get(index)

  override def getAt(index: String)(implicit ctx: Context): Option[PAny] =
    keyValueMap.get(index)

  override def setAt(index: Long, value: PAny)(implicit ctx: Context) = {
    if (index > maxIndex) {
      maxIndex = index
    }
    value.retain()
    keyValueMap.get(index).foreach(_.release())
    keyValueMap.put(index, value)
    value
  }

  override def setAt(index: String, value: PAny)(implicit ctx: Context) = {
    value.retain()
    keyValueMap.get(index).foreach(_.release())
    keyValueMap.put(index, value)
    value
  }

  def prepend(values: PAny*)(implicit ctx: Context) {
    var index: Long = 0
    val newKeyValueMap = new ExtendedLinkedHashMap[Any]
    values.foreach {
      value =>
        value.retain()
        newKeyValueMap.put(index, value)
        index += 1
    }
    keyValueMap.foreach {
      case (_: Long, value) =>
        newKeyValueMap.put(index, value)
        index += 1
      case (key: String, value) =>
        newKeyValueMap.put(key, value)
    }
    keyValueMap = newKeyValueMap
  }

  override def append(value: PAny)(implicit ctx: Context) = {
    maxIndex += 1
    value.retain()
    keyValueMap.get(maxIndex).foreach(_.release())
    keyValueMap.put(maxIndex, value)
    value
  }

  override def unsetAt(index: Long)(implicit ctx: Context) {
    keyValueMap.remove(index).foreach(_.release())
  }

  override def unsetAt(index: String)(implicit ctx: Context) {
    keyValueMap.remove(index).foreach(_.release())
  }

  override def retain() {
    _refCount += 1
  }

  override def release()(implicit ctx: Context) {
    _refCount -= 1
    if (_refCount == 0)
      cleanup()
  }

  def count: IntegerVal = IntegerVal(keyValueMap.size)

  def updateIteratorState(iteratorState: IteratorState): IteratorState = {
    if (iteratorState.isCompatible(keyValueMap))
      iteratorStateHolder.set(iteratorState)
    else
      iteratorStateHolder.clear()
    iteratorStateHolder.get()
  }

  def iteratorHasNext: Boolean = iteratorStateHolder.get().hasNext

  def iteratorCurrent(implicit ctx: Context): PVal = iteratorStateHolder.get().current

  def iteratorNext()(implicit ctx: Context): PVal = iteratorStateHolder.get().next

  def iteratorAdvance() {
    iteratorStateHolder.get().advance()
  }

  def iteratorReset(): IteratorState = {
    iteratorStateHolder.clear()
    iteratorStateHolder.get()
  }

  def cleanup()(implicit ctx: Context) {
    keyValueMap.values.foreach(_.release())
  }

  override def foreachByVal[R](f: (PVal, PAny) => Option[R])(implicit ctx: Context) = {
    val it =  iteratorReset().copy(fixedEntries = true)
    var result = Option.empty[R]
    while (it.hasNext && result.isEmpty) {
      val key = it.currentKey
      val value = it.currentValue
      it.advance()
      updateIteratorState(it.copy(fixedEntries = false))
      result = f(key, value)
    }
    result
  }
}

object ArrayVal {
  def apply(): ArrayVal = new ArrayVal(new ExtendedLinkedHashMap)

  def apply(keyValues: (Option[PVal], PAny)*)(implicit ctx: Context): ArrayVal = {
    var nextIndex: Long = -1

    new ArrayVal(keyValues.foldLeft(new ExtendedLinkedHashMap[Any]) {
      (builder, keyValue) =>
        val key = keyValue._1.map {
          case IntegerVal(value) =>
            if (value > nextIndex)
              nextIndex = value
            value
          case DoubleVal(value) =>
            val intVal = value.toLong
            if (intVal > nextIndex)
              nextIndex = intVal
            intVal
          case str: StringVal if str.isStrongNumericPattern =>
            val intVal = str.toInteger.asLong
            if (intVal > nextIndex)
              nextIndex = intVal
            intVal
          case BooleanVal.TRUE =>
            if (1L > nextIndex)
              nextIndex = 1L
            1L
          case BooleanVal.FALSE =>
            if (0L > nextIndex)
              nextIndex = 0L
            0L
          case value =>
            value.toStr.asString
        }.getOrElse {
          nextIndex += 1
          nextIndex
        }

        builder += key -> keyValue._2
    })
  }

  def unapply(array: ArrayVal)(implicit ctx: Context): Option[Seq[(PVal, PAny)]] = Some(array.keyValues)
}