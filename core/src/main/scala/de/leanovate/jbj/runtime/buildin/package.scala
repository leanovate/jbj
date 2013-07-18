package de.leanovate.jbj.runtime

package object buildin {
  val buildinFunctions = Seq(
    StringFunctions.strlen
  ).map {
    function => function.name -> function
  }.toMap
}
