package de.leanovate.jbj.runtime.value

import scala.collection.{Iterator, mutable}
import de.leanovate.jbj.runtime.context.Context

class ExtendedLinkedHashMap[A] extends mutable.LinkedHashMap[A, PAny] {
  def iteratorState = new FloatingIteratorState(firstEntry)

  def mapDirect[B](f: ((A, PAny)) => (B, PAny)): ExtendedLinkedHashMap[B] = {
    val b = new ExtendedLinkedHashMap[B]
    for (x <- this) b += f(x)
    b
  }

  override def clone(): ExtendedLinkedHashMap[A] = new ExtendedLinkedHashMap[A] ++= repr

  class FloatingIteratorState(private var cur: Entry) extends IteratorState {
    override protected def currentKeyValue = {
      if (hasNext)
        (cur.key, cur.value)
      else
        Iterator.empty.next()
    }

    override def currentValue_=(pAny: PAny)(implicit ctx: Context) {
      pAny.retain()
      cur.value.release()
      cur.value = pAny
    }

    override def hasNext = cur ne null

    override def advance() {
      if (hasNext)
        cur = cur.later
    }

    override def copy(fixedEntries: Boolean) = {
      if (fixedEntries) {
        val builder = Seq.newBuilder[Entry]
        var c = cur

        while (c ne null) {
          builder += c
          c = c.later
        }
        new FixedEntryIteratorState(builder.result().iterator.buffered)
      } else
        new FloatingIteratorState(cur)
    }
  }

  class FixedEntryIteratorState(iterator: BufferedIterator[Entry]) extends IteratorState {
    override protected def currentKeyValue = {
      if (hasNext)
        (iterator.head.key, iterator.head.value)
      else
        Iterator.empty.next()
    }

    override def currentValue_=(pAny: PAny)(implicit ctx: Context) {
      pAny.retain()
      iterator.head.value.release()
      iterator.head.value = pAny
    }

    override def hasNext = iterator.hasNext

    override def advance() {
      if (hasNext)
        iterator.next()
    }

    override def copy(fixedEntries: Boolean) = {
      if (hasNext) {
        if (fixedEntries) {
          val builder = Seq.newBuilder[Entry]
          var c = iterator.head

          while (c ne null) {
            builder += c
            c = c.later
          }
          new FixedEntryIteratorState(builder.result().iterator.buffered)
        } else
          new FloatingIteratorState(iterator.head)
      } else {
        new FloatingIteratorState(null)
      }
    }
  }
}
