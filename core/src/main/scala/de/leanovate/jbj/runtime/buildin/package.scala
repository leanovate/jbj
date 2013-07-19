package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.buildin.{RuntimeFunctions, VariableFunctions}

package object buildin {
  val buildinFunctions = (StringFunctions.functions ++ VariableFunctions.functions ++ RuntimeFunctions.functions).map {
    function => function.name -> function
  }.toMap
}
