package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.runtime.NamespaceName

object PTraversable extends PInterface {
  override def name = NamespaceName(relative = false, "Traversable")

  override def interfaces = List.empty

  override def declaredConstants = Map.empty

  override def methods = Map.empty
}
