package de.leanovate.jbj.runtime.value

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

}