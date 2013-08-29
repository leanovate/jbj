/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.value.PAny
import de.leanovate.jbj.core.runtime.context.Context

trait Expr extends Node {
  def isDefined(implicit ctx: Context) = !eval.asVal.isNull

  def eval(implicit ctx: Context): PAny
}