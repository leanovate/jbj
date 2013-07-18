package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.buildin.{RuntimeFunctions, VariableFunctions}

package object buildin {
  val buildinFunctions = Seq(
    StringFunctions.strlen,
    VariableFunctions.isset,
    RuntimeFunctions.error_reporting
  ).map {
    function => function.name -> function
  }.toMap
}
