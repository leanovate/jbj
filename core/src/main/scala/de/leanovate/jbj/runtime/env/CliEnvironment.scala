package de.leanovate.jbj.runtime.env

import de.leanovate.jbj.runtime.{Context, ValueRef}
import de.leanovate.jbj.runtime.value.{Value, StringVal, ArrayVal}
import de.leanovate.jbj.ast.NoNodePosition

object CliEnvironment {
  implicit val position = NoNodePosition

  def commandLine(fileName: String, args: Seq[String], ctx: Context) {
    val serverArgv = ArrayVal(Option.empty[Value] -> StringVal(fileName) :: args.map {
      str => Option.empty[Value] -> StringVal(str)
    }.toList: _*)
    ctx.defineVariable("_SERVER", ValueRef(ArrayVal(
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count
    )))
  }
}
