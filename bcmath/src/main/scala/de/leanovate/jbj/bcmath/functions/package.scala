package de.leanovate.jbj.bcmath

import de.leanovate.jbj.runtime.types.PFunction
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

package object functions {
  val bcMathFunctions: Seq[PFunction] =
    GlobalFunctions.functions(BcMathFunctions)
}
