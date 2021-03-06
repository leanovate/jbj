/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.{HttpResponseContext, Context}
import de.leanovate.jbj.runtime.NamespaceName
import de.leanovate.jbj.runtime.types.PFunction
import de.leanovate.jbj.api.http.JbjSettings

case class TestContext() extends Context {
  def name = ???

  def global = ???

  def static = ???

  def settings = new JbjSettings

  def out = ???

  def httpResponseContext = ???

  def err = ???

  def filesystem = ???

  def stack = ???

  def findFunction(name: NamespaceName) = ???

  def defineFunction(function: PFunction) {}

  def findVariable(name: String) = ???

  def defineVariable(name: String, variable: PVar) {}

  def undefineVariable(name: String) {}

  def cleanup() {}
}
