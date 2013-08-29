/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.context

sealed trait ConstantKey

case class CaseSensitiveConstantKey(name: String) extends ConstantKey {
}

case class CaseInsensitiveConstantKey(name: String) extends ConstantKey {
}