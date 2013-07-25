package de.leanovate.jbj.runtime.context

sealed trait ConstantKey

case class CaseSensitiveConstantKey(name: String) extends ConstantKey {
}

case class CaseInsensitiveConstantKey(name: String) extends ConstantKey {
}