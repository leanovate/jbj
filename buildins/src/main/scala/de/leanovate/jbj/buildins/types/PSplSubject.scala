package de.leanovate.jbj.buildins.types

import de.leanovate.jbj.runtime.types.{PInterfaceAdapter, PInterface}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.adapter.InterfaceFunctions
import de.leanovate.jbj.runtime.annotations.InstanceFunction
import de.leanovate.jbj.runtime.value.{ObjectVal, DelegateObjectVal}
import de.leanovate.jbj.runtime.context.Context

trait PSplSubject extends DelegateObjectVal {
  @InstanceFunction
  def attach(observer: PSplObserver)(implicit ctx: Context)

  @InstanceFunction
  def detach(observer: PSplObserver)(implicit ctx: Context)

  @InstanceFunction(Some("notify"))
  def pNotify()(implicit ctx: Context)
}

object PSplSubject extends PInterface with PInterfaceAdapter[PSplSubject] {
  def name = NamespaceName(relative = false, prefixed = false, "SplSubject")

  def interfaces = List.empty

  def declaredConstants = Map.empty

  def methods = InterfaceFunctions.methods[PSplSubject]

  def cast(obj: ObjectVal) = InterfaceFunctions.cast[PSplSubject](obj)
}