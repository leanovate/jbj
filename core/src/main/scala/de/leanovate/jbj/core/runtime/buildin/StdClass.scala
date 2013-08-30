/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.buildin

import de.leanovate.jbj.core.runtime.value._
import scala.collection.mutable
import de.leanovate.jbj.core.ast.Expr
import de.leanovate.jbj.core.ast.NamespaceName
import de.leanovate.jbj.core.ast.ClassEntry
import de.leanovate.jbj.core.runtime.PClass
import de.leanovate.jbj.core.runtime.context.{StaticContext, Context}

object StdClass extends PClass {
  override def classEntry = ClassEntry.CLASS

  override def name = NamespaceName(relative = false, "stdClass")

  override def superClass = None

  override def classConstants: Map[String, ConstVal] = Map.empty

  override def initializeStatic(staticContext: StaticContext)(implicit ctx: Context) {}

  override def newInstance(parameters: List[Expr])(implicit ctx: Context) =
    new ObjectVal(this, ctx.global.instanceCounter.incrementAndGet(), mutable.LinkedHashMap.empty[ObjectPropertyKey.Key, PAny])

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {}

  override def methods = Map.empty
}
