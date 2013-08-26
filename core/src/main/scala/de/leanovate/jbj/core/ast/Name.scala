package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.context.Context

trait Name extends Node {
  def evalName(implicit ctx: Context): String

  def evalNamespaceName(implicit ctx: Context): NamespaceName = NamespaceName(evalName)
}
