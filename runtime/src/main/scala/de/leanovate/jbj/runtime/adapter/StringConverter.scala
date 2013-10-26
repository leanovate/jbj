/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.value.{PAny, ObjectVal, StringVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.types.PParam

object StringConverter extends Converter[String, StringVal] {
  override def typeName = "string"

  override def missingValue(implicit ctx: Context) = ""

  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = {
    pAny.asVal.concrete match {
      case obj: ObjectVal =>
        val converted = obj.toStr
        if (obj.pClass.findMethod("__toString").isDefined)
          converted.asString
        else {
          ctx.log.notice("Object of class %s to string conversion".format(obj.pClass.name.toString))
          "Object"
        }
      case pVal =>
        pVal.toStr.asString
    }
  }

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = {
    param.byVal.concrete match {
      case obj: ObjectVal =>
        val converted = obj.toStr
        if (obj.pClass.findMethod("__toString").isDefined)
          converted.asString
        else {
          ctx.log.notice("Object of class %s to string conversion".format(obj.pClass.name.toString))
          "Object"
        }
      case pVal =>
        pVal.toStr.asString
    }
  }


  def toScala(value: PAny)(implicit ctx: Context) = value.asVal.concrete match {
    case StringVal(str) => Some(str)
    case _ => None
  }

  override def toJbj(value: String)(implicit ctx: Context) = StringVal(value)
}
