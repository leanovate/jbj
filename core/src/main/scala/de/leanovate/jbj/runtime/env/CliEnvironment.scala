package de.leanovate.jbj.runtime.env

import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.runtime.value.{VarRef, PAnyVal, StringVal, ArrayVal}
import de.leanovate.jbj.ast.NoNodePosition

object CliEnvironment {
  implicit val position = NoNodePosition

  def commandLine(fileName: String, args: Seq[String])(implicit ctx: Context) {
    val serverArgv = ArrayVal(Option.empty[PAnyVal] -> StringVal(fileName) :: args.map {
      str => Option.empty[PAnyVal] -> StringVal(str)
    }.toList: _*)
    ctx.defineVariable("_SERVER", VarRef(ArrayVal(
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count
    )))
  }
}
