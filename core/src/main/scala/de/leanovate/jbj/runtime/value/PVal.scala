package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import scala.xml.NodeSeq

trait PVal extends PAny {
  def toOutput(implicit ctx:Context): String

  def toStr(implicit ctx:Context): StringVal

  def toNum(implicit ctx:Context): NumericVal

  def toInteger(implicit ctx:Context): IntegerVal

  def toDouble(implicit ctx:Context): DoubleVal

  def toBool(implicit ctx:Context): BooleanVal

  def toArray(implicit ctx:Context): ArrayVal

  def isNull: Boolean

  def copy(implicit ctx:Context): PVal

  def incr: PVal

  def decr: PVal

  def typeName:String

  def compare(other:PVal)(implicit ctx:Context): Int

  final override def asVal = this

  final override def asVar = PVar(this)

  def toXml:NodeSeq = NodeSeq.Empty
}
