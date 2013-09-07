package de.leanovate.jbj.runtime.adapter

import language.experimental.macros
import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.annotations.{ErrorAction, GlobalFunction}
import de.leanovate.jbj.runtime.{context, PParam, NamespaceName, PFunction}
import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

object GlobalFunctions {
  def functions(inst: Any): Seq[PFunction] = macro functions_impl

  def functions_impl(c: Context)(inst: c.Expr[Any]): c.Expr[Seq[PFunction]] = {
    import c.universe._
    val pVarClass = typeOf[PVar].typeSymbol
    val pValClass = typeOf[PVal].typeSymbol
    val pAnyClass = typeOf[PAny].typeSymbol
    val paramClass = typeOf[PParam].typeSymbol

    def function_impl(member: MethodSymbol): c.Expr[PFunction] = {
      val args = member.annotations.find(_.tpe == typeOf[GlobalFunction]).get.scalaArgs
      val notEnoughParamAction: ErrorAction.Type = c.eval(c.Expr[ErrorAction.Type](c.resetAllAttrs(args(0))))

      val memberParams = member.paramss.headOption.getOrElse(Nil)
      var remain: Tree = Ident(newTermName("parameters"))
      val expected = memberParams.foldLeft(0) {
        case (count, parameter) => count + expectedParameterCount(parameter.typeSignature)
      }
      val parameters = memberParams.zipWithIndex.map {
        case (parameter, idx) =>
          val paramName = newTermName("param" + idx)
          val (adapter, stared) = mapParameter(parameter.typeSignature)
          val adapterCall = Apply(Select(adapter.tree, newTermName("adapt")), List(remain))
          remain = Select(Ident(paramName), newTermName("_2"))
          val notEnoughHandler = notEnoughParamAction match {
            case ErrorAction.IGNORE =>
              notEnoughIgnore.tree
            case ErrorAction.WARN =>
              notEnoughWarn(member.name.encoded, expected).tree
            case ErrorAction.FATAL =>
              notEnoughThrowFatal(member.name.encoded, expected).tree
          }
          if (stared) {
            Typed(Select(Ident(paramName), newTermName("_1")), Ident(tpnme.WILDCARD_STAR)) ->
              ValDef(Modifiers(), paramName, TypeTree(), Apply(Select(adapterCall, newTermName("getOrElse")), List(notEnoughHandler)))
          } else {
            Select(Ident(paramName), newTermName("_1")) ->
              ValDef(Modifiers(), paramName, TypeTree(), Apply(Select(adapterCall, newTermName("getOrElse")), List(notEnoughHandler)))
          }
      }
      val functionName = c.Expr[String](Literal(Constant(member.name.encoded)))
      val impl = c.Expr(Block(parameters.map(_._2), Apply(Select(This(inst.actualType.typeSymbol), member.name), parameters.map(_._1))))
      val resultConverter = converterForType(member.returnType)

      reify {
        new PFunction {
          def name = NamespaceName.apply(functionName.splice)

          def call(parameters: List[PParam])(implicit callerCtx: context.Context): PAny = {
            val result = impl.splice
            resultConverter.splice.toJbj(result)
          }
        }
      }
    }

    def expectedParameterCount(_type: Type) = _type match {
      case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => 0
      case TypeRef(_, sym, a) if sym == definitions.OptionClass => 0
      case TypeRef(_, sym, _) if sym == pVarClass => 1
      case t => 1
    }
    def mapParameter(_type: Type): (c.Expr[ParameterAdapter[_]], Boolean) = _type match {
      case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => reify {
        VarargParameterAdapter(converterForType(a.head).splice)
      } -> true
      case TypeRef(_, sym, a) if sym == definitions.OptionClass => reify {
        OptionParameterAdapter(converterForType(a.head).splice)
      } -> false
      case TypeRef(_, sym, _) if sym == pVarClass => reify {
        RefParameterAdapter
      } -> false
      case t => reify {
        DefaultParamterAdapter(converterForType(t).splice)
      } -> false
    }

    def converterForType(_type: Type): c.Expr[Converter[_, _ <: PAny]] = {
      _type match {
        case t if t.typeSymbol == definitions.StringClass =>
          reify {
            StringConverter
          }
        case t if t.typeSymbol == definitions.IntClass =>
          reify {
            IntConverter
          }
        case t if t.typeSymbol == definitions.LongClass =>
          reify {
            LongConverter
          }
        case t if t.typeSymbol == definitions.DoubleClass =>
          reify {
            DoubleConverter
          }
        case t if t.typeSymbol == definitions.BooleanClass =>
          reify {
            BooleanConverter
          }
        case t if t.typeSymbol == pValClass =>
          reify {
            PValConverter
          }
        case t if t.typeSymbol == pAnyClass =>
          reify {
            PAnyConverter
          }
        case t if t.typeSymbol == paramClass =>
          reify {
            ParamConverter
          }
        case t if t.typeSymbol == definitions.UnitClass =>
          reify {
            UnitConverter
          }
        case TypeRef(_, sym, a) if sym == definitions.ArrayClass && a.head.typeSymbol == definitions.ByteClass =>
          reify {
            ByteArrayConverter
          }
      }
    }

    def notEnoughIgnore: c.Expr[Any] = {
      reify {
        (null, Nil)
      }
    }

    def notEnoughWarn(name: String, expected: Int): c.Expr[Any] = {
      val msg = c.Expr[String](Literal(Constant("%s() expects at least %d parameter, %%d given".format(name, expected))))
      val ctx = c.Expr[context.Context](Ident(newTermName("callerCtx")))
      val ret = c.Expr[Unit](Return(Select(Ident(c.mirror.staticModule("de.leanovate.jbj.runtime.value.BooleanVal")), newTermName("FALSE"))))
      reify {
        ctx.splice.log.warn(msg.splice)
        ret.splice
      }
    }

    def notEnoughThrowFatal(name: String, expected: Int): c.Expr[Any] = {
      val msg = c.Expr[String](Literal(Constant("%s() expects at least %d parameter, %%d given".format(name, expected))))
      val given = c.Expr(Select(Ident(newTermName("parameters")), newTermName("size")))
      val ctx = c.Expr(Ident(newTermName("callerCtx")))
      reify {
        throw new FatalErrorJbjException(msg.splice.format(given.splice))(ctx.splice)
      }
    }

    val exprs = inst.actualType.members.filter {
      member =>
        member.isMethod && member.annotations.exists(_.tpe == typeOf[GlobalFunction])
    }.map {
      member =>
        function_impl(member.asMethod).tree
    }.toList
    c.Expr[Seq[PFunction]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")),
      exprs))
  }
}
