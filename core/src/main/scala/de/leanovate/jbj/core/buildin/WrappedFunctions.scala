/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.buildin

import de.leanovate.jbj.runtime.{NamespaceName, PParam, PFunction}
import scala.reflect.runtime.universe._
import de.leanovate.jbj.runtime.adapter._
import de.leanovate.jbj.runtime.value.{PVar, PAny, PVal}
import de.leanovate.jbj.runtime.annotations.{WanrExactly, GlobalFunction}
import de.leanovate.jbj.runtime.adapter.VarargParameterAdapter
import de.leanovate.jbj.runtime.context.Context

trait WrappedFunctions {

  import WrappedFunctions._

  lazy val functions: Seq[PFunction] = {
    val runtime = runtimeMirror(getClass.getClassLoader)
    val instance = runtime.reflect(this)

    instance.symbol.toType.members.filter {
      member => member.isMethod && member.annotations.exists(_.tpe.typeSymbol == globalFunctionClass)
    }.map {
      member =>
        mapMethod(member.asMethod, instance)
    }.toSeq
  }
}

object WrappedFunctions {
  val globalFunctionClass = typeOf[GlobalFunction].typeSymbol
  val warnExactlyClass = typeOf[WanrExactly].typeSymbol
  val contextClass = typeOf[Context].typeSymbol
  val pVarClass = typeOf[PVar].typeSymbol
  val pValClass = typeOf[PVal].typeSymbol
  val pAnyClass = typeOf[PAny].typeSymbol
  val paramClass = typeOf[PParam].typeSymbol

  def mapMethod(method: MethodSymbol, instance: InstanceMirror): PFunction = {
    val adapters = method.paramss.flatten.map(mapParameter).toSeq
    val resultConverter = converterForClass(method.returnType)
    val warnExactly = method.annotations.exists(_.tpe.typeSymbol == warnExactlyClass)

    WrappedReflectMethodFunction(new NamespaceName(relative = false, method.name.decoded),
      instance.reflectMethod(method), adapters, resultConverter, warnExactly)
  }

  def mapParameter(parameter: Symbol): ParameterAdapter[_] = parameter.typeSignature match {
    case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => VarargParameterAdapter(converterForClass(a.head))
    case TypeRef(_, sym, a) if sym == definitions.OptionClass => OptionParameterAdapter(converterForClass(a.head))
    case TypeRef(_, sym, _) if sym == contextClass => ContextParameterAdapter
    case TypeRef(_, sym, _) if sym == pVarClass => RefParameterAdapter
    case t => DefaultParamterAdapter(converterForClass(t))
  }

  def converterForClass(_type: Type): Converter[_, _ <: PAny] = _type match {
    case t if t.typeSymbol == definitions.StringClass => StringConverter
    case t if t.typeSymbol == definitions.IntClass => IntConverter
    case t if t.typeSymbol == definitions.LongClass => LongConverter
    case t if t.typeSymbol == definitions.DoubleClass => DoubleConverter
    case t if t.typeSymbol == definitions.BooleanClass => BooleanConverter
    case t if t.typeSymbol == pValClass => PValConverter
    case t if t.typeSymbol == pAnyClass => PAnyConverter
    case t if t.typeSymbol == paramClass => ParamConverter
    case t if t.typeSymbol == definitions.UnitClass => UnitConverter
    case TypeRef(_, sym, a) if sym == definitions.ArrayClass && a.head.typeSymbol == definitions.ByteClass =>
      ByteArrayConverter
  }
}