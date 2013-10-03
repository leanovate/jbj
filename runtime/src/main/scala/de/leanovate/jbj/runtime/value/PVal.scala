/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import scala.xml.NodeSeq

trait PVal extends PAny {
  def toOutput(implicit ctx: Context): String

  def toStr(implicit ctx: Context): StringVal

  def toNum: NumericVal

  def toInteger: IntegerVal

  def toDouble: DoubleVal

  def toBool: BooleanVal

  def toArray(implicit ctx: Context): ArrayVal

  def isNull: Boolean

  def copy: PVal

  def clone(implicit ctx: Context): PVal = copy

  def incr: PVal

  def decr: PVal

  def typeName(simple: Boolean = false): String

  def compare(other: PVal)(implicit ctx: Context): Int

  override def asVal = this

  final override def asVar = PVar(asVal)

  def concrete: PConcreteVal

  def toXml: NodeSeq = NodeSeq.Empty

  def foreachByVal[R](f: (PVal, PAny) => Option[R])(implicit ctx: Context): Option[R] = {
    ctx.log.warn("Invalid argument supplied for foreach()")
    None
  }
}
