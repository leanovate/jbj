package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value.{NullVal, BooleanVal}

package object buildin {
  val buildinFunctions = (StringFunctions.functions ++ VariableFunctions.functions ++ RuntimeFunctions.functions).map {
    function => function.name -> function
  }.toMap

  val buildinConstants = Seq(
    "true" -> BooleanVal.TRUE,
    "false" -> BooleanVal.FALSE,
    "null" -> NullVal
  ).toMap
}
