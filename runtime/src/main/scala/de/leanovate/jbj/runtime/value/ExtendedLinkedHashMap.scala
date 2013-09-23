package de.leanovate.jbj.runtime.value

import scala.collection.{Iterator, mutable}

class ExtendedLinkedHashMap[A] extends mutable.LinkedHashMap[A, PAny] {
  def iteratorState = new IteratorState {
    private var cur = firstEntry

    override protected def currentKeyValue = {
      if (hasNext)
        (cur.key, cur.value)
      else
        Iterator.empty.next()
    }

    override def hasNext = cur ne null

    override def advance() {
      if (hasNext)
        cur = cur.later
    }
  }

  def mapDirect[B](f: ((A, PAny)) => (B, PAny)): ExtendedLinkedHashMap[B] = {
    val b = new ExtendedLinkedHashMap[B]
    for (x <- this) b += f(x)
    b
  }

  override def clone(): ExtendedLinkedHashMap[A] = new ExtendedLinkedHashMap[A] ++= repr
}
