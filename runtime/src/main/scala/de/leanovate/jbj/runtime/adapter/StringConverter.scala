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
  override def toScalaWithConversion(pAny: PAny)(implicit ctx: Context) = {
    pAny.asVal.concrete match {
      case obj: ObjectVal =>
        val converted = obj.toStr
        if (obj.pClass.findMethod("__toString").isDefined)
          toScala(converted)
        else {
          ctx.log.notice("Object of class %s to string conversion".format(obj.pClass.name.toString))
          "Object"
        }
      case pVal =>
        toScala(pVal.toStr)
    }
  }

  override def toScalaWithConversion(param: PParam)(implicit ctx: Context) = {
    param.byVal.concrete match {
      case obj: ObjectVal =>
        val converted = obj.toStr
        if (obj.pClass.findMethod("__toString").isDefined)
          toScala(converted)
        else {
          ctx.log.notice("Object of class %s to string conversion".format(obj.pClass.name.toString))
          "Object"
        }
      case pVal =>
        toScala(pVal.toStr)
    }
  }

  override def toScala(value: StringVal)(implicit ctx: Context) = value.asString

  override def toJbj(value: String)(implicit ctx: Context) = StringVal(value)
}
