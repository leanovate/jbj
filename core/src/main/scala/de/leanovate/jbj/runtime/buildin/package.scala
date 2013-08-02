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
    "E_ERROR" -> IntegerVal(1),
    "E_WARNING" -> IntegerVal(2),
    "E_PARSE" -> IntegerVal(4),
    "E_NOTICE" -> IntegerVal(8),
    "E_CORE_ERROR" -> IntegerVal(16),
    "E_CORE_WARNING" -> IntegerVal(32),
    "E_COMPILE_ERROR" -> IntegerVal(64),
    "E_COMPILE_WARNING" -> IntegerVal(128),
    "E_USER_ERROR" -> IntegerVal(256),
    "E_USER_WARNING" -> IntegerVal(512),
    "E_USER_NOTICE" -> IntegerVal(1024),
    "E_STRICT" -> IntegerVal(2048),
    "E_RECOVERABLE_ERROR" -> IntegerVal(4096),
    "E_DEPRECATED" -> IntegerVal(8192),
    "E_USER_DEPRECATED" -> IntegerVal(16384),
    "E_ALL" -> IntegerVal(32767)
  ).toMap

  val buildinClasses = Seq(
    new PClass {
      def classEntry = ClassEntry.CLASS

      def name = NamespaceName("stdClass")

      def newInstance(ctx: Context, callerPosition: NodePosition, parameters: List[Value]) =
        new ObjectVal(this, mutable.LinkedHashMap.empty[ArrayKey, Value])

      def invokeMethod(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, methodName: String,
                       parameters: List[Value]) = {
        ctx.log.fatal(callerPosition, "Call to undefined method %s::%s()".format(name.toString, methodName))
        Left(UndefinedVal)
      }
    }
  ).map {
    c =>
      c.name -> c
  }.toMap
}
