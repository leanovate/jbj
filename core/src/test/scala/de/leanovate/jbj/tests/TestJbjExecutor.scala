package de.leanovate.jbj.tests

import java.io.{ByteArrayOutputStream, PrintStream}
import de.leanovate.jbj.parser.JbjParser
import org.scalatest.matchers.{MatchResult, MustMatchers, Matcher}
import de.leanovate.jbj.runtime.context.GlobalContext
import scala.Some

trait TestJbjExecutor extends MustMatchers {

  case class ScriptResult(out: String, err: String, exception: Option[Throwable])

  def script(progStr: String): ScriptResult = {
    val prog = JbjParser(progStr)
    val bOut = new ByteArrayOutputStream()
    val bErr = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val err = new PrintStream(bErr, false, "UTF-8")
    val context = GlobalContext(out, err)

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

  def haveOutput(right: String) = new Matcher[Any] {
    def apply(left: Any) = left match {
      case ScriptResult(left, _, _) =>
        be(right).apply(left)
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
          false,
          "no exception was thrown",
          "no exception was thrown"
        )
    }
  }
}
