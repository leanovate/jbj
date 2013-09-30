package de.leanovate.jbj.buildins.types

import de.leanovate.jbj.runtime.annotations.InstanceFunction
import de.leanovate.jbj.runtime.types.{ClassTypeHint, PParamDef, PInterfaceMethod, PInterface}
import de.leanovate.jbj.runtime.NamespaceName

trait PSplObserver {
  @InstanceFunction
  def update(subject: PSplSubject)
}

object PSplObserver extends PInterface {
  def name = NamespaceName(relative = false, prefixed = false, "SplObserver")

  def interfaces = List.empty

  def declaredConstants = Map.empty

  def methods = Seq(
    PInterfaceMethod(this, "update", Seq(new PParamDef {
      def name = "subject"

      def hasDefault = false

      def byRef = false

      def typeHint = Some(ClassTypeHint(PSplSubject.name))
    }))
  ).map {
    method =>
      method.name.toLowerCase -> method
  }.toMap
}