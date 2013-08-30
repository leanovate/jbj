/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.decl

import de.leanovate.jbj.core.ast.{NamespaceName, Stmt}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.{SuccessExecResult, PInterface}
import scala.collection.immutable.List

case class InterfaceDeclStmt(name: NamespaceName, superInterfaces: List[NamespaceName],
                             decls: List[ClassMemberDecl])
  extends Stmt with PInterface {

  override def extendedInterfaces = ???

  override def exec(implicit ctx: Context) = {
    if (ctx.global.findInterfaceOrClass(name).isDefined)
      ctx.log.fatal("Cannot redeclare class %s".format(name))
    else {
      ctx.global.defineInterface(this)
    }
    SuccessExecResult
  }
}
