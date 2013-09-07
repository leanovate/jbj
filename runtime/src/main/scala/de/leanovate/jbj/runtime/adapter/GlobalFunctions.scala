package de.leanovate.jbj.runtime.adapter

import language.experimental.macros
import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.{context, PParam, NamespaceName, PFunction}
import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny, NullVal}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

trait GlobalFunctions {
}

object GlobalFunctions {
  def functions(inst: GlobalFunctions): Seq[PFunction] = macro functions_impl

  def functions_impl(c: Context)(inst: c.Expr[GlobalFunctions]): c.Expr[Seq[PFunction]] = {
    import c.universe._
    val pVarClass = typeOf[PVar].typeSymbol
    val pValClass = typeOf[PVal].typeSymbol
    val pAnyClass = typeOf[PAny].typeSymbol
    val paramClass = typeOf[PParam].typeSymbol

    def function_impl(member: MethodSymbol): c.Expr[PFunction] = {

      val adapter1 = Apply(Select(mapParameter(typeOf[String]).tree, newTermName("adapt")), List(Ident(newTermName("parameters"))))
      //
      val param1 = c.Expr(ValDef(Modifiers(), newTermName("param1"), TypeTree(), Apply(Select(adapter1, newTermName("getOrElse")), List(throwFatal(member.name.encoded).tree)))).tree
      val functionName = c.Expr[String](Literal(Constant(member.name.encoded)))
      val impl = c.Expr(Block(List(param1), Apply(Select(This(inst.actualType.typeSymbol), member.name), List())))
      val resultConverter = converterForType(member.returnType)

      reify {
        new PFunction {
          def name = NamespaceName.apply(functionName.splice)

          def call(parameters: List[PParam])(implicit callerCtx: context.Context) = {
            val result = impl.splice
            resultConverter.splice.toJbj(result)
          }
        }
      }
    }

    def mapParameter(_type: Type): c.Expr[ParameterAdapter[_]] = _type match {
      case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => reify {
        VarargParameterAdapter(converterForType(a.head).splice)
      }
      case TypeRef(_, sym, a) if sym == definitions.OptionClass => reify {
        OptionParameterAdapter(converterForType(a.head).splice)
      }
      case TypeRef(_, sym, _) if sym == pVarClass => reify {
        RefParameterAdapter
      }
      case t => reify {
        DefaultParamterAdapter(converterForType(t).splice)
      }
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


    def throwFatal(name: String): c.Expr[Unit] = {
      val msg = c.Expr[String](Literal(Constant("%s() expects at least %d parameter, %%d given".format(name, 1))))
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
