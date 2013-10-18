/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

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

  override def typeName(simple: Boolean = false) = "null"

  override def compare(other: PVal)(implicit ctx: Context): Int = other match {
    case BooleanVal(otherBool) => if (otherBool) 1 else 0
    case NumericVal(otherDouble) => 0.0.compare(otherDouble)
    case _ => StringVal.compare(Array[Byte](), other.toStr.chars)
  }

  override def isCallable(implicit ctx: Context) = false

  override def call(params: List[PParam])(implicit ctx: Context) =
    throw new FatalErrorJbjException("Function name must be a string")

  override def phpStr: String = "NULL"

  override def toXml = <null/>
}
