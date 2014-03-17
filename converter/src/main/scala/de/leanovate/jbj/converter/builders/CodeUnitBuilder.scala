/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter.builders

import scala.text.Document

trait CodeUnitBuilder extends Builder {
  def defineFunction(name:String, code: Document)

  def defineLocalVar(name: String)

  def isFunctionDirect(name: String): Boolean
}
