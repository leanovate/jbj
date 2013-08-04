package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.ast.{ClassEntry, NamespaceName, NodePosition}
import scala.collection.mutable
import de.leanovate.jbj.runtime.value.IntegerVal

package object buildin {
  val buildinFunctions = (ArrayFunctions.functions ++ ClassFunctions.functions ++ StringFunctions.functions ++
    VariableFunctions.functions ++ RuntimeFunctions.functions).map {
    function => function.name -> function
  }.toMap

  val buildinConstants = Seq(
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
  ).toMap

  val buildinClasses = Seq(
    new PClass {
      def classEntry = ClassEntry.CLASS

      def name = NamespaceName("stdClass")

      def newInstance(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) =
        new ObjectVal(this, instanceCounter.incrementAndGet(), mutable.LinkedHashMap.empty[ArrayKey, Value])

      def invokeMethod(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, methodName: String,
                       parameters: List[Value]) = {
        ctx.log.fatal(callerPosition, "Call to undefined method %s::%s()".format(name.toString, methodName))
        Left(NullVal)
      }

      def findMethod(methodName: String): Option[PMethod] = None
    }
  ).map {
    c =>
      c.name -> c
  }.toMap
}
