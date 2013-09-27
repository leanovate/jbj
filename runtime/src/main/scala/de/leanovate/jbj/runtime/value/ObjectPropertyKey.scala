/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.value

import de.leanovate.jbj.runtime.context.Context

object ObjectPropertyKey {

  sealed trait Key {
    def name: String
  }

  case class IntKey(key: Long) extends Key {
    override def name = key.toString
  }

  case class PublicKey(key: String) extends Key {
    override def name = key
  }

  case class ProtectedKey(key: String) extends Key {
    override def name = key
  }

  case class PrivateKey(key: String, className: String) extends Key {
    override def name = key
  }

  object PublicKeyFilter extends KeyFilter[Key] {
    def accept(key: Key) = key match {
      case IntKey(_) => true
      case PublicKey(_) => true
      case _ => false
    }

    def mapKey(key: Key)(implicit ctx: Context) = key match {
      case IntKey(k) => IntegerVal(k)
      case PublicKey(k) => StringVal(k)
      case _ => throw new RuntimeException("Invalid key")
    }
  }

  case class PrivateKeyFilter(className: String) extends KeyFilter[Key] {
    def accept(key: Key) = key match {
      case IntKey(_) => true
      case PublicKey(_) => true
      case ProtectedKey(_) => true
      case PrivateKey(_, name) if name == className => true
      case _ => false
    }

    def mapKey(key: Key)(implicit ctx: Context) = key match {
      case IntKey(k) => IntegerVal(k)
      case PublicKey(k) => StringVal(k)
      case ProtectedKey(k) => StringVal(k)
      case PrivateKey(k, _) => StringVal(k)
      case _ => throw new RuntimeException("Invalid key")
    }
  }

}