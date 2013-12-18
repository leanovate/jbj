package de.leanovate.jbj.bcmath

import de.leanovate.jbj.runtime.JbjExtension
import de.leanovate.jbj.runtime.types.PFunction

object BcMathExtension extends JbjExtension {
  val name = "bcmath"

  override def functions: Seq[PFunction] = de.leanovate.jbj.bcmath.functions.bcMathFunctions
}
