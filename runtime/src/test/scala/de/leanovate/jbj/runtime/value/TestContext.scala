/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{NamespaceName, PFunction}
import de.leanovate.jbj.api.JbjSettings

case class TestContext() extends Context {
  def name = ???

  def global = ???

  def static = ???

  def settings = new JbjSettings

  def out = ???

  def err = ???

  def stack = ???

  def findFunction(name: NamespaceName) = ???

  def defineFunction(function: PFunction) {}

  def findVariable(name: String) = ???

  def defineVariable(name: String, variable: PVar) {}

  def undefineVariable(name: String) {}

  def cleanup() {}
}
