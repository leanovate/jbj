package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.ast.{Expr, ClassEntry, NamespaceName}
import scala.collection.Map
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.context.Context

package object buildin {
  val buildinFunctions: Seq[PFunction] =
    ArrayFunctions.functions ++
      ClassFunctions.functions ++
      DateFunctions.functions ++
      FileFunctions.functions ++
      FunctionFunctions.functions ++
      StringFunctions.functions ++
      OutputFunctions.functions ++
      RuntimeFunctions.functions

  val buildinConstants: Seq[(String, PVal)] = Seq(
    "TRUE" -> BooleanVal.TRUE,
    "FALSE" -> BooleanVal.FALSE,
    "NULL" -> NullVal,
    "E_ERROR" -> IntegerVal(Settings.E_ERROR),
    "E_WARNING" -> IntegerVal(Settings.E_WARNING),
    "E_PARSE" -> IntegerVal(Settings.E_PARSE),
    "E_NOTICE" -> IntegerVal(Settings.E_NOTICE),
    "E_CORE_ERROR" -> IntegerVal(Settings.E_CORE_ERROR),
    "E_CORE_WARNING" -> IntegerVal(Settings.E_CORE_WARNING),
    "E_COMPILE_ERROR" -> IntegerVal(Settings.E_COMPILE_ERROR),
    "E_COMPILE_WARNING" -> IntegerVal(Settings.E_COMPILE_WARNING),
    "E_USER_ERROR" -> IntegerVal(Settings.E_USER_ERROR),
    "E_USER_WARNING" -> IntegerVal(Settings.E_USER_WARNING),
    "E_USER_NOTICE" -> IntegerVal(Settings.E_USER_NOTICE),
    "E_STRICT" -> IntegerVal(Settings.E_STRICT),
    "E_RECOVERABLE_ERROR" -> IntegerVal(Settings.E_RECOVERABLE_ERROR),
    "E_DEPRECATED" -> IntegerVal(Settings.E_DEPRECATED),
    "E_USER_DEPRECATED" -> IntegerVal(Settings.E_USER_DEPRECATED),
    "E_ALL" -> IntegerVal(Settings.E_ALL)
  )


  val Exception = new PClass {
    override def classEntry = ClassEntry.CLASS

    override def name = NamespaceName(relative = false, "Exception")

    override def superClass = None

    override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
      instance.setProperty("message", None, StringVal(Array.emptyByteArray))
      instance.setProperty("code", None, IntegerVal(0))
      instance.setProperty("previous", None, NullVal)
      instance.setProperty("file", None, StringVal(ctx.currentPosition.fileName))
      instance.setProperty("line", None, IntegerVal(ctx.currentPosition.line))
    }

    override def newInstance(parameters: List[Expr])(implicit ctx: Context) = {
      val instance = newEmptyInstance(this)

      parameters.map(_.evalOld(ctx)) match {
        case msg :: Nil =>
          instance.setAt("message", msg.toStr)(ctx)
        case msg :: c :: Nil => (msg.toStr, c.toInteger, NullVal)
          instance.setAt("message", msg.toStr)(ctx)
          instance.setAt("code", c.toInteger)(ctx)
        case msg :: c :: prev :: tail => (msg.toStr, c.toInteger, prev)
          instance.setAt("message", msg.toStr)(ctx)
          instance.setAt("code", c.toInteger)(ctx)
          instance.setAt("previous", prev)(ctx)
        case _ =>
      }
      instance
    }

    override def methods = Map.empty
  }

  val buildinClasses: Seq[PClass] = Seq(StdClass, Exception)
}
