/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.value._
import scala.collection.mutable
import de.leanovate.jbj.runtime.context.Context

object PStdClass extends PClass {
  override def isAbstract = false

  override def isFinal = false

  override def name = NamespaceName(relative = false, "stdClass")

  override def superClass = None

  override def interfaces = Set.empty

  override def classConstants: Map[String, ConstVal] = Map.empty

  override def initializeStatic(staticContext: ObjectVal)(implicit ctx: Context) {}

  override def newInstance(parameters: List[PParam])(implicit ctx: Context) =
    new StdObjectVal(this, ctx.global.instanceCounter.incrementAndGet(), mutable.LinkedHashMap.empty[ObjectPropertyKey.Key, PAny])

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {}

  override def properties = Map.empty

  override def methods = Map.empty
}
