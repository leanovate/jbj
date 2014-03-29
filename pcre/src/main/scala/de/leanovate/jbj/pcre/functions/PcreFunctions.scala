/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.pcre.functions

import de.leanovate.jbj.runtime.adapter.GlobalFunctions
import de.leanovate.jbj.runtime.annotations.ParameterMode
import scala.util.matching.Regex
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import scala.Some
import de.leanovate.jbj.runtime.value.IntegerVal
import java.util.regex.PatternSyntaxException
import de.leanovate.jbj.core.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.runtime.ReturnExecResult
import de.leanovate.jbj.runtime.exception.{FatalErrorJbjException, ParseJbjException}

trait PcreFunctions {
  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_match(pattern: String, subject: String, optMatches: Option[PVar],
                 optFlags: Option[Int], optOffset: Option[Int])(implicit ctx: Context): PVal = {
    convertPattern("preg_match", pattern) match {
      case Left((regex, _)) =>
        regex.findFirstMatchIn(subject).map {
          m =>
            val groupNames = m.groupNames
            optMatches.foreach {
              matches =>
                val matchesKeyValues = new ExtendedLinkedHashMap[Any]
                matchesKeyValues += 0L -> StringVal(m.group(0))
                Range(1, m.groupCount + 1).foreach {
                  idx =>
                    val group = Option(m.group(idx)).getOrElse("")
                    if (idx <= groupNames.length) {
                      Option(groupNames(idx - 1)).foreach {
                        groupName =>
                          matchesKeyValues += groupName -> StringVal(group)
                      }
                    }
                    matchesKeyValues += idx.toLong -> StringVal(group)
                }
                matches := new ArrayVal(matchesKeyValues)
            }
            IntegerVal(1)
        }.getOrElse(IntegerVal(0))
      case Right(v) => v
    }
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_match_all(pattern: String, subject: String, optMatches: Option[PVar],
                     optFlags: Option[Int], optOffset: Option[Int])(implicit ctx: Context): PVal = {
    val flags = optFlags.getOrElse(0x1)
    val patternOrder = (flags & 0x1) != 0
    val patternSet = (flags & 0x2) != 0

    if (flags > 256 || (patternOrder && patternSet)) {
      ctx.log.warn("preg_match_all(): Invalid flags specified")
      NullVal
    } else {
      convertPattern("preg_match_all", pattern) match {
        case Left((regex, _)) if patternSet =>
          val resultsKeyValues = Seq.newBuilder[(Option[PVal], PAny)]
          regex.findAllMatchIn(subject).foreach {
            m =>
              val groupNames = m.groupNames
              val matchesKeyValues = new ExtendedLinkedHashMap[Any]
              matchesKeyValues += 0L -> StringVal(m.group(0))
              Range(1, m.groupCount + 1).foreach {
                idx =>
                  val group = Option(m.group(idx)).getOrElse("")
                  if (idx <= groupNames.length) {
                    Option(groupNames(idx - 1)).foreach {
                      groupName =>
                        matchesKeyValues += groupName -> StringVal(group)
                    }
                  }
                  matchesKeyValues += idx.toLong -> StringVal(group)
              }
              resultsKeyValues += None -> new ArrayVal(matchesKeyValues)
          }
          optMatches.foreach {
            matches =>
              matches := ArrayVal(resultsKeyValues.result(): _*)
          }
          IntegerVal(resultsKeyValues.result().size)
        case Left((regex, _)) =>
          val matchesKeyValues = new ExtendedLinkedHashMap[Any]
          var count = 0
          regex.findAllMatchIn(subject).foreach {
            m =>
              val groupNames = m.groupNames
              matchesKeyValues.getOrElseUpdate(0L, ArrayVal()).asInstanceOf[ArrayVal].append(StringVal(m.group(0)))
              count += 1
              Range(1, m.groupCount + 1).foreach {
                idx =>
                  val group = Option(m.group(idx)).getOrElse("")
                  if (idx <= groupNames.length) {
                    Option(groupNames(idx - 1)).foreach {
                      groupName =>
                        matchesKeyValues.getOrElseUpdate(groupName, ArrayVal()).asInstanceOf[ArrayVal].append(StringVal(group))
                    }
                  }
                  matchesKeyValues.getOrElseUpdate(idx.toLong, ArrayVal()).asInstanceOf[ArrayVal].append(StringVal(group))
              }
          }
          optMatches.foreach {
            matches =>
              matches := new ArrayVal(matchesKeyValues)
          }
          IntegerVal(count)
        case Right(v) => v
      }
    }
  }

  private val quoteCharPattern = """[\\\+\*\?\[\^\]\$\(\)\{\}\=\!\<\>\|\:\-]""".r

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN, warnResult = NullVal)
  def preg_quote(str: String, optDelimiter: Option[String]): String = {
    quoteCharPattern.replaceAllIn(str, {
      m =>
        "\\${m.group(0)}"
    })
  }

