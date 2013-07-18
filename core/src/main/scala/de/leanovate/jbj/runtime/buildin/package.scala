package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.buildin.VariableFunctions

package object buildin {
  val buildinFunctions = Seq(
    StringFunctions.strlen,
    VariableFunctions.isset
  ).map {
    function => function.name -> function
  }.toMap
}
