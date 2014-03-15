package de.leanovate.jbj.buildins

import de.leanovate.jbj.runtime.JbjExtension
import de.leanovate.jbj.runtime.types.PFunction
import de.leanovate.jbj.buildins.functions.ZendFunctions

object ZendExtension extends JbjExtension with ZendFunctions {
  val name = "zend"

  override def functions: Seq[PFunction] = de.leanovate.jbj.buildins.functions.zendFunctions
}
