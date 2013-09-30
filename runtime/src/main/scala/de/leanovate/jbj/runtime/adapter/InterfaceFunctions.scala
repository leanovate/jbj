package de.leanovate.jbj.runtime.adapter

import language.experimental.macros
import de.leanovate.jbj.runtime.types.{ClassTypeHint, TypeHint, PParamDef, PInterface}
import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.annotations.InstanceFunction

object InterfaceFunctions {
  def methods[T](interface: PInterface): Map[String, PInterfaceMethod] = macro methodsImpl[T]

  def methodsImpl[T: c.WeakTypeTag](c: Context)(interface: c.Expr[PInterface]): c.Expr[Map[String, PInterfaceMethod]] = {
    import c.universe._
    val interfaceType = weakTypeOf[T]

    def typeHint(parameter: Symbol): c.Expr[Option[TypeHint]] = parameter.typeSignature match {
      case TypeRef(_, sym, _) if sym.companionSymbol.typeSignature <:< typeOf[PInterface] =>
        val interface = c.Expr[PInterface](Ident(sym.companionSymbol))
        reify {
          Some(ClassTypeHint(interface.splice.name))
        }
      case _ =>
        reify {
          Option.empty[TypeHint]
        }
    }

    val methods = interfaceType.members.filter {
      member =>
        member.isMethod && member.annotations.exists(_.tpe == typeOf[InstanceFunction])
    }.map {
      method =>
        val args = method.annotations.find(_.tpe == typeOf[InstanceFunction]).get.scalaArgs
        val actualName = c.eval(c.Expr[Option[String]](c.resetAllAttrs(args(0)))).getOrElse(method.name.encoded)
        val name = c.literal(actualName)
        val parameters = method.asMethod.paramss.flatten.map {
          param =>
            val paramName = c.literal(param.name.encoded)
            val th = typeHint(param)
            reify {
              SimpleParamDef(paramName.splice, hasDefault = false, byRef = false, th.splice)
            }.tree
        }.toList
        val parametersSeq = c.Expr[Seq[PParamDef]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")), parameters))

        reify {
          PInterfaceMethod(interface.splice, name.splice, parametersSeq.splice)
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
