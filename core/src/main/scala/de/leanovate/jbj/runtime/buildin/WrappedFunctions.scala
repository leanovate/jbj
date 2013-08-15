package de.leanovate.jbj.runtime.buildin

import de.leanovate.jbj.runtime.{Context, PFunction}
import scala.reflect.runtime.universe._
import de.leanovate.jbj.runtime.adapter._
import de.leanovate.jbj.runtime.value.{ValueOrRef, Value}
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.adapter.VarargParameterAdapter
import de.leanovate.jbj.ast.{NamespaceName, NodePosition}

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
  val contextClass = typeOf[Context].typeSymbol
  val nodePositionClass = typeOf[NodePosition].typeSymbol
  val valueClass = typeOf[Value].typeSymbol
  val valueOrRefClass = typeOf[ValueOrRef].typeSymbol
  val exprClass = typeOf[de.leanovate.jbj.ast.Expr].typeSymbol

  def mapMethod(method: MethodSymbol, instance: InstanceMirror): PFunction = {
    val adapters = method.paramss.flatten.map(mapParameter).toSeq
    val resultConverter = converterForClass(method.returnType)

    WrappedReflectMethodFunction(new NamespaceName(relative = false, method.name.decoded),
      instance.reflectMethod(method), adapters, resultConverter)
  }

  def mapParameter(parameter: Symbol): ParameterAdapter[_] = parameter.typeSignature match {
    case TypeRef(_, sym, a) if sym == definitions.RepeatedParamClass => VarargParameterAdapter(converterForClass(a.head))
    case TypeRef(_, sym, a) if sym == definitions.OptionClass => OptionParameterAdapter(converterForClass(a.head))
    case TypeRef(_, sym, _) if sym == contextClass => ContextParameterAdapter
    case TypeRef(_, sym, _) if sym == nodePositionClass => PositionParameterAdapter
    case t => DefaultParamterAdapter(converterForClass(t))
  }

  def converterForClass(_type: Type): Converter[_, _ <: Value] = _type match {
    case t if t.typeSymbol == definitions.StringClass => StringConverter
    case t if t.typeSymbol == definitions.IntClass => IntConverter
    case t if t.typeSymbol == definitions.LongClass => LongConverter
    case t if t.typeSymbol == definitions.DoubleClass => DoubleConverter
    case t if t.typeSymbol == definitions.BooleanClass => BooleanConverter
    case t if t.typeSymbol == valueClass => ValueConverter
    case t if t.typeSymbol == valueOrRefClass => ValueOrRefConverter
    case t if t.typeSymbol == exprClass => ExprConverter
    case t if t.typeSymbol == definitions.UnitClass => UnitConverter
    case TypeRef(_, sym, a) if sym == definitions.ArrayClass && a.head.typeSymbol == definitions.ByteClass =>
      ByteArrayConverter
  }
}