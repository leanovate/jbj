package de.leanovate.jbj.ast

package object buildin {
  val buildinFunctions = Seq(
    StringFunctions.strlen
  ).map {
    function => function.name -> function
  }.toMap
}
