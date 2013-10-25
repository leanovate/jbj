/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.annotations

import scala.annotation.StaticAnnotation
import de.leanovate.jbj.runtime.value.{BooleanVal, PVal}

case class GlobalFunction(parameterMode: ParameterMode.Type = ParameterMode.RELAX_ERROR,
                          warnResult: PVal = BooleanVal.FALSE) extends StaticAnnotation {

}
