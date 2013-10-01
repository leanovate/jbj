package de.leanovate.jbj.runtime.adapter

import scala.reflect.macros.Context
import de.leanovate.jbj.runtime.value.{ObjectVal, PVal, PVar, PAny}
import de.leanovate.jbj.runtime.types.{PInterfaceAdapter, PInterface, PParam}

class ConverterHelper[C <: Context](val c: C) {
  import c.universe._

  val pVarClass = typeOf[PVar].typeSymbol
  val pValClass = typeOf[PVal].typeSymbol
  val pAnyClass = typeOf[PAny].typeSymbol
  val paramClass = typeOf[PParam].typeSymbol

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
      case t if t.typeSymbol == pVarClass =>
        reify {
          PVarConverter
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
      case TypeRef(_, sym, _) if sym.companionSymbol.typeSignature <:< typeOf[PInterfaceAdapter[_ <: ObjectVal]] =>
        val interfaceAdapter = c.Expr[PInterfaceAdapter[_ <: ObjectVal]](Ident(sym.companionSymbol))
        reify {
          PInterfaceConverter(interfaceAdapter.splice)
        }
    }
  }
}
