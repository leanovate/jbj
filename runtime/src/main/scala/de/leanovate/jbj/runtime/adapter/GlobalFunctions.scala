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
  def generatePFunctions[T]: Seq[PFunction] = macro generatePFunctions_impl[T]

  def generatePFunctions_impl[T : c.WeakTypeTag](c: Context): c.Expr[Seq[PFunction]] = {
    import c.universe._
    val converterHelper = new ConverterHelper[c.type](c)
    val pVarClass = typeOf[PVar].typeSymbol

    val companioned = weakTypeOf[T].typeSymbol
    val companionSymbol = companioned.companionSymbol

    def function_impl(member: MethodSymbol): c.Expr[PFunction] = {
      val args = member.annotations.find(_.tpe == typeOf[GlobalFunction]).get.scalaArgs
      val paramaterModeExpr = c.Expr[ParameterMode.Type](c.resetAllAttrs(args(0)))
      val parameterMode: ParameterMode.Type = c.eval(paramaterModeExpr)
      val warnResult = args(1)
      val memberParams = member.paramss.headOption.getOrElse(Nil)
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
      val parameters: List[(c.Expr[_], Tree, ValDef)] = memberParams.zipWithIndex.map {
        case (parameter, idx) =>
          val paramName = newTermName(s"param${idx + 1}")
          val (adapter, stared) = mapParameter(idx, parameter.typeSignature, strict)
          val adapterRef = Select(Ident(newTermName("adapters")), newTermName(s"_${idx + 1}"))
          val adapterCall = Apply(Select(adapterRef, newTermName("adapt")), List(Ident(newTermName("parametersIt"))))

          if (stared) {
            (adapter, Typed(Ident(paramName), Ident(tpnme.WILDCARD_STAR)),
              ValDef(Modifiers(), paramName, TypeTree(), adapterCall))
          } else {
            (adapter, Ident(paramName),
              ValDef(Modifiers(), paramName, TypeTree(), adapterCall))
          }
      }
      val tooManyHandler = tooManyWarn(Ident(newTermName("parameters")), expectedMax).tree
      val functionName = c.literal(member.name.encoded)
      val impl = c.Expr[Any](Block(tooManyHandler :: parameters.map(_._3), Apply(Select(Ident(companionSymbol), member.name.toTermName), parameters.map(_._2))))
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
      val expectedMaxExpr = c.literal(expectedMax)
      val hasOptionalExpr = c.literal(hasOptional)
      val warnResultExpr = c.Expr[PVal](warnResult)
      val adaptersExpr = makeTuple(parameters.map(_._1))
      reify {
        new PFunction {
          val errorHandlers = ParameterAdapter.errorHandlers(functionName.splice, paramaterModeExpr.splice, expectedExpr.splice, expectedMaxExpr.splice, hasOptionalExpr.splice, warnResultExpr.splice)

          val adapters = adaptersExpr.splice

          def name = NamespaceName.apply(functionName.splice)

          def parameters = paramDefs.splice

          def doCall(parameters: Seq[PParam])(implicit callerCtx: context.Context): PAny = {
            val parametersIt = parameters.iterator
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
      _type match {
        case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => reify {
          VarargParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(a.head).splice)
        } -> true
        case TypeRef(_, sym, a) if sym == definitions.OptionClass && strict => reify {
          StrictOptionParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(a.head).splice, errorHandlersExpr.splice)
        } -> false
        case TypeRef(_, sym, a) if sym == definitions.OptionClass => reify {
          RelaxedOptionParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(a.head).splice)
        } -> false
        case TypeRef(_, sym, _) if sym == pVarClass => reify {
          RefParameterAdapter(parameterIdxExpr.splice, errorHandlersExpr.splice)
        } -> false
        case t if strict => reify {
          StrictParameterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(t).splice,  errorHandlersExpr.splice)
        } -> false
        case t => reify {
          RelaxedParamterAdapter(parameterIdxExpr.splice, converterHelper.converterForType(t).splice, errorHandlersExpr.splice)
        } -> false
      }
    }

    def tooManyWarn(parameters: Tree, expected: Int): c.Expr[Any] = {
      val remainExpr = c.Expr[List[PParam]](parameters)
      val expectedExpr = c.literal(expected)
      val errorHandlersExpr = c.Expr[ParameterAdapter.ErrorHandlers](Ident(newTermName("errorHandlers")))
      reify {
        if (remainExpr.splice.size > expectedExpr.splice) {
          errorHandlersExpr.splice.tooManyParameters(remainExpr.splice.size)
        }
      }
    }

    def makeTuple(elements: Seq[c.Expr[_]]) = {
      if (elements.isEmpty)
        c.Expr(Ident(newTermName("Unit")))
      else
        c.Expr(Apply(Select(Ident(newTermName(s"Tuple${elements.size}")), newTermName("apply")), elements.map(_.tree).toList))
    }

    val exprs = companioned.typeSignature.members.filter {
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
