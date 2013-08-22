package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import scala.xml.NodeSeq

trait PVal extends PAny {
  def toOutput(implicit ctx:Context): String

  def toStr: StringVal

  def toNum: NumericVal

  def toInteger: IntegerVal

  def toDouble: DoubleVal

  def toBool: BooleanVal

  def toArray(implicit ctx:Context): ArrayVal

  def isNull: Boolean

  def copy(implicit ctx:Context): PVal

  def incr: PVal

  def decr: PVal

  def typeName:String

  def compare(other:PVal): Int

  final override def asVal = this

  final override def asVar = PVar(this)

  def toXml:NodeSeq = NodeSeq.Empty
}
