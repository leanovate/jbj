package de.leanovate.jbj.tests

import java.io.{ByteArrayOutputStream, PrintStream}
import de.leanovate.jbj.parser.JbjParser
import org.scalatest.matchers.{MatchResult, MustMatchers, Matcher}
import de.leanovate.jbj.runtime.context.GlobalContext
import scala.Some
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.runtime.env.{CliEnvironment, CgiEnvironment}

trait TestJbjExecutor extends MustMatchers {

  case class Script(prog: Prog) {
    val bOut = new ByteArrayOutputStream()
    val bErr = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val err = new PrintStream(bErr, false, "UTF-8")
    val context = GlobalContext(out, err)

    def withGet(uriStr: String) = {
      CgiEnvironment.httpGet(uriStr, context)
      this
    }

    def withPost(uriStr: String, formData: String) = {
      CgiEnvironment.httpPostForm(uriStr, formData, context)
      this
    }

    def withCommandLine(args: String) = {
      CliEnvironment.commandLine("-", args.split(" "), context)
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

  def script(progStr: String): Script = Script(JbjParser("-", progStr))

  def haveOutput(right: String) = new Matcher[Any] {
    def apply(left: Any) = left match {
      case ScriptResult(l, _, _) =>
        be(right).apply(l)
    }
  }

  def haveThrown(right: Class[_]) = new Matcher[Any] {
    def apply(left: Any) = left match {
      case ScriptResult(_, _, Some(thrown)) =>
        MatchResult(
          thrown.getClass == right,
          thrown + " is not of " + right,
          thrown + " is of " + right
        )
      case ScriptResult(_, _, None) =>
        MatchResult(
          matches = false,
          "no exception was thrown",
          "no exception was thrown"
        )
    }
  }
}
