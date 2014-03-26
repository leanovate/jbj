package de.leanovate.jbj.runtime.adapter

import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.types.{PInterfaceAdapter, PParam}
import java.nio.channels.SeekableByteChannel

class ConverterHelper[C <: Context](val c: C) {

  import c.universe._

  val pVarClass = typeOf[PVar].typeSymbol
  val pValClass = typeOf[PVal].typeSymbol
  val pAnyClass = typeOf[PAny].typeSymbol
  val seekableByteChannelClass = typeOf[SeekableByteChannel].typeSymbol
  val paramClass = typeOf[PParam].typeSymbol

  def converterForType(_type: Type): c.Expr[Converter[Any, _ <: PAny]] = {
    _type match {
      case t if t.typeSymbol == definitions.StringClass =>
        reify {
          StringConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == definitions.IntClass =>
        reify {
          IntConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == definitions.LongClass =>
        reify {
          LongConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == definitions.DoubleClass =>
        reify {
          DoubleConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == definitions.BooleanClass =>
        reify {
          BooleanConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == pValClass =>
        reify {
          PValConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == pVarClass =>
        reify {
          PVarConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == pAnyClass =>
        reify {
          PAnyConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == seekableByteChannelClass =>
        reify {
          SeekableByteChannelConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == paramClass =>
        reify {
          ParamConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case t if t.typeSymbol == definitions.UnitClass =>
        reify {
          UnitConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case TypeRef(_, sym, a) if sym == definitions.ArrayClass && a.head.typeSymbol == definitions.ByteClass =>
        reify {
          ByteArrayConverter
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
      case TypeRef(_, sym, _) if sym.companionSymbol.typeSignature <:< typeOf[PInterfaceAdapter[_ <: ObjectVal]] =>
        val interfaceAdapter = c.Expr[PInterfaceAdapter[_ <: ObjectVal]](Ident(sym.companionSymbol))
        reify {
          PInterfaceConverter(interfaceAdapter.splice)
        }.asInstanceOf[c.Expr[Converter[Any, _ <: PAny]]]
    }
  }
}
