package de.leanovate.jbj

import de.leanovate.jbj.runtime.types.PFunction
import de.leanovate.jbj.runtime.adapter.GlobalFunctions

package object buildins {
  val buildinFunctions: Seq[PFunction] =
    GlobalFunctions.functions(ArrayFunctions) ++
      GlobalFunctions.functions(ClassFunctions) ++
      GlobalFunctions.functions(DateFunctions) ++
      GlobalFunctions.functions(EnvironmentFunctions) ++
      GlobalFunctions.functions(FileFunctions) ++
      GlobalFunctions.functions(FunctionFunctions) ++
      GlobalFunctions.functions(OutputBufferFunctions) ++
      GlobalFunctions.functions(OutputFunctions) ++
      GlobalFunctions.functions(RuntimeFunctions) ++
      GlobalFunctions.functions(StringFunctions) ++
      GlobalFunctions.functions(ValueFunctions)


}
