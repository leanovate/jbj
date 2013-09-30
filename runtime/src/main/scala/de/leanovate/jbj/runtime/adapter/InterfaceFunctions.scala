package de.leanovate.jbj.runtime.adapter

import language.experimental.macros
import de.leanovate.jbj.runtime.types.{PInterface, PInterfaceMethod}
import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.annotations.InstanceFunction

object InterfaceFunctions {
  def methods[T](interface: PInterface): Map[String, PInterfaceMethod] = macro methodsImpl[T]

  def methodsImpl[T: c.WeakTypeTag](c: Context)(interface: c.Expr[PInterface]): c.Expr[Map[String, PInterfaceMethod]] = {
    import c.universe._
    val interfaceType = weakTypeOf[T]

    val methods = interfaceType.members.filter {
      member =>
        member.isMethod && member.annotations.exists(_.tpe == typeOf[InstanceFunction])
    }.map {
      method =>
        val args = method.annotations.find(_.tpe == typeOf[InstanceFunction]).get.scalaArgs
        val actualName = c.eval(c.Expr[Option[String]](c.resetAllAttrs(args(0)))).getOrElse(method.name.encoded)
        val name = c.literal(actualName)
        reify {
          PInterfaceMethod(interface.splice, name.splice)
        }.tree
    }.toList

    val methodSeq = c.Expr[Seq[PInterfaceMethod]](
      Apply(
        TypeApply(
          Select(Ident(newTermName("Seq")), newTermName("apply")),
          List(Ident(typeOf[PInterfaceMethod].typeSymbol))),
        methods))

    reify {
      methodSeq.splice.map {
        method =>
          method.name.toLowerCase -> method
      }.toMap
    }
  }
}
