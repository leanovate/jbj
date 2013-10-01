package de.leanovate.jbj.runtime.adapter

import language.experimental.macros
import de.leanovate.jbj.runtime.types.{ClassTypeHint, TypeHint, PParamDef, PInterface}
import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.annotations.InstanceFunction
import de.leanovate.jbj.runtime.value.ObjectVal

object InterfaceFunctions {
  def methods[T]: Map[String, PInterfaceMethod] = macro methodsImpl[T]

  def methodsImpl[T: c.WeakTypeTag](c: Context): c.Expr[Map[String, PInterfaceMethod]] = {
    import c.universe._
    val interfaceType = weakTypeOf[T]
    val interface = c.Expr[PInterface](This(tpnme.EMPTY))

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
              AdaptedParamDef(paramName.splice, hasDefault = false, byRef = false, th.splice)
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

  def cast[T](obj: ObjectVal): T = macro castImpl[T]

  def castImpl[T: c.WeakTypeTag](c: Context)(obj: c.Expr[ObjectVal]): c.Expr[T] = {
    import c.universe._

    val interfaceType = weakTypeOf[T]
    val constructor = DefDef(Modifiers(), nme.CONSTRUCTOR, List(), List(List()), TypeTree(),
      Block(List(Apply(Select(Super(This(tpnme.EMPTY), tpnme.EMPTY), nme.CONSTRUCTOR), List())), Literal(Constant())))
    val delegate = DefDef(Modifiers(), newTermName("delegate"), List(), List(), TypeTree(), obj.tree)
    val methods = interfaceType.members.filter {
      member =>
        member.isMethod && member.annotations.exists(_.tpe == typeOf[InstanceFunction])
    }.map {
      method =>
        val parameters = method.asMethod.paramss.map {
          params =>
            params.map {
              case param if param.isImplicit =>
                ValDef(Modifiers(Flag.PARAM | Flag.IMPLICIT), param.name.toTermName, Ident(param.typeSignature.typeSymbol), EmptyTree)
              case param =>
                ValDef(Modifiers(Flag.PARAM), param.name.toTermName, Ident(param.typeSignature.typeSymbol), EmptyTree)
            }
        }
        DefDef(Modifiers(), method.name, List(), parameters, TypeTree(), Select(Ident(newTermName("Predef")), newTermName("$qmark$qmark$qmark")))
    }.toList
    c.Expr[T](Block(
      List(
        ClassDef(Modifiers(Flag.FINAL), newTypeName("$anon"), List(),
          Template(List(Ident(interfaceType.typeSymbol)), emptyValDef, constructor :: delegate :: methods))
      ),
      Apply(Select(New(Ident(newTypeName("$anon"))), nme.CONSTRUCTOR), List())))
  }
}
