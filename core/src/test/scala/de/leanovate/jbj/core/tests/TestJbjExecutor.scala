/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import java.io.{ByteArrayOutputStream, PrintStream}
import de.leanovate.jbj.core.parser.JbjParser
import de.leanovate.jbj.runtime.env.{CliEnvironment, CgiEnvironment}
import org.specs2.matcher.{MatchResult, BeEqualTo, Expectable, Matcher}
import de.leanovate.jbj.core.{JbjEnvironmentBuilder, JbjEnv}
import scala.Some
import de.leanovate.jbj.runtime.exception.ParseJbjException
import de.leanovate.jbj.api.http.{JbjSettings, JbjException, CookieInfo}
import de.leanovate.jbj.runtime.context.HttpResponseContext

trait TestJbjExecutor {

  case class Script(progString: String) {
    val bErr = new ByteArrayOutputStream()
    val err = new PrintStream(bErr, false, "UTF-8")

    val jbj = JbjEnvironmentBuilder().withScriptLocator(TestLocator).withErrStream(bErr).build()
    val pseudoFileName = TestJbjExecutor.this.getClass.getName.replace("de.leanovate.jbj.core.tests", "").replace('.', '/') + ".inlinePhp"
    val bOut = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val httpResponseContext = new HttpResponseContext {
      var httpStatus = 200
      var httpStatusMessage = "OK"
      val httpResponseHeaders = collection.mutable.Map[String, Seq[String]]()
    }
    implicit val context = jbj.newGlobalContext(out, Some(httpResponseContext))

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

    def withSessionAutoStart(sessionAutoStart: Boolean) = {
      context.settings.setSessionAuthStart(sessionAutoStart)
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

    def withGet(uriStr: String, cookies: Seq[CookieInfo] = Seq.empty) = {
      CgiEnvironment.httpRequest(TestRequestInfo.get(uriStr, cookies))
      this
    }

    def withPost(uriStr: String, contentType: String, content: String, cookies: Seq[CookieInfo] = Seq.empty) = {
      CgiEnvironment.httpRequest(TestRequestInfo.post(uriStr, contentType, content, cookies))
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

        prog.exec
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

      ScriptResult(new String(bOut.toByteArray, "UTF-8"), new String(bErr.toByteArray, "UTF-8"), thrown, httpResponseContext)
    }
  }

  case class ScriptResult(out: String, err: String, exception: Option[Throwable], httpResponseContext: HttpResponseContext)

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
      case ScriptResult(_, _, Some(thrown), _) =>
        result(
          thrown.getClass == expected,
          thrown + " is not of " + expected,
          thrown + " is of " + expected,
          t
        )
      case ScriptResult(_, _, None, _) =>
        result(
          test = false,
          "no exception was thrown",
          "no exception was thrown",
          t
        )
    }
  }
}
