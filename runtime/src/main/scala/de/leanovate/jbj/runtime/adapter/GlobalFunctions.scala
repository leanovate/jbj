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
      val paramaterModeExpr = c.Expr[ParameterMode.Type](c.resetAllAttrs(args(0)))
      val parameterMode: ParameterMode.Type = c.eval(paramaterModeExpr)
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

      val strict = parameterMode match {
        case ParameterMode.STRICT_WARN => true
        case _ => false
      }
      val parameters: List[(Tree, ValDef)] = memberParams.zipWithIndex.map {
        case (parameter, idx) =>
          val paramName = newTermName("param" + idx)
          val (adapter, stared) = mapParameter(idx, parameter.typeSignature, strict)
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

      val expectedExpr = c.literal(expected)
      val hasOptionalExpr = c.literal(hasOptional)
      val warnResultExpr = c.Expr[PVal](warnResult)
      reify {
        new PFunction {
          val errorHandlers = ParameterAdapter.errorHandlers(functionName.splice, paramaterModeExpr.splice, expectedExpr.splice, hasOptionalExpr.splice, warnResultExpr.splice)

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

    def mapParameter(parameterIdx: Int, _type: Type, strict: Boolean): (c.Expr[ParameterAdapter[_]], Boolean) = {
      val parameterIdxExpr = c.literal(parameterIdx)
      val errorHandlersExpr = c.Expr[ParameterAdapter.ErrorHandlers](Ident(newTermName("errorHandlers")))
      val strictExpr = c.literal(strict)
      _type match {
        case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => reify {
          VarargParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(a.head).splice)
        } -> true
        case TypeRef(_, sym, a) if sym == definitions.OptionClass => reify {
          OptionParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(a.head).splice, strictExpr.splice, errorHandlersExpr.splice)
        } -> false
        case TypeRef(_, sym, _) if sym == pVarClass => reify {
          RefParameterAdapter(parameterIdxExpr.splice, errorHandlersExpr.splice)
        } -> false
        case t => reify {
          StdParamterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(t).splice, strictExpr.splice, errorHandlersExpr.splice)
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

  def main(args: Array[String]) {
    import scala.reflect.runtime.{universe => u}

    val expr = u.reify {
      val a = (1,2,3)
    }
    println(u.show(expr))
    println(u.showRaw(expr))
  }
}
