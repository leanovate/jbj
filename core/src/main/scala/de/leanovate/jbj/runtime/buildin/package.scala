package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.ast.{Expr, ClassEntry, NamespaceName, NodePosition}
import scala.collection.Map
import de.leanovate.jbj.runtime.value.IntegerVal

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

    override def initializeInstance(instance: ObjectVal)(implicit ctx: Context, callerPosition: NodePosition) {
      instance.setProperty("message", None, StringVal(Array.emptyByteArray))
      instance.setProperty("code", None, IntegerVal(0))
      instance.setProperty("previous", None, NullVal)
      instance.setProperty("file", None, StringVal(callerPosition.fileName))
      instance.setProperty("line", None, IntegerVal(callerPosition.line))
    }

    override def newInstance(parameters: List[Expr])(implicit ctx: Context, callerPosition: NodePosition) = {
      val instance = newEmptyInstance(this)

      parameters.map(_.eval(ctx)) match {
        case msg :: Nil =>
          instance.setAt("message", msg.toStr)(ctx, callerPosition)
        case msg :: c :: Nil => (msg.toStr, c.toInteger, NullVal)
          instance.setAt("message", msg.toStr)(ctx, callerPosition)
          instance.setAt("code", c.toInteger)(ctx, callerPosition)
        case msg :: c :: prev :: tail => (msg.toStr, c.toInteger, prev)
          instance.setAt("message", msg.toStr)(ctx, callerPosition)
          instance.setAt("code", c.toInteger)(ctx, callerPosition)
          instance.setAt("previous", prev)(ctx, callerPosition)
        case _ =>
      }
      instance
    }

    override def methods = Map.empty
  }

  val buildinClasses: Seq[PClass] = Seq(StdClass, Exception)
}
