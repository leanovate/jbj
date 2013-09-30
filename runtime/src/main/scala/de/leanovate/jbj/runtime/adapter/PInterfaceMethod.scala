package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.api.http.JbjException
import de.leanovate.jbj.runtime.types.{PParam, PMethod, PParamDef, PInterface}

case class PInterfaceMethod(pInterface: PInterface, name: String, parameters: Seq[PParamDef] = Seq.empty) extends PMethod {
  def isFinal = false

  def isProtected = false

  def isStatic = false

  def isAbstract = true

  def isPrivate = false

  def declaringClass = {
    throw new JbjException("No declaring class for interface methods")
  }

  def invoke(instance: ObjectVal, parameters: List[PParam])(implicit callerCtx: Context) = {
    throw new FatalErrorJbjException("Cannot call abstract method %s::%s()".format(pInterface.name.toString, name))
  }

  def invokeStatic(parameters: List[PParam])(implicit callerCtx: Context) = {
    throw new FatalErrorJbjException("Cannot call abstract method %s::%s()".format(pInterface.name.toString, name))
  }
}
