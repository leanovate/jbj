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

  case class Script(prog: Prog) {
    val jbj = JbjEnv(TestLocator)
    val bOut = new ByteArrayOutputStream()
    val bErr = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val err = new PrintStream(bErr, false, "UTF-8")
    val context = jbj.newGlobalContext(out, err)

    context.settings.errorReporting = Settings.E_ALL

    def withGet(uriStr: String) = {
      CgiEnvironment.httpGet(uriStr, context)
      this
    }

    def withPost(uriStr: String, formData: String) = {
      CgiEnvironment.httpPostForm(uriStr, formData, context)
      this
    }

    def withCommandLine(args: String) = {
      CliEnvironment.commandLine(prog.fileName, args.split(" "), context)
      this
    }

    def result = {

      var thrown: Option[Throwable] = None

      try {
        prog.exec(context)
      } catch {
        case e: Throwable =>
          thrown = Some(e)
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
    val pseudoFileName = getClass.getName.replace("de.leanovate.jbj.tests", "").replace('.', '/') + ".inlinePhp"
    Script(JbjParser(pseudoFileName, progStr))
  }

  def haveOutput(expected: String) = new Matcher[ScriptResult] {
    def apply[S <: ScriptResult](t: Expectable[S]): MatchResult[S] =
      result(t.map {
        scriptResult :S =>
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
