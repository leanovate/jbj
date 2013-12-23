package de.leanovate.jbj.buildins

import de.leanovate.jbj.runtime.JbjExtension
import de.leanovate.jbj.runtime.types.PFunction

object ZendExtension extends JbjExtension {
  val name = "zend"

  override def functions: Seq[PFunction] = de.leanovate.jbj.buildins.functions.zendFunctions
}
