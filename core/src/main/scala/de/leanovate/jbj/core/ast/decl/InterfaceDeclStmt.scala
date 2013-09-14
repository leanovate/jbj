/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.DeclStmt
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{NamespaceName, SuccessExecResult}
import scala.collection.immutable.List
import scala.collection.mutable
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.types.{PMethod, PInterface}
import de.leanovate.jbj.runtime.value.ConstVal

case class InterfaceDeclStmt(name: NamespaceName, superInterfaces: List[NamespaceName],
                             decls: List[ClassMemberDecl])
  extends DeclStmt with PInterface {

  protected[decl] val _interfaceConstants = mutable.Map.empty[String, ConstVal]

  private var _initialized = false
  private var _interfaces: List[PInterface] = Nil

  override def interfaces = _interfaces

  override def interfaceConstants: Map[String, ConstVal] =
    interfaces.flatMap(_.interfaceConstants.toList).toMap ++ _interfaceConstants.toMap

  override def register(implicit ctx: Context) {
  }

  override def exec(implicit ctx: Context) = {
    if (ctx.global.findInterfaceOrClass(name, autoload = false).isDefined)
      throw new FatalErrorJbjException("Cannot redeclare class %s".format(name))
    else {
      _interfaces = superInterfaces.map {
        interfaceName =>
          ctx.global.findInterfaceOrClass(interfaceName, autoload = true) match {
            case Some(Left(interface)) => interface
            case Some(Right(_)) =>
              throw new FatalErrorJbjException("%s cannot implement %s - it is not an interface".format(name.toString,
                interfaceName.toString))
            case None =>
              throw new FatalErrorJbjException("Interface '%s' not found".format(interfaceName.toString))
          }
      }

      decls.foreach {
        method =>
          ctx.currentPosition = method.position
          method.initializeInterface(this)
      }

      ctx.global.defineInterface(this)
    }
    _initialized = true
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
