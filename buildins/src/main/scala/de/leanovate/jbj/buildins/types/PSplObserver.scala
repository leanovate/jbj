package de.leanovate.jbj.buildins.types

import de.leanovate.jbj.runtime.annotations.InstanceFunction
import de.leanovate.jbj.runtime.types.{ClassTypeHint, PInterface}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.adapter.{SimpleParamDef, PInterfaceMethod}
import de.leanovate.jbj.runtime.value.DelegateObjectVal

trait PSplObserver extends DelegateObjectVal {
  @InstanceFunction
  def update(subject: PSplSubject)
}

object PSplObserver extends PInterface {
  def name = NamespaceName(relative = false, prefixed = false, "SplObserver")

  def interfaces = List.empty

  def declaredConstants = Map.empty

  def methods = Seq(
    PInterfaceMethod(this, "update", Seq(SimpleParamDef("subject", hasDefault = false, byRef = false, Some(ClassTypeHint(PSplSubject.name)))))
  ).map {
    method =>
      method.name.toLowerCase -> method
  }.toMap
}