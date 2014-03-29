/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.pcre

import de.leanovate.jbj.runtime.JbjExtension
import de.leanovate.jbj.runtime.types.PFunction
import de.leanovate.jbj.runtime.value.{StringVal, IntegerVal, PVal}
import PcreConstants._

object PcreExtension extends JbjExtension {
  val name = "pcre"

  override def constants: Seq[(String, PVal)] = Seq(
    "PREG_PATTERN_ORDER" -> IntegerVal(PREG_PATTERN_ORDER),
    "PREG_SET_ORDER" -> IntegerVal(PREG_SET_ORDER),
    "PREG_OFFSET_CAPTURE" -> IntegerVal(PREG_OFFSET_CAPTURE),
    "PREG_SPLIT_NO_EMPTY" -> IntegerVal(PREG_SPLIT_NO_EMPTY),
    "PREG_SPLIT_DELIM_CAPTURE" -> IntegerVal(PREG_SPLIT_DELIM_CAPTURE),
    "PREG_SPLIT_OFFSET_CAPTURE" -> IntegerVal(PREG_SPLIT_OFFSET_CAPTURE),
    "PREG_NO_ERROR" -> IntegerVal(PREG_NO_ERROR),
    "PREG_INTERNAL_ERROR" -> IntegerVal(1),
    "PREG_BACKTRACK_LIMIT_ERROR" -> IntegerVal(2),
    "PREG_RECURSION_LIMIT_ERROR" -> IntegerVal(3),
    "PREG_BAD_UTF8_ERROR" -> IntegerVal(4),
    "PREG_BAD_UTF8_OFFSET_ERROR" -> IntegerVal(5),
    "PREG_GREP_INVERT" -> IntegerVal(1),
    "PCRE_VERSION" -> new StringVal("8.32 2012-11-30".getBytes("UTF-8"))
  )

  override def functions: Seq[PFunction] = de.leanovate.jbj.pcre.functions.pcreFunctions
}
