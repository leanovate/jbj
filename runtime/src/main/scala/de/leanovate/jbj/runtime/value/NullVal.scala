/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

object NullVal extends PConcreteVal {
  override def toOutput(implicit ctx: Context) = ""

  override def toStr(implicit ctx: Context) = StringVal(Array.empty[Byte])

  override def toNum = toInteger

  override def toDouble = DoubleVal(0.0)

  override def toInteger = IntegerVal(0)

  override def toBool = BooleanVal.FALSE

  override def toArray(implicit ctx: Context) = ArrayVal()

  override def isNull = true

  override def copy = this

  override def incr = IntegerVal(1)

  override def decr = NullVal

  override def typeName = "null"

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case BooleanVal(otherBool) => if (otherBool) 1 else 0
    case NumericVal(otherDouble) => 0.0.compare(otherDouble)
    case _ => StringVal.compare(Array[Byte](), other.toStr.chars)
  }

  override def toXml = <null/>
}
