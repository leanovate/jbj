/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core

import scala.collection.Map
import de.leanovate.jbj.runtime.value.PVal
import de.leanovate.jbj.runtime.{PClass, PInterface, PFunction}
import de.leanovate.jbj.core.ast.Prog

trait JbjRuntimeEnv {
  def preedfinedConstants: Map[String, PVal]

  def predefinedFunctions: Map[Seq[String], PFunction]

  def predefinedInterfaces: Map[Seq[String], PInterface]

  def predefinedClasses: Map[Seq[String], PClass]

  def parse(fileName: String): Option[Either[Prog, Throwable]]
}