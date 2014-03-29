package de.leanovate.jbj.pcre.tests

import java.io.{PrintStream, ByteArrayOutputStream}
import de.leanovate.jbj.core.JbjEnvironmentBuilder
import de.leanovate.jbj.api.http.{JbjException, CookieInfo, JbjSettings}
import de.leanovate.jbj.runtime.env.{CliEnvironment, CgiEnvironment}
import de.leanovate.jbj.core.parser.JbjParser
import de.leanovate.jbj.runtime.exception.ParseJbjException
import org.specs2.matcher.{BeEqualTo, MatchResult, Expectable, Matcher}
import de.leanovate.jbj.pcre.PcreExtension

trait TestJbjExecutor {

  case class Script(progString: String) {
    val bErr = new ByteArrayOutputStream()

    val err = new PrintStream(bErr, false, "UTF-8")

    val jbj = JbjEnvironmentBuilder().withScriptLocator(TestLocator).withErrStream(bErr).withExtension(PcreExtension)
      .build()

    val pseudoFileName =
      TestJbjExecutor.this.getClass.getName.replace("de.leanovate.jbj.bcmath.tests", "").replace('.', '/') + ".inlinePhp"

    val bOut = new ByteArrayOutputStream()

    val out = new PrintStream(bOut, false, "UTF-8")

    implicit val context = jbj.newGlobalContext(out, None)

    context.settings.setErrorReporting(JbjSettings.E_ALL)

    def withErrorReporting(errorReporting: Int) = {

      context.settings.setErrorReporting(JbjSettings.ErrorLevel.errorLevelsForValue(errorReporting))
      this
    }

    def withAlwaysPopulateRawPostData(alwaysPopulateRawPostData: Boolean) = {

      context.settings.setAlwaysPopulateRawPostData(alwaysPopulateRawPostData)
      this
    }

    def withMaxPostSize(maxPostSize: Long) = {

      context.settings.setPostMaxSize(maxPostSize)
      this
    }

    def withMaxInputNestingLevel(maxInputNestingLevel: Int) = {

      context.settings.setMaxInputNestingLevel(maxInputNestingLevel)
      this
    }

    def withTrackErrors(trackErrors: Boolean) = {

      context.settings.setTrackErrors(trackErrors)
      this
    }

    def withDisplayErrors(displayErrors: JbjSettings.DisplayError) = {

      context.settings.setDisplayErrors(displayErrors)
      this
    }

    def withShortOpenTag(shortOpenTag: Boolean) = {

      jbj.settings.setShortOpenTag(shortOpenTag)
      this
    }

    def withAspTags(aspTags: Boolean) = {

      jbj.settings.setAspTags(aspTags)
      this
    }

    def withBcScaleFactor(bcScaleFactor: Int) = {

      context.settings.setBcScaleFactor(bcScaleFactor)
      this
    }

    def withGet(uriStr: String, cookies: Seq[CookieInfo] = Seq.empty) = {

      CgiEnvironment.httpRequest(TestRequestInfo.get(uriStr, cookies))
      this
    }

    def withCommandLine(args: String) = {

      CliEnvironment.commandLine(pseudoFileName, args.split(" "))
      this
    }

    def result = {

      var thrown: Option[Throwable] = None

      try {
        val prog = JbjParser(pseudoFileName, progString, jbj.settings)

        prog.run
      } catch {
        case e: ParseJbjException =>
          context.log.parseError(e.pos, e.msg)
          thrown = Some(e)
        case e: JbjException =>
          thrown = Some(e)
      } finally {
        try {
          context.cleanup()
        } catch {
          case e: Throwable =>
            thrown = Some(e)
        }
      }

      out.flush()
      out.close()
      err.flush()
      err.close()

      ScriptResult(new String(bOut.toByteArray, "UTF-8"), new String(bErr.toByteArray, "UTF-8"), thrown)
    }
  }

  case class ScriptResult(out: String, err: String, exception: Option[Throwable])

  def script(progStr: String): Script = {

    Script(progStr)
  }

  def haveOutput(expected: String) = new Matcher[ScriptResult] {
    def apply[S <: ScriptResult](t: Expectable[S]): MatchResult[S] =
      result(t.map {
        scriptResult: S =>
          scriptResult.out
      }.applyMatcher(new BeEqualTo(expected)), t)
  }

  def haveThrown(expected: Class[_]) = new Matcher[ScriptResult] {
    def apply[S <: ScriptResult](t: Expectable[S]) = t.value match {
      case ScriptResult(_, _, Some(thrown)) =>
        result(
                thrown.getClass == expected,
                thrown + " is not of " + expected,
                thrown + " is of " + expected,
                t
              )
      case ScriptResult(_, _, None) =>
        result(
                test = false,
                "no exception was thrown",
                "no exception was thrown",
                t
              )
    }
  }
}
