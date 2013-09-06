/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.core.ast.{ClassEntry, NamespaceName}
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.{PParam, PClass}

object PException extends PClass {
  override def classEntry = ClassEntry.CLASS

  override def name = NamespaceName(relative = false, "Exception")

  override def superClass = None

  override def interfaces = Set.empty

  override def classConstants: Map[String, ConstVal] = Map.empty

  override def initializeStatic(staticContext: ObjectVal)(implicit ctx: Context) {}

  override def initializeInstance(instance: ObjectVal)(implicit ctx: Context) {
    instance.definePublicProperty("message", StringVal(Array.emptyByteArray))
    instance.definePublicProperty("code", IntegerVal(0))
    instance.definePublicProperty("previous", NullVal)
    instance.definePublicProperty("file", StringVal(ctx.currentPosition.fileName))
    instance.definePublicProperty("line", IntegerVal(ctx.currentPosition.line))
  }

  override def newInstance(parameters: List[PParam])(implicit ctx: Context) = {
    val instance = newEmptyInstance(this)

    parameters.map(_.byVal) match {
      case msg :: Nil =>
        instance.definePublicProperty("message", msg.toStr)
      case msg :: c :: Nil => (msg.toStr, c.toInteger, NullVal)
        instance.definePublicProperty("message", msg.toStr)
        instance.definePublicProperty("code", c.toInteger)
      case msg :: c :: prev :: tail => (msg.toStr, c.toInteger, prev)
        instance.definePublicProperty("message", msg.toStr)
        instance.definePublicProperty("code", c.toInteger)
        instance.definePublicProperty("previous", prev)
      case _ =>
    }
    instance
  }

  override def destructInstance(instance: ObjectVal)(implicit ctx: Context) {}

  override def properties = Map.empty

  override def methods = Map.empty

}
