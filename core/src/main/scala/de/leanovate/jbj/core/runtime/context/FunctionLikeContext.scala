package de.leanovate.jbj.core.runtime.context

trait FunctionLikeContext extends Context {
  def callerContext: Context
}
