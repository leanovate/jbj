/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.adapter

import de.leanovate.jbj.runtime.types.{TypeHint, PParamDef}

case class AdaptedParamDef(name: String, hasDefault: Boolean, byRef: Boolean, typeHint: Option[TypeHint]) extends PParamDef {
}
