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
import de.leanovate.jbj.runtime.value.IntegerVal
import java.util.regex.PatternSyntaxException
import de.leanovate.jbj.core.parser.{ParseContext, JbjParser}
import de.leanovate.jbj.runtime.exception.{FatalErrorJbjException, ParseJbjException}
import de.leanovate.jbj.pcre.PcreConstants._
import de.leanovate.jbj.runtime.CallbackHelper

trait PcreFunctions {
  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_match(pattern: String, subject: String, optMatches: Option[PVar],
                 optFlags: Option[Int], optOffset: Option[Int])(implicit ctx: Context): PVal = {
    convertPattern("preg_match", pattern) match {
      case Left((regex, patternFlags)) =>
        regex.findFirstMatchIn(subject).map {
          m =>
            val groupNames = m.groupNames
            optMatches.foreach {
              matches =>
                val matchesKeyValues = new ExtendedLinkedHashMap[Any]
                matchesKeyValues += 0L -> StringVal(m.group(0))
                Range(1, maxNotNullGroup(m)).foreach {
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
    val flags = optFlags.getOrElse(PREG_PATTERN_ORDER)
    val patternOrder = (flags & PREG_PATTERN_ORDER) != 0
    val patternSet = (flags & PREG_SET_ORDER) != 0
    val offsetCapture = (flags & PREG_OFFSET_CAPTURE) != 0

    if (flags > 259 || (patternOrder && patternSet)) {
      ctx.log.warn("preg_match_all(): Invalid flags specified")
      NullVal
    } else {
      convertPattern("preg_match_all", pattern) match {
        case Left((regex, patternFlags)) if patternSet =>
          val resultsKeyValues = Seq.newBuilder[(Option[PVal], PAny)]
          regex.findAllMatchIn(subject).foreach {
            m =>
              val groupNames = m.groupNames
              val matchesKeyValues = new ExtendedLinkedHashMap[Any]
              if (offsetCapture)
                matchesKeyValues += 0L -> ArrayVal(
                  None -> StringVal(m.group(0)),
                  None -> IntegerVal(m.start(0)))
              else
                matchesKeyValues += 0L -> StringVal(m.group(0))
              Range(1, maxNotNullGroup(m)).foreach {
                idx =>
                  val group = Option(m.group(idx)).getOrElse("")
                  val v = if (offsetCapture) ArrayVal(
                    None -> StringVal(group),
                    None -> IntegerVal(m.start(idx)))
                  else
                    StringVal(group)
                  if (idx <= groupNames.length) {
                    Option(groupNames(idx - 1)).foreach {
                      groupName =>
                        matchesKeyValues += groupName -> v
                    }
                  }
                  matchesKeyValues += idx.toLong -> v
              }
              resultsKeyValues += None -> new ArrayVal(matchesKeyValues)
          }
          optMatches.foreach {
            matches =>
              matches := ArrayVal(resultsKeyValues.result(): _*)
          }
          IntegerVal(resultsKeyValues.result().size)
        case Left((regex, patternFlags)) =>
          val matchesKeyValues = new ExtendedLinkedHashMap[Any]
          var count = 0
          regex.findAllMatchIn(subject).foreach {
            m =>
              val groupNames = m.groupNames
              matchesKeyValues.getOrElseUpdate(0L, ArrayVal()).asInstanceOf[ArrayVal].append(StringVal(m.group(0)))
              count += 1
              Range(1, maxNotNullGroup(m)).foreach {
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
  def preg_replace(patternVal: PVal, replacementVal: PVal, subject: String,
                   optLimit: Option[Int], optCount: Option[Int])(implicit ctx: Context): PVal = {
    (patternVal, replacementVal) match {
      case (patterns: ArrayVal, replacements: ArrayVal) =>
        Range(0, math.min(patterns.size, replacements.size)).foldLeft(StringVal(subject): PVal) {
          (prev, idx) => prev match {
            case StringVal(str) =>
              val pattern = patterns.keyValues.apply(idx)._2.asVal.toStr.asString
              val replacement = replacements.keyValues.apply(idx)._2.asVal.toStr.asString
              _preg_replace(pattern, replacement, str, optLimit, optCount)
            case v => v
          }
        }
      case (patterns: ArrayVal, replacement) =>
        Range(0, patterns.size).foldLeft(StringVal(subject): PVal) {
          (prev, idx) => prev match {
            case StringVal(str) =>
              val pattern = patterns.keyValues.apply(idx)._2.asVal.toStr.asString
              _preg_replace(pattern, replacement.toStr.asString, str, optLimit, optCount)
            case v => v
          }
        }
      case (pattern, replacement) =>
        _preg_replace(pattern.toStr.asString, replacement.toStr.asString, subject, optLimit, optCount)
    }
  }

  private def _preg_replace(pattern: String, replacement: String, subject: String,
                            optLimit: Option[Int], optCount: Option[Int])(implicit ctx: Context): PVal = {
    convertPattern("preg_replace", pattern) match {
      case Left((regex, patternFlags)) if patternFlags.contains('e') =>
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
            sb.append(evalReplace("preg_replace", script).toStr.asString)
        }
        sb.append(subject.substring(last))
        StringVal(sb.toString())
      case Left((regex, patternFlags)) =>
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

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_replace_callback(patternVal: PVal, callback: PVal, subject: String,
                            optLimit: Option[Int], optCount: Option[Int])(implicit ctx: Context): PVal = {
    patternVal match {
      case patterns: ArrayVal =>
        Range(0, patterns.size).foldLeft(StringVal(subject): PVal) {
          (prev, idx) => prev match {
            case StringVal(str) =>
              val pattern = patterns.keyValues.apply(idx)._2.asVal.toStr.asString
              _preg_replace_callback(pattern, callback, str, optLimit, optCount)
            case v => v
          }
        }
      case pattern =>
        _preg_replace_callback(pattern.toStr.asString, callback, subject, optLimit, optCount)
    }
  }

  private def _preg_replace_callback(pattern: String, callback: PVal, subject: String,
                                     optLimit: Option[Int], optCount: Option[Int])(implicit ctx: Context): PVal = {
    convertPattern("preg_replace", pattern) match {
      case Left((regex, patternFlags)) =>
        val sb = new StringBuilder
        var last = 0
        regex.findAllMatchIn(subject).foreach {
          m =>
            val groupNames = m.groupNames
            sb.append(subject.substring(last, m.start))
            last = m.end
            val matchesKeyValues = new ExtendedLinkedHashMap[Any]
            matchesKeyValues += 0L -> StringVal(m.group(0))
            Range(1, maxNotNullGroup(m)).foreach {
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
            val result = CallbackHelper.callCallback(callback, new ArrayVal(matchesKeyValues))
            sb.append(result.asVal.toStr.asString)
        }
        sb.append(subject.substring(last))
        StringVal(sb.toString())
      case Right(v) => v
    }
  }


  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_split(pattern: String, subject: String, optLimit: Option[Int], optFlags: Option[Int])(implicit ctx: Context): PVal = {
    val flags = optFlags.getOrElse(0)
    val delimCapture = (flags & PREG_SPLIT_DELIM_CAPTURE) != 0
    val splitOffsetCapture = (flags & PREG_SPLIT_OFFSET_CAPTURE) != 0

    convertPattern("preg_split", pattern) match {
      case Left((regex, patternFlags)) =>
        val result = Seq.newBuilder[(Option[PVal], PAny)]
        var last = 0
        regex.findAllMatchIn(subject).foreach {
          m =>
            if (splitOffsetCapture)
              result += None -> ArrayVal(
                None -> StringVal(subject.substring(last, m.start)),
                None -> IntegerVal(last))
            else
              result += None -> StringVal(subject.substring(last, m.start))
            last = m.end
            if (delimCapture) {
              if (splitOffsetCapture)
                result += None -> ArrayVal(
                  None -> StringVal(m.group(m.groupCount)),
                  None -> IntegerVal(m.start(m.groupCount)))
              else
                result += None -> StringVal(m.group(m.groupCount))
            }
        }
        if (last < subject.length) {
          if (splitOffsetCapture)
            result += None -> ArrayVal(
              None -> StringVal(subject.substring(last)),
              None -> IntegerVal(last))
          else
            result += None -> StringVal(subject.substring(last))
        }
        ArrayVal(result.result(): _*)
      case Right(v) => v
    }
  }

  @GlobalFunction(parameterMode = ParameterMode.EXACTLY_WARN)
  def preg_last_error()(implicit ctx: Context): Int = {
    ctx.global.findVariable("__preg_last_error").map(_.asVal.toInteger.asInt).getOrElse(PREG_NO_ERROR)
  }

  private def set_last_error(errorCode: Int)(implicit ctx: Context) {
    ctx.global.findOrDefineVariable("__preg_last_error") := IntegerVal(errorCode)
  }

  private def convertPattern(functionName: String, raw: String)(implicit ctx: Context): Either[(Regex, Set[Char]), PVal] = {
    extractPattern(raw.trim).map {
      case (delimiter, pattern, flags) =>
        var (effective, names) = extractGroupNames(pattern)
        if (flags.contains('x'))
          effective = effective.replaceAll("[ \n\r\t]", "")
        if (flags.contains('s'))
          effective = "(?s)" + effective
        if (flags.contains('m'))
          effective = "(?m)" + effective
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

  private val groupNamePattern = """(\\)?\((\?([P]?<([^>]+)>)?(\:)?)?""".r

  def extractGroupNames(pattern: String): (String, Seq[String]) = {
    val names = Seq.newBuilder[String]
    val effective = groupNamePattern.replaceAllIn(pattern, {
      m =>
        m.subgroups match {
          case "\\" :: rest :: _ :: _ :: _ :: Nil =>
            "\\\\(" + Option(rest).getOrElse("")
          case _ :: _ :: _ :: _ :: ":" :: Nil =>
            "(?:"
          case null :: _ :: _ :: name :: _ :: Nil =>
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
      val expr = parser.parseExpr(script)

      expr.eval
    } catch {
      case e: ParseJbjException =>
        throw new FatalErrorJbjException(s"$functionName(): Failed evaluating code: $script")
    }
  }

  private def maxNotNullGroup(m: Regex.Match): Int = {
    Range(m.groupCount, 0, -1).find(m.group(_) != null).map(_ + 1).getOrElse(1)
  }
}

object PcreFunctions extends PcreFunctions {
  val functions = GlobalFunctions.generatePFunctions[PcreFunctions]
}