  val replacePattern = """(?:\\\\|\$)([0-9]+|\{([0-9]+)\})""".r

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_replace(pattern: String, replacement: String, subject: String,
                   optLimit: Option[Int], optCount: Option[Int])(implicit ctx: Context): PVal = {
    convertPattern("preg_replace", pattern) match {
      case Left((regex, flags)) if flags.contains('e') =>
        ctx.log.deprecated(ctx.currentPosition, "preg_replace(): The /e modifier is deprecated, use preg_replace_callback instead")
        val sb = new StringBuilder
        var last = 0
        regex.findAllMatchIn(subject).foreach {
          m =>
            sb.append(subject.substring(last, m.start))
            last = m.end
            val script = replacePattern.replaceAllIn(replacement, {
              rm =>
                rm.subgroups match {
                  case num :: null :: Nil =>
                    m.group(num.toInt)
                  case _ :: num :: Nil =>
                    m.group(num.toInt)
                  case _ =>
                    ""
                }
            })
            sb.append(evalReplace("preg_replace", script).toStr.toString)
        }
        sb.append(subject.substring(last))
        StringVal(sb.toString())
      case Left((regex, flags)) =>
        val sb = new StringBuilder
        var last = 0
        regex.findAllMatchIn(subject).foreach {
          m =>
            sb.append(subject.substring(last, m.start))
            last = m.end
            sb.append(replacePattern.replaceAllIn(replacement, {
              rm =>
                rm.subgroups match {
                  case num :: null :: Nil =>
                    m.group(num.toInt)
                  case _ :: num :: Nil =>
                    m.group(num.toInt)
                  case _ =>
                    ""
                }
            }))
        }
        sb.append(subject.substring(last))
        StringVal(sb.toString())
      case Right(v) => v
    }
  }

  def convertPattern(functionName: String, raw: String)(implicit ctx: Context): Either[(Regex, Set[Char]), PVal] = {
    extractPattern(raw.trim).map {
      case (delimiter, pattern, flags) =>
        var (effective, names) = extractGroupNames(pattern)
        if (flags.contains('x'))
          effective = effective.replaceAll("[ \n\r\t]", "")
        try {
          Left(effective.r(names: _*) -> flags)
        } catch {
          case e: PatternSyntaxException =>
            ctx.log.warn(s"$functionName(): Compilation failed: ${e.getDescription} at offset ${e.getIndex}")
            Right(NullVal)
        }
    }.getOrElse {
      ctx.log.warn(s"$functionName(): Empty regular expression")
      Right(BooleanVal.FALSE)
    }
  }

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

  private def evalReplace(functionName: String, script: String)(implicit ctx: Context): PVal = {
    try {
      val parser = new JbjParser(ParseContext("%s(%d) : eval()'d code".format(ctx.currentPosition.fileName, ctx.currentPosition.line), ctx.settings))
      val prog = parser.parseStmt(script)

      prog.exec match {
        case ReturnExecResult(returnExpr) => returnExpr.map(_.byVal).getOrElse(NullVal)
        case _ => NullVal
      }
    } catch {
      case e: ParseJbjException =>
        throw new FatalErrorJbjException(s"$functionName(): Failed evaluating code: $script")
    }
  }
}

object PcreFunctions extends PcreFunctions {
  val functions = GlobalFunctions.generatePFunctions[PcreFunctions]
}
