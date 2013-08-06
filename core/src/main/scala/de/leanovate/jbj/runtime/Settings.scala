package de.leanovate.jbj.runtime

class Settings extends Cloneable {

  import Settings._

  var errorReporting: Int = E_ALL & ~E_NOTICE & ~E_STRICT

  override def clone: Settings = super.clone().asInstanceOf[Settings]
}

object Settings {
  val E_ERROR = 1
  val E_WARNING = 2
  val E_PARSE = 4
  val E_NOTICE = 8
  val E_CORE_ERROR = 16
  val E_CORE_WARNING = 32
  val E_COMPILE_ERROR = 64
  val E_COMPILE_WARNING = 128
  val E_USER_ERROR = 256
  val E_USER_WARNING = 512
  val E_USER_NOTICE = 1024
  val E_STRICT = 2048
  val E_RECOVERABLE_ERROR = 4096
  val E_DEPRECATED = 8192
  val E_USER_DEPRECATED = 16384
  val E_ALL = 32767
}