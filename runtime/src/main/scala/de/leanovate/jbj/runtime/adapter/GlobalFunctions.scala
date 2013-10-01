/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import language.experimental.macros
import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.{context, NamespaceName}
import de.leanovate.jbj.runtime.value.{PVal, PVar, PAny}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.runtime.types.{PParamDef, PParam, PFunction}

object GlobalFunctions {
  def functions(inst: Any): Seq[PFunction] = macro functions_impl

  def functions_impl(c: Context)(inst: c.Expr[Any]): c.Expr[Seq[PFunction]] = {
    import c.universe._
    val converterHelper = new ConverterHelper[c.type](c)
    val pVarClass = typeOf[PVar].typeSymbol

    def function_impl(member: MethodSymbol): c.Expr[PFunction] = {
      val args = member.annotations.find(_.tpe == typeOf[GlobalFunction]).get.scalaArgs
      val parameterMode: ParameterMode.Type = c.eval(c.Expr[ParameterMode.Type](c.resetAllAttrs(args(0))))
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
          val notEnoughHandler = parameterMode match {
            case ParameterMode.EXACTLY_WARN =>
              notEnoughWarn(member.name.encoded, expected).tree
            case ParameterMode.RELAX_ERROR =>
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
      val tooManyHandler: List[Tree] = parameterMode match {
        case ParameterMode.EXACTLY_WARN =>
          tooManyWarn(remain, member.name.encoded, expected).tree :: Nil
        case _ =>
          Nil
      }
      val functionName = c.literal(member.name.encoded)
      val impl = c.Expr(Block(parameters.map(_._2) ++ tooManyHandler, Apply(Select(Ident(inst.actualType.termSymbol), member.name), parameters.map(_._1))))
      val resultConverter = converterHelper.converterForType(member.returnType)
      val paramDefs = c.Expr[Seq[PParamDef]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")),
        memberParams.map {
          parameter =>
            val name = c.literal(parameter.name.toString)
            val byRef = parameter.typeSignature match {
              case TypeRef(_, sym, _) if sym == pVarClass =>
                c.literal(true)
              case _ =>
                c.literal(false)
            }

            reify {
              AdaptedParamDef(name.splice, false, byRef.splice, None)
            }.tree
        }
      ))

      reify {
        new PFunction {
          def name = NamespaceName.apply(functionName.splice)

          def parameters = paramDefs.splice

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
        VarargParameterAdapter(converterHelper.converterForType(a.head).splice)
      } -> true
      case TypeRef(_, sym, a) if sym == definitions.OptionClass => reify {
        OptionParameterAdapter(converterHelper.converterForType(a.head).splice)
      } -> false
      case TypeRef(_, sym, _) if sym == pVarClass => reify {
        RefParameterAdapter
      } -> false
      case t => reify {
        DefaultParamterAdapter(converterHelper.converterForType(t).splice)
      } -> false
    }

    def notEnoughWarn(name: String, expected: Int): c.Expr[Any] = {
      val msg = c.literal("%s() expects exactly %d parameter, %%d given".format(name, expected))
      val given = c.Expr(Select(Ident(newTermName("parameters")), newTermName("size")))
      val ctx = c.Expr[context.Context](Ident(newTermName("callerCtx")))
      val ret = c.Expr[Unit](Return(Select(Ident(c.mirror.staticModule("de.leanovate.jbj.runtime.value.BooleanVal")), newTermName("FALSE"))))
      reify {
        ctx.splice.log.warn(msg.splice.format(given.splice))
        ret.splice
      }
    }

    def tooManyWarn(remain: Tree, name: String, expected: Int): c.Expr[Any] = {
      val msg = c.literal("%s() expects exactly %d parameter, %%d given".format(name, expected))
      val given = c.Expr(Select(Ident(newTermName("parameters")), newTermName("size")))
      val ctx = c.Expr[context.Context](Ident(newTermName("callerCtx")))
      val remainExpr = c.Expr[List[PParam]](remain)
      val ret = c.Expr[Unit](Return(Select(Ident(c.mirror.staticModule("de.leanovate.jbj.runtime.value.BooleanVal")), newTermName("FALSE"))))
      reify {
        if (!remainExpr.splice.isEmpty) {
          ctx.splice.log.warn(msg.splice.format(given.splice))
          ret.splice
        }
      }
    }

    def notEnoughThrowFatal(name: String, expected: Int): c.Expr[Any] = {
      val msg = c.literal("%s() expects at least %d parameter, %%d given".format(name, expected))
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
    //    c.echo(c.enclosingPosition, "Add global functions: " + exprs.mkString("\n"))
    c.Expr[Seq[PFunction]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")),
      exprs))
  }
}
