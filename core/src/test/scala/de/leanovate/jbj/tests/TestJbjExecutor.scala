package de.leanovate.jbj.tests

import java.io.{ByteArrayOutputStream, PrintStream}
import de.leanovate.jbj.runtime.{Context}
import de.leanovate.jbj.parser.JbjParser
import scala.util.Success
import de.leanovate.jbj.runtime.context.GlobalContext

trait TestJbjExecutor {
  def resultOf(progStr: String): String = {
    val prog = JbjParser(progStr)
    val bOut = new ByteArrayOutputStream()
    val bErr = new ByteArrayOutputStream()
    val out = new PrintStream(bOut, false, "UTF-8")
    val err = new PrintStream(bErr, false, "UTF-8")
    val context = GlobalContext(out, err)

    prog.exec(context)

    out.flush()
    out.close()

    new String(bOut.toByteArray, "UTF-8")
  }
}
