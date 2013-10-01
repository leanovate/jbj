package de.leanovate.jbj.buildins.types

import de.leanovate.jbj.runtime.annotations.InstanceFunction
import de.leanovate.jbj.runtime.types.{PInterfaceAdapter, PInterface}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.adapter.InterfaceFunctions
import de.leanovate.jbj.runtime.value.{ObjectVal, DelegateObjectVal}

trait PSplObserver extends DelegateObjectVal {
  @InstanceFunction
  def update(subject: PSplSubject)
}

object PSplObserver extends PInterface with PInterfaceAdapter[PSplObserver] {
  def name = NamespaceName(relative = false, prefixed = false, "SplObserver")

  def interfaces = List.empty

  def declaredConstants = Map.empty

  def methods = InterfaceFunctions.methods[PSplObserver](this)

  def cast(obj: ObjectVal) = ???
}