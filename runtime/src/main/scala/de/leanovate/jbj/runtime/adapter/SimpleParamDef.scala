package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.types.{TypeHint, PParamDef}

case class SimpleParamDef(name: String, hasDefault: Boolean, byRef: Boolean, typeHint: Option[TypeHint]) extends PParamDef {

}
