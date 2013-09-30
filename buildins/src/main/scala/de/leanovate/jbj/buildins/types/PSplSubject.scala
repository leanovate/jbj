package de.leanovate.jbj.buildins.types

import de.leanovate.jbj.runtime.types.PInterface
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.adapter.InterfaceFunctions
import de.leanovate.jbj.runtime.annotations.InstanceFunction

trait PSplSubject {
  @InstanceFunction
  def attach(observer: PSplObserver)

  @InstanceFunction
  def detach(observer: PSplObserver)

  @InstanceFunction(Some("notify"))
  def pNotify()
}

object PSplSubject extends PInterface {
  def name = NamespaceName(relative = false, prefixed = false, "SplSubject")

  def interfaces = List.empty

  def declaredConstants = Map.empty

  def methods = InterfaceFunctions.methods[PSplSubject](this)
}