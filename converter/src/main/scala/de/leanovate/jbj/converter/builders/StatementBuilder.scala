/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.converter.builders

import scala.text.Document
import scala.text.Document._

object StatementBuilder {
  def inlineStmt(inlineText: String): Document =
    if (inlineText.isEmpty)
      empty
    else
      text("inline(") :: LiteralBuilder.build(inlineText) :: text(")")
}
