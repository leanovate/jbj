package de.leanovate.jbj.buildins

import de.leanovate.jbj.runtime.types.PFunction

package object functions {
  val standardFunctions: Seq[PFunction] =
    ArrayFunctions.functions ++
      ClassFunctions.functions ++
      DateFunctions.functions ++
      FileFunctions.functions ++
      FunctionFunctions.functions ++
      HeadFunctions.functions ++
      OutputBufferFunctions.functions ++
      OutputFunctions.functions ++
      RuntimeFunctions.functions ++
      SessionFunctions.functions ++
      StringFunctions.functions ++
      ValueFunctions.functions

  val zendFunctions: Seq[PFunction] =
    ZendFunctions.functions
}
