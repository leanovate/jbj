package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

trait PAny {
  def toOutput(implicit ctx: Context): String

  def asVal: PVal

  def asVar: PVar

  def cleanup() {
  }

  def +(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum + other.asVal.toNum

  def -(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum - other.asVal.toNum

  def *(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum * other.asVal.toNum

  def /(other: PAny)(implicit ctx: Context): PAny = this.asVal.toNum / other.asVal.toNum

  def %(other: PAny)(implicit ctx: Context): PAny = this.asVal.toInteger % other.asVal.toInteger

  def __(other: PAny)(implicit ctx: Context): PAny = this.asVal.toStr __ other.asVal.toStr

  def &(other:PAny)(implicit ctx:Context):PAny = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal:StringVal) => leftVal & rightVal
    case (leftVal, rightVal) => leftVal.toInteger & rightVal.toInteger
  }

  def |(other:PAny)(implicit ctx:Context):PAny = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal:StringVal) => leftVal | rightVal
    case (leftVal, rightVal) => leftVal.toInteger | rightVal.toInteger
  }

  def ^(other:PAny)(implicit ctx:Context):PAny = (this.asVal, other.asVal) match {
    case (leftVal: StringVal, rightVal:StringVal) => leftVal ^ rightVal
    case (leftVal, rightVal) => leftVal.toInteger ^ rightVal.toInteger
  }
}
