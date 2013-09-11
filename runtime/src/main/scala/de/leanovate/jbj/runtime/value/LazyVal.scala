package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

abstract class LazyVal extends PVal {
  def value: PVal

  def toOutput(implicit ctx: Context) = value.toOutput

  def toStr = value.toStr

  def toNum = value.toNum

  def toInteger = value.toInteger

  def toDouble = value.toDouble

  def toBool = value.toBool

  def toArray(implicit ctx: Context) = value.toArray

  def isNull = value.isNull

  def copy = value.copy

  def incr = value.incr

  def decr = value.decr

  def typeName = value.typeName

  def compare(other: PVal) = value.compare(other)

  override def asVal = value

  override def concrete = value
}
