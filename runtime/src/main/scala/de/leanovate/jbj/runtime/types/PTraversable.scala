package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

object PTraversable extends PInterface {
  override def name = NamespaceName(relative = false, prefixed = false, "Traversable")

  override def interfaces = List.empty

  override def declaredConstants = Map.empty

  override def methods = Map.empty

  override def initializeClass(pClass: PClass)(implicit ctx: Context) {
    if (!PIterator.isAssignableFrom(pClass) && !PIteratorAggregate.isAssignableFrom(pClass)) {
      throw new FatalErrorJbjException("Class test must implement interface Traversable as part of either Iterator or IteratorAggregate")
    }
  }
}
