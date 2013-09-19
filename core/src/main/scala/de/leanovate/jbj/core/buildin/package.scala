/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import de.leanovate.jbj.runtime.value._
import scala.collection.JavaConverters._
import de.leanovate.jbj.runtime.adapter.GlobalFunctions
import de.leanovate.jbj.runtime.types._
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.output.OutputHandler
import de.leanovate.jbj.api.http.JbjSettings

package object buildin {
  val buildinFunctions: Seq[PFunction] =
    GlobalFunctions.functions(ArrayFunctions) ++
      GlobalFunctions.functions(ClassFunctions) ++
      GlobalFunctions.functions(DateFunctions) ++
      GlobalFunctions.functions(EnvironmentFunctions) ++
      GlobalFunctions.functions(FileFunctions) ++
      GlobalFunctions.functions(FunctionFunctions) ++
      GlobalFunctions.functions(StringFunctions) ++
      GlobalFunctions.functions(OutputFunctions) ++
      GlobalFunctions.functions(OutputBufferFunctions) ++
      GlobalFunctions.functions(RuntimeFunctions) ++
      GlobalFunctions.functions(ValueFunctions)

  val buildinConstants: Seq[(String, PVal)] = Seq(
    "TRUE" -> BooleanVal.TRUE,
    "FALSE" -> BooleanVal.FALSE,
    "NULL" -> NullVal,
    "PHP_OUTPUT_HANDLER_START" -> IntegerVal(OutputHandler.PHP_OUTPUT_HANDLER_START),
    "PHP_OUTPUT_HANDLER_END" -> IntegerVal(OutputHandler.PHP_OUTPUT_HANDLER_END),
    "DIRECTORY_SEPARATOR" -> new StringVal(Array[Byte]('/')),
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

  val buildinInterfaces: Seq[PInterface] = Seq(PArrayAccess)

  val buildinClasses: Seq[PClass] = Seq(PStdClass, PException, PArrayObject)
}
