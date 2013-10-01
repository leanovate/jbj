package de.leanovate.jbj.buildins.types

import de.leanovate.jbj.runtime.annotations.InstanceFunction
import de.leanovate.jbj.runtime.types.{PInterfaceAdapter, PInterface}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.adapter.InterfaceFunctions
import de.leanovate.jbj.runtime.value.{ObjectVal, DelegateObjectVal}
import de.leanovate.jbj.runtime.context.Context

trait PSplObserver extends DelegateObjectVal {
  @InstanceFunction
  def update(subject: PSplSubject)(implicit ctx: Context)
}

object PSplObserver extends PInterface with PInterfaceAdapter[PSplObserver] {
  def name = NamespaceName(relative = false, prefixed = false, "SplObserver")

  def interfaces = List.empty

  def declaredConstants = Map.empty

  def methods = InterfaceFunctions.methods[PSplObserver]

  def cast(obj: ObjectVal) = InterfaceFunctions.cast[PSplObserver](obj)
}