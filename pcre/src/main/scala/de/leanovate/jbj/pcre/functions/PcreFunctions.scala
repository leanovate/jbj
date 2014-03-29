/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.pcre.functions

import de.leanovate.jbj.runtime.adapter.GlobalFunctions
import de.leanovate.jbj.runtime.annotations.{ParameterMode, GlobalFunction}
import de.leanovate.jbj.runtime.types.PParam
import scala.util.matching.Regex
import java.util.regex.Pattern
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import scala.Some
import de.leanovate.jbj.runtime.value.IntegerVal
import de.leanovate.jbj.runtime.context.Context

trait PcreFunctions {
  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_match(pattern: String, subject: String, optMatches: Option[PVar], flags: Option[Int], offset: Option[Int])(implicit ctx: Context): PVal = {
    convertPattern(pattern).map {
      regex =>
        regex.findFirstMatchIn(subject).map {
          m =>
            val groupNames = m.groupNames
            optMatches.foreach {
              matches =>
                val matchesKeyValues = Seq.newBuilder[(Option[PVal], PAny)]
                matchesKeyValues += Some(IntegerVal(0)) -> StringVal(m.group(0))
                Range(1, m.groupCount + 1).foreach {
                  idx =>
                    val group = Option(m.group(idx)).getOrElse("")
                    if (idx <= groupNames.length) {
                      Option(groupNames(idx - 1)).foreach {
                        groupName =>
                          matchesKeyValues += Some(StringVal(groupName)) -> StringVal(group)
                      }
                    }
                    matchesKeyValues += Some(IntegerVal(idx)) -> StringVal(group)
                }
                matches := ArrayVal(matchesKeyValues.result(): _*)
            }
            IntegerVal(1)
        }.getOrElse(IntegerVal(0))
    }.getOrElse(BooleanVal.FALSE)
  }

  def convertPattern(raw: String): Option[Regex] = {
    extractPattern(raw.trim).map {
      case (delimiter, pattern, flags) =>
        var (effective, names) = extractGroupNames(pattern)
        if (flags.contains('x'))
          effective = effective.replaceAll("[ \n\r\t]", "")
        effective.r(names: _*)
    }
  }

  //  (\?([P]?<([^>]+))>)?(:)?)?
  private val groupNamePattern = """\((\?([P]?<([^>]+)>)?(\:)?)?""".r

  private def extractGroupNames(pattern: String): (String, Seq[String]) = {
    val names = Seq.newBuilder[String]
    val effective = groupNamePattern.replaceAllIn(pattern, {
      m =>
        m.subgroups match {
          case _ :: _ :: _ :: ":" :: Nil =>
            "(?:"
          case _ :: _ :: name :: _ :: Nil =>
            names += name
            "("
          case _ => "("
        }
    })
    effective -> names.result()
  }

  private def extractPattern(raw: String): Option[(Char, String, Set[Char])] = {
    if (raw.isEmpty)
      None
    else {
      val delimiter = raw.charAt(0)
      val idx = raw.indexOf(delimiter, 1)
      if (idx < 0)
        None
      else {
        val pattern = raw.substring(1, idx)
        val flags = raw.substring(idx + 1).toCharArray.toSet

        Some(delimiter, pattern, flags)
      }
    }
  }
}

object PcreFunctions extends PcreFunctions {
  val functions = GlobalFunctions.generatePFunctions[PcreFunctions]
}

