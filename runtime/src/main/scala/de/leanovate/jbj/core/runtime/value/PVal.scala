/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.value

import de.leanovate.jbj.core.runtime.context.Context
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

  def copy: PVal

  def clone(implicit ctx:Context): PVal = copy

  def incr: PVal

  def decr: PVal

  def typeName:String

  def compare(other:PVal): Int

  final override def asVal = this

  final override def asVar = PVar(this)

  def toXml:NodeSeq = NodeSeq.Empty
}
