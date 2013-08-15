package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.annotations.GlobalFunction

object FileFunctions extends WrappedFunctions {
  @GlobalFunction
  def dirname(fileName: String): String = {
    val idx = fileName.lastIndexOf('/')
    if (idx >= 0)
      fileName.substring(0, idx)
    else
      ""
  }
}
