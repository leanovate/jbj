package de.leanovate.jbj.runtime


trait Function {
  def name: String

  def call(ctx: Context, parameters: List[Value]): Value
}
