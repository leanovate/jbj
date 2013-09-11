package de.leanovate.jbj.runtime.types

import de.leanovate.jbj.api.JbjException
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class PInterfaceMethod(var name: String, pInterface: PInterface) extends PMethod {
  def isFinal = false

  def isProtected = false

  def isStatic = false

  def isAbstract = true

  def isPrivate = false

  def declaringClass = {
    throw new JbjException("No declaring class for interface methods")
  }

  def invoke(ctx: Context, instance: ObjectVal, parameters: List[PParam]) = {
    throw new FatalErrorJbjException("Cannot call abstract method %s::%s()".format(pInterface.name.toString, name))(ctx)
  }

  def invokeStatic(ctx: Context, parameters: List[PParam]) = {
    throw new FatalErrorJbjException("Cannot call abstract method %s::%s()".format(pInterface.name.toString, name))(ctx)
  }
}
