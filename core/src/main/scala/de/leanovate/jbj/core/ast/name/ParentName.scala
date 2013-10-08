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

object ParentName extends Name {
  override def evalName(implicit ctx: Context) = evalNamespaceName.toString

  override def evalNamespaceName(implicit ctx: Context) = ctx match {
    case MethodContext(instance, pMethod, _) =>
      pMethod.declaringClass.superClass.map(_.name).getOrElse {
        throw new FatalErrorJbjException("Cannot access parent:: when current class scope has no parent")
      }
    case StaticMethodContext(pMethod, _, _) =>
      pMethod.declaringClass.superClass.map(_.name).getOrElse {
        throw new FatalErrorJbjException("Cannot access parent:: when current class scope has no parent")
      }
    case ClassContext(pClass, _, _) =>
      pClass.superClass.map(_.name).getOrElse {
        throw new FatalErrorJbjException("Cannot access parent:: when current class scope has no parent")
      }
    case InstanceContext(_, pClass, _) =>
      pClass.superClass.map(_.name).getOrElse {
        throw new FatalErrorJbjException("Cannot access parent:: when current class scope has no parent")
      }
    case _ =>
      throw new FatalErrorJbjException("Cannot access parent:: when no class scope is active")
  }
}
