package de.leanovate.jbj.core.tests

import java.io.{ByteArrayOutputStream, PrintStream}
import de.leanovate.jbj.core.parser.JbjParser
import de.leanovate.jbj.core.runtime.env.{CliEnvironment, CgiEnvironment}
import org.specs2.matcher.{MatchResult, BeEqualTo, Expectable, Matcher}
import de.leanovate.jbj.core.JbjEnv
import scala.Some
import de.leanovate.jbj.api.JbjSettings

trait TestJbjExecutor {

  case class Script(progString: String) {
    val bErr = new ByteArrayOutputStream()
    val err = new PrintStream(bErr, false, "UTF-8")

    val jbj = JbjEnv(TestLocator, errorStream = Some(err))
    val pseudoFileName = TestJbjExecutor.this.getClass.getName.replace("de.leanovate.jbj.core.tests", "").replace('.', '/') + ".inlinePhp"
    val prog = JbjParser(pseudoFileName, progString, jbj.settings)
    val bOut = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    implicit val context = jbj.newGlobalContext(out)

    context.settings.setErrorReporting(JbjSettings.E_ALL)

    def withGet(uriStr: String) = {
      CgiEnvironment.httpRequest(TestRequestInfo.get(uriStr))
      this
    }

    def withPost(uriStr: String, formData: String) = {
      CgiEnvironment.httpRequest(TestRequestInfo.post(uriStr, formData))
      this
    }

    def withCommandLine(args: String) = {
      CliEnvironment.commandLine(prog.fileName, args.split(" "))
      this
    }

    def result = {

      var thrown: Option[Throwable] = None

      try {
        prog.exec
      } catch {
        case e: Throwable =>
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
