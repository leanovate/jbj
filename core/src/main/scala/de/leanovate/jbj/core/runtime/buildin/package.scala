/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime

import de.leanovate.jbj.core.runtime.value._
import de.leanovate.jbj.core.ast.{Expr, ClassEntry, NamespaceName}
import de.leanovate.jbj.core.runtime.value.IntegerVal
import de.leanovate.jbj.core.runtime.context.{StaticContext, Context}
import de.leanovate.jbj.api.JbjSettings
import scala.collection.JavaConverters._

/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package object buildin {
  val buildinFunctions: Seq[PFunction] =
    ArrayFunctions.functions ++
      ClassFunctions.functions ++
      DateFunctions.functions ++
      FileFunctions.functions ++
      FunctionFunctions.functions ++
      StringFunctions.functions ++
      OutputFunctions.functions ++
      OutputBufferFunctions.functions ++
      RuntimeFunctions.functions

  val buildinConstants: Seq[(String, PVal)] = Seq(
    "TRUE" -> BooleanVal.TRUE,
    "FALSE" -> BooleanVal.FALSE,
    "NULL" -> NullVal,
    "E_ERROR" -> IntegerVal(JbjSettings.ErrorLevel.E_ERROR.getValue),
    "E_WARNING" -> IntegerVal(JbjSettings.ErrorLevel.E_WARNING.getValue),
    "E_PARSE" -> IntegerVal(JbjSettings.ErrorLevel.E_PARSE.getValue),
    "E_NOTICE" -> IntegerVal(JbjSettings.ErrorLevel.E_NOTICE.getValue),
    "E_CORE_ERROR" -> IntegerVal(JbjSettings.ErrorLevel.E_CORE_ERROR.getValue),
    "E_CORE_WARNING" -> IntegerVal(JbjSettings.ErrorLevel.E_CORE_WARNING.getValue),
    "E_COMPILE_ERROR" -> IntegerVal(JbjSettings.ErrorLevel.E_COMPILE_ERROR.getValue),
    "E_COMPILE_WARNING" -> IntegerVal(JbjSettings.ErrorLevel.E_COMPILE_WARNING.getValue),
    "E_USER_ERROR" -> IntegerVal(JbjSettings.ErrorLevel.E_USER_ERROR.getValue),
    "E_USER_WARNING" -> IntegerVal(JbjSettings.ErrorLevel.E_USER_WARNING.getValue),
    "E_USER_NOTICE" -> IntegerVal(JbjSettings.ErrorLevel.E_USER_NOTICE.getValue),
    "E_STRICT" -> IntegerVal(JbjSettings.ErrorLevel.E_STRICT.getValue),
    "E_RECOVERABLE_ERROR" -> IntegerVal(JbjSettings.ErrorLevel.E_RECOVERABLE_ERROR.getValue),
    "E_DEPRECATED" -> IntegerVal(JbjSettings.ErrorLevel.E_DEPRECATED.getValue),
    "E_USER_DEPRECATED" -> IntegerVal(JbjSettings.ErrorLevel.E_USER_DEPRECATED.getValue),
    "E_ALL" -> IntegerVal(JbjSettings.E_ALL.asScala.foldLeft(0) {
      (v, enum) => v | enum.getValue
    })
  )


  val Exception = new PClass {
    override def classEntry = ClassEntry.CLASS

    override def name = NamespaceName(relative = false, "Exception")

    override def superClass = None

    override def classConstants: Map[String, ConstVal] = Map.empty

    override def initializeStatic(staticContext: StaticContext)(implicit ctx: Context) {}

    override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
      instance.definePublicProperty("message", StringVal(Array.emptyByteArray))
      instance.definePublicProperty("code", IntegerVal(0))
      instance.definePublicProperty("previous", NullVal)
      instance.definePublicProperty("file", StringVal(ctx.currentPosition.fileName))
      instance.definePublicProperty("line", IntegerVal(ctx.currentPosition.line))
    }

    override def newInstance(parameters: List[Expr])(implicit ctx: Context) = {
      val instance = newEmptyInstance(this)

      parameters.map(_.eval(ctx).asVal) match {
        case msg :: Nil =>
          instance.definePublicProperty("message", msg.toStr)
        case msg :: c :: Nil => (msg.toStr, c.toInteger, NullVal)
          instance.definePublicProperty("message", msg.toStr)
          instance.definePublicProperty("code", c.toInteger)
        case msg :: c :: prev :: tail => (msg.toStr, c.toInteger, prev)
          instance.definePublicProperty("message", msg.toStr)
          instance.definePublicProperty("code", c.toInteger)
          instance.definePublicProperty("previous", prev)
        case _ =>
      }
      instance
    }

    override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {}

    override def methods = Map.empty
  }

  val buildinClasses: Seq[PClass] = Seq(StdClass, Exception)
}
