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

abstract class BooleanVal extends PConcreteVal {
  def asBoolean: Boolean

  override def toBool = this

  override def toNum(implicit ctx: Context) = toInteger

  override def toArray(implicit ctx: Context) = ArrayVal(None -> this)

  override def isScalar = true

  override def isNull = false

  override def copy = this

  override def incr(implicit ctx: Context) = this

  override def decr(implicit ctx: Context) = this

  override def typeName(simple: Boolean = false) = "boolean"

  override def isCallable(implicit ctx: Context) = false

  override def call(params: List[PParam])(implicit ctx: Context) =
    throw new FatalErrorJbjException("Function name must be a string")
}

object BooleanVal {
  val TRUE = new BooleanVal {
    val asBoolean = true

    override def toOutput(implicit ctx: Context) = "1"

    override def toDouble = DoubleVal(1.0)

    override def toInteger(implicit ctx: Context) = IntegerVal(1)

    override def toStr(implicit ctx: Context) = StringVal("1".getBytes("UTF-8"))

    override def compare(other: PVal)(implicit ctx: Context): Int = other match {
      case BooleanVal(otherBool) => if (otherBool) 0 else 1
      case NumericVal(otherDouble) => 1.0.compare(otherDouble)
      case str: StringVal => if (str.chars.length > 0) 0 else 1
      case array: ArrayVal => if (!array.isEmpty) 0 else 1
      case _ => 1
    }

    override def toXml = <true/>
  }

  val FALSE = new BooleanVal {
    val asBoolean = false

    override def toOutput(implicit ctx: Context) = ""

    override def toDouble = DoubleVal(0.0)

    override def toInteger(implicit ctx: Context) = IntegerVal(0)

    override def toStr(implicit ctx: Context) = StringVal(Array.emptyByteArray)

    override def compare(other: PVal)(implicit ctx: Context): Int = other match {
      case BooleanVal(otherBool) => if (otherBool) -1 else 0
      case NumericVal(otherDouble) => 0.0.compare(otherDouble)
      case str: StringVal => if (str.chars.length > 0) -1 else 0
      case array: ArrayVal => if (!array.isEmpty) -1 else 0
      case _ => 0
    }

    override def toXml = <false/>
  }

  def apply(value: Boolean): BooleanVal = if (value) TRUE else FALSE

  def unapply(boolean: BooleanVal) = Some(boolean.asBoolean)
}
