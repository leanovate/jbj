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
import de.leanovate.jbj.runtime.value.PVal

case class InterfaceDeclStmt(declaredName: NamespaceName, superInterfaces: List[NamespaceName],
                             decls: List[ClassMemberDecl])
  extends DeclStmt with PInterface {

  protected[decl] val _interfaceConstants = mutable.Map.empty[String, PVal]

  private var _initialized = false
  private var _name = declaredName
  private var _interfaces: List[PInterface] = Nil

  override def name = _name

  override def interfaces = _interfaces

  override def declaredConstants = _interfaceConstants.toMap

  override def register(implicit ctx: Context) {
    initialize(autoload = false, ignoreErrors = true)
  }

  override def exec(implicit ctx: Context) = {
    if (!_initialized)
      initialize(autoload = true, ignoreErrors = false)
    SuccessExecResult
  }

  override lazy val methods: Map[String, PMethod] = {
    val result = mutable.LinkedHashMap.empty[String, PMethod]

    _interfaces.foreach(result ++= _.methods)
    decls.foreach {
      case method: ClassMethodDecl =>
        method.declaringInterface = Some(this)
        result -= method.name.toLowerCase
        result += method.name.toLowerCase -> method
      case _ =>
    }
    result.toMap
  }

  private def initialize(autoload: Boolean, ignoreErrors: Boolean)(implicit ctx: Context) {
    _name = declaredName.absolutePrefix
    if (ctx.global.findInterfaceOrClass(name, autoload = false).isDefined)
      throw new FatalErrorJbjException("Cannot redeclare class %s".format(name))
    else {
      val implementedInterfaces = mutable.Set.empty[String]
      _interfaces = superInterfaces.map {
        interfaceName =>
          val effectiveName = interfaceName.absolute
          if (implementedInterfaces.contains(effectiveName.toString.toLowerCase)) {
            throw new FatalErrorJbjException("Class %s cannot implement previously implemented interface %s".format(name.toString, interfaceName.toString))
          }
          implementedInterfaces += effectiveName.toString.toLowerCase
          ctx.global.findInterfaceOrClass(effectiveName, autoload) match {
            case Some(Left(interface)) => interface
            case Some(Right(_)) =>
              if (ignoreErrors)
                return
              else
                throw new FatalErrorJbjException("%s cannot implement %s - it is not an interface".format(name.toString,
                  interfaceName.toString))
            case None =>
              if (ignoreErrors)
                return
              else
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

  }
}
