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
import de.leanovate.jbj.runtime.exception.{WarnWithResultJbjException, FatalErrorJbjException}
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
      val warnResult = args(1)
      val memberParams = member.paramss.headOption.getOrElse(Nil)
      var remain: Tree = Ident(newTermName("parameters"))
      var hasOptional = false
      val expected = memberParams.foldLeft(0) {
        case (count, parameter) =>
          val expect = expectedParameterCount(parameter.typeSignature)
          if (expect == 0)
            hasOptional = true
          count + expect
      }
      // with Option[], varargs...
      val expectedMax = memberParams.size

      val (notEnoughHandler, strict, conversionHandler) = parameterMode match {
        case ParameterMode.EXACTLY_WARN =>
          (notEnoughWarn(member.name.encoded, expected, hasOptional, warnResult), false, conversionIgnore)
        case ParameterMode.STRICT_WARN =>
          (notEnoughWarn(member.name.encoded, expected, hasOptional, warnResult), true, conversionWarn(member.name.encoded, warnResult))
        case ParameterMode.RELAX_ERROR =>
          (notEnoughThrowFatal(member.name.encoded, expected), false, conversionIgnore)
      }
      val parameters: List[(Tree, ValDef)] = memberParams.zipWithIndex.map {
        case (parameter, idx) =>
          val paramName = newTermName("param" + idx)
          val (adapter, stared) = mapParameter(idx, parameter.typeSignature, strict, notEnoughHandler, conversionHandler)
          val adapterCall = Apply(Select(adapter.tree, newTermName("adapt")), List(remain))
          remain = Select(Ident(paramName), newTermName("_2"))
          if (stared) {
            Typed(Select(Ident(paramName), newTermName("_1")), Ident(tpnme.WILDCARD_STAR)) ->
              ValDef(Modifiers(), paramName, TypeTree(), adapterCall)
          } else {
            Select(Ident(paramName), newTermName("_1")) ->
              ValDef(Modifiers(), paramName, TypeTree(), adapterCall)
          }
      }
      val tooManyHandler: List[Tree] = parameterMode match {
        case ParameterMode.EXACTLY_WARN =>
          tooManyWarn(Ident(newTermName("parameters")), member.name.encoded, expectedMax, hasOptional, warnResult).tree :: Nil
        case ParameterMode.STRICT_WARN =>
          tooManyWarn(Ident(newTermName("parameters")), member.name.encoded, expectedMax, hasOptional, warnResult).tree :: Nil
        case _ =>
          Nil
      }
      val functionName = c.literal(member.name.encoded)
      val impl = c.Expr(Block(tooManyHandler ++ parameters.map(_._2), Apply(Select(Ident(inst.actualType.termSymbol), member.name), parameters.map(_._1))))
      val resultConverter = converterHelper.converterForType(member.returnType)
      val paramDefs = c.Expr[Seq[PParamDef]](Apply(Select(Ident(newTermName("Seq")), newTermName("apply")),
        memberParams.map {
          parameter =>
            val name = c.literal(parameter.name.toString)
            val byRef = parameter.typeSignature match {
              case TypeRef(_, sym, _) if sym == pVarClass =>
                c.literalTrue
              case _ =>
                c.literalFalse
            }

            reify {
              AdaptedParamDef(name.splice, None, byRef.splice, None)
            }.tree
        }
      ))

      reify {
        new PFunction {
          def name = NamespaceName.apply(functionName.splice)

          def parameters = paramDefs.splice

          def doCall(parameters: List[PParam])(implicit callerCtx: context.Context): PAny = {
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

    def mapParameter(parameterIdx: Int, _type: Type, strict: Boolean, missingParameterHandler: c.Expr[(Int, de.leanovate.jbj.runtime.context.Context) => Unit], conversionErrorHandler: c.Expr[(String, String, Int) => Unit]): (c.Expr[ParameterAdapter[_]], Boolean) = {
      val parameterIdxExpr = c.literal(parameterIdx)
      val strictExpr = c.literal(strict)
      _type match {
        case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => reify {
          VarargParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(a.head).splice)
        } -> true
        case TypeRef(_, sym, a) if sym == definitions.OptionClass => reify {
          OptionParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(a.head).splice, strictExpr.splice, conversionErrorHandler.splice)
        } -> false
        case TypeRef(_, sym, _) if sym == pVarClass => reify {
          RefParameterAdapter(parameterIdxExpr.splice, missingParameterHandler.splice, conversionErrorHandler.splice)
        } -> false
        case t => reify {
          StdParamterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(t).splice, strictExpr.splice, missingParameterHandler.splice, conversionErrorHandler.splice)
        } -> false
      }
    }

    def tooManyWarn(parameters: Tree, name: String, expected: Int, hasOptional: Boolean, warnResult: c.universe.Tree): c.Expr[Any] = {
      val msg = c.literal("%s() expects %s %s, %%d given".format(name, if (hasOptional) "at most" else "exactly", plural(expected, "parameter")))
      val given = c.Expr(Select(Ident(newTermName("parameters")), newTermName("size")))
      val remainExpr = c.Expr[List[PParam]](parameters)
      val ret = c.Expr[PVal](warnResult)
      val expectedExpr = c.literal(expected)
      reify {
        if (remainExpr.splice.size > expectedExpr.splice) {
          throw new WarnWithResultJbjException(msg.splice.format(given.splice), ret.splice)
        }
      }
    }

    def notEnoughWarn(name: String, expected: Int, hasOptional: Boolean, warnResult: c.universe.Tree): c.Expr[(Int, de.leanovate.jbj.runtime.context.Context) => Unit] = {
      val nameExpr = c.literal(name)
      val expectedExpr = c.literal(expected)
      val ret = c.Expr[PVal](warnResult)
      if (hasOptional) {
        reify {
          ParameterAdapter.notEnoughWarn(nameExpr.splice, expectedExpr.splice, ret.splice)
        }
      } else {
        reify {
          ParameterAdapter.notEnoughExactlyWarn(nameExpr.splice, expectedExpr.splice, ret.splice)
        }
      }
    }

    def notEnoughThrowFatal(name: String, expected: Int): c.Expr[(Int, de.leanovate.jbj.runtime.context.Context) => Unit] = {
      val nameExpr = c.literal(name)
      val expectedExpr = c.literal(expected)
      reify {
        ParameterAdapter.notEnoughThrowFatal(nameExpr.splice, expectedExpr.splice)
      }
    }

    def conversionIgnore: c.Expr[(String, String, Int) => Unit] = {
      reify {
        ParameterAdapter.conversionErrorIgnore
      }
    }

    def conversionWarn(name: String, warnResult: c.universe.Tree): c.Expr[(String, String, Int) => Unit] = {
      val nameExpr = c.literal(name)
      val resultExpr = c.Expr[PVal](warnResult)
      reify {
        ParameterAdapter.conversionErrorWarn(nameExpr.splice, resultExpr.splice)
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

  def plural(num: Int, str: String) = {
    if (num == 0 || num > 1)
      s"$num ${str}s"
    else
      s"$num $str"
  }
}
