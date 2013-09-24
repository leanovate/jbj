package de.leanovate.jbj.runtime.value

import scala.collection.{Iterator, mutable}
import de.leanovate.jbj.runtime.context.Context

trait KeyFilter[A] {
  def accept(key: A): Boolean

  def mapKey(key: A)(implicit ctx: Context): PVal
}

class ExtendedLinkedHashMap[A] extends mutable.LinkedHashMap[A, PAny] {
  def iteratorState(keyFilter: KeyFilter[A]) = new FloatingIteratorState(keyFilter, firstEntry)

  def mapDirect[B](f: ((A, PAny)) => (B, PAny)): ExtendedLinkedHashMap[B] = {
    val b = new ExtendedLinkedHashMap[B]
    for (x <- this) b += f(x)
    b
  }

  override def clone(): ExtendedLinkedHashMap[A] = new ExtendedLinkedHashMap[A] ++= repr

  class FloatingIteratorState(keyFilter: KeyFilter[A], private var cur: Entry) extends IteratorState {
    while ((cur ne null) && !keyFilter.accept(cur.key)) {
      cur = cur.later
    }

    override protected def currentKeyValue(implicit ctx: Context) = {
      if (hasNext)
        (keyFilter.mapKey(cur.key), cur.value)
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
      cur = cur.later
      while ((cur ne null) && !keyFilter.accept(cur.key)) {
        cur = cur.later
      }
    }

    override def copy(fixedEntries: Boolean) = {
      if (fixedEntries) {
        val builder = Seq.newBuilder[Entry]
        var c = cur

        while (c ne null) {
          if (keyFilter.accept(c.key))
            builder += c
          c = c.later
        }
        new FixedEntryIteratorState(keyFilter, builder.result().iterator.buffered)
      } else
        new FloatingIteratorState(keyFilter, cur)
    }
  }

  class FixedEntryIteratorState(keyFilter: KeyFilter[A], iterator: BufferedIterator[Entry]) extends IteratorState {
    override protected def currentKeyValue(implicit ctx: Context) = {
      if (hasNext)
        (keyFilter.mapKey(iterator.head.key), iterator.head.value)
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
            if (keyFilter.accept(c.key))
              builder += c
            c = c.later
          }
          new FixedEntryIteratorState(keyFilter, builder.result().iterator.buffered)
        } else
          new FloatingIteratorState(keyFilter, iterator.head)
      } else {
        new FloatingIteratorState(keyFilter, null)
      }
    }
  }

}
