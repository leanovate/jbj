package de.leanovate.jbj.runtime

import de.leanovate.jbj.ast.FilePosition

trait PFunction {
  def name: String

  def call(ctx: Context, callerPosition: FilePosition, parameters: List[Value]): Value
}
