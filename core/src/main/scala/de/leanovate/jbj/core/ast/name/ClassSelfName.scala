/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.name

import de.leanovate.jbj.core.ast.Name
import de.leanovate.jbj.runtime.context._
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.context.StaticMethodContext
import de.leanovate.jbj.runtime.context.ClassContext

object ClassSelfName extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = ctx match {
    case MethodContext(_, pMethod, _) =>
      pMethod.declaringClass.name
    case StaticMethodContext(pMethod, _, _) =>
      pMethod.declaringClass.name
    case ClassContext(pClass, _, _) =>
      pClass.name
    case InstanceContext(_, pClass, _) =>
      pClass.name
    case _ =>
      throw new FatalErrorJbjException("Cannot access self:: when no class scope is active")
  }
}
