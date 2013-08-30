/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.stmt.decl

import de.leanovate.jbj.core.ast.NamespaceName

sealed trait TypeHint

object ArrayTypeHint extends TypeHint

object CallableTypeHint extends TypeHint

case class ClassTypeHint(className: NamespaceName) extends TypeHint
