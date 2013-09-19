/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import scala.collection.Map
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.types.{PClass, PInterface, PFunction}
import de.leanovate.jbj.api.http.{JbjProcessContext, JbjEnvironment}
import de.leanovate.jbj.runtime.context.Context

trait JbjRuntimeEnv {
  def preedfinedConstants: Map[String, PVal]

  def predefinedFunctions: Map[Seq[String], PFunction]

  def predefinedInterfaces: Map[Seq[String], PInterface]

  def predefinedClasses: Map[Seq[String], PClass]

  def parse(fileName: String): Option[Either[JbjScript, Throwable]]

  def exec(phpCommands: String, context: Context)
}