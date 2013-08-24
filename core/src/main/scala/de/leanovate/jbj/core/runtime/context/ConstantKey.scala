package de.leanovate.jbj.core.runtime.context

sealed trait ConstantKey

case class CaseSensitiveConstantKey(name: String) extends ConstantKey {
}

case class CaseInsensitiveConstantKey(name: String) extends ConstantKey {
}