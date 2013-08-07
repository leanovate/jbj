package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.PFunction
import de.leanovate.jbj.runtime.value.StringVal

object FileFunctions {
  val functions: Seq[PFunction] = Seq(
    BuildinFunction1("dirname", {
      case (ctx, callerPosition, Some(name)) =>
        val fileName = name.toStr.value
        val idx = fileName.lastIndexOf('/')
        StringVal(if (idx >= 0)
          fileName.substring(0, idx)
        else
          "")
    })
  )
}
