package de.leanovate.jbj.converter

import de.leanovate.jbj.runtime.JbjCodeUnit
import de.leanovate.jbj.core.JbjEnvironmentBuilder
import java.io.{PrintStream, ByteArrayOutputStream}
import java.util.concurrent.atomic.AtomicInteger
import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder
import java.nio.file.Files
import de.leanovate.jbj.runtime.exception.ParseJbjException
import de.leanovate.jbj.api.http.JbjException
import de.leanovate.jbj.runtime.context.HttpResponseContext
import org.specs2.matcher.{BeEqualTo, MatchResult, Expectable, Matcher}

trait TestJbjEnvironment {

  case class Environment(codeUnit: JbjCodeUnit) {
    val bErr = new ByteArrayOutputStream()
    val err = new PrintStream(bErr, false, "UTF-8")
    val filesystem = TestJbjEnvironment.createTestFilesystem()

    val jbj = JbjEnvironmentBuilder().withErrStream(bErr).withFileSystem(filesystem).build()
    val bOut = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val httpResponseContext = new HttpResponseContext {
      var httpStatus = 200
      var httpStatusMessage = "OK"
      val httpResponseHeaders = collection.mutable.Map[String, Seq[String]]()
    }
    implicit val context = jbj.newGlobalContext(out, Some(httpResponseContext))

    def result = {

      var thrown: Option[Throwable] = None

      try {
        codeUnit.exec
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
        filesystem.close()
      }

      out.flush()
      out.close()
      err.flush()
      err.close()

      ScriptResult(new String(bOut.toByteArray, "UTF-8"), new String(bErr.toByteArray, "UTF-8"), thrown, httpResponseContext)
    }
  }

  case class ScriptResult(out: String, err: String, exception: Option[Throwable], httpResponseContext: HttpResponseContext)

  def exec(codeUnit: JbjCodeUnit) = new Environment(codeUnit)

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

object TestJbjEnvironment {
  val counter = new AtomicInteger(0)

  def createTestFilesystem() = {
    val filesystem = MemoryFileSystemBuilder.newLinux().build("test_" + counter.incrementAndGet())
    Files.createDirectory(filesystem.getPath("/tmp"))
    filesystem
  }
}