package de.leanovate.jbj.tests

import java.io.{ByteArrayOutputStream, PrintStream}
import de.leanovate.jbj.parser.JbjParser
import scala.Some
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.runtime.env.{CliEnvironment, CgiEnvironment}
import de.leanovate.jbj.runtime.Settings
import de.leanovate.jbj.JbjEnv
import org.specs2.matcher.{MatchResult, BeEqualTo, Expectable, Matcher}

trait TestJbjExecutor {

  case class Script(prog: Prog, jbj: JbjEnv) {
    val bOut = new ByteArrayOutputStream()
    val bErr = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val err = new PrintStream(bErr, false, "UTF-8")
    implicit val context = jbj.newGlobalContext(out, err)

    context.settings.errorReporting = Settings.E_ALL

    def withGet(uriStr: String) = {
      CgiEnvironment.httpGet(uriStr)
      this
    }

    def withPost(uriStr: String, formData: String) = {
      CgiEnvironment.httpPostForm(uriStr, formData)
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
        context.cleanup()
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
    val jbj = JbjEnv(TestLocator)
    val pseudoFileName = getClass.getName.replace("de.leanovate.jbj.tests", "").replace('.', '/') + ".inlinePhp"
    Script(JbjParser(pseudoFileName, progStr, jbj.settings), jbj)
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
