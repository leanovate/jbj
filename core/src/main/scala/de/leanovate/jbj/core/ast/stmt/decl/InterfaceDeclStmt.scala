/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.decl

import de.leanovate.jbj.core.ast.{NamespaceName, Stmt}
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.core.runtime.{PMethod, SuccessExecResult, PInterface}
import scala.collection.immutable.List
import scala.collection.mutable
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

case class InterfaceDeclStmt(name: NamespaceName, superInterfaces: List[NamespaceName],
                             decls: List[ClassMemberDecl])
  extends Stmt with PInterface {

  private var _interfaces: Seq[PInterface] = Seq.empty

  override def interfaces = _interfaces

  override def exec(implicit ctx: Context) = {
    if (ctx.global.findInterfaceOrClass(name).isDefined)
      throw new FatalErrorJbjException("Cannot redeclare class %s".format(name))
    else {
      _interfaces = superInterfaces.map {
        interfaceName =>
          ctx.global.findInterfaceOrClass(interfaceName) match {
            case Some(Left(interface)) => interface
            case Some(Right(_)) =>
              throw new FatalErrorJbjException("%s cannot implement %s - it is not an interface".format(name.toString,
                interfaceName.toString))
            case None =>
              throw new FatalErrorJbjException("Interface '%s' not found".format(interfaceName.toString))
          }
      }
      ctx.global.defineInterface(this)
    }
    SuccessExecResult
  }

  override lazy val methods: Map[String, PMethod] = {
    val result = mutable.LinkedHashMap.empty[String, PMethod]

    _interfaces.foreach(result ++= _.methods)
    decls.foreach {
      case method: ClassMethodDecl =>
        result -= method.name.toLowerCase
        result += method.name.toLowerCase -> method
      case _ =>
    }
    result.toMap
  }
}
