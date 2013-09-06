/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.NamespaceName

trait Name extends Node {
  def evalName(implicit ctx: Context): String

  def evalNamespaceName(implicit ctx: Context): NamespaceName = NamespaceName(evalName)
}
