package de.leanovate.jbj.core.runtime

import de.leanovate.jbj.core.runtime.value.{PAny, ObjectVal}
import de.leanovate.jbj.core.ast.{MemberModifier, Expr}
import de.leanovate.jbj.core.runtime.context.Context

trait PMethod {
  def declaringClass: PClass

  def name: String

  def modifieres: Set[MemberModifier.Type]

  def activeModifieres: Set[MemberModifier.Type]

  lazy val isPrivate = activeModifieres.contains(MemberModifier.PRIVATE)

  lazy val isProtected = activeModifieres.contains(MemberModifier.PROTECTED)

  lazy val isStatic = activeModifieres.contains(MemberModifier.STATIC)

  def invoke(ctx: Context, instance: ObjectVal, parameters: List[Expr]): PAny

  def invokeStatic(ctx: Context, parameters: List[Expr]): PAny
}
