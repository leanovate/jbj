/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.env

import de.leanovate.jbj.runtime.value.{PVar, PVal, StringVal, ArrayVal}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.NoNodePosition

object CliEnvironment {
  implicit val position = NoNodePosition

  def commandLine(fileName: String, args: Seq[String])(implicit ctx: Context) {
    val serverArgv = ArrayVal(Option.empty[PVal] -> StringVal(fileName) :: args.map {
      str => Option.empty[PVal] -> StringVal(str)
    }.toList: _*)
    ctx.defineVariable("_SERVER", PVar(ArrayVal(
      Some(StringVal("argv")) -> serverArgv,
      Some(StringVal("argc")) -> serverArgv.count
    )))
  }
}
