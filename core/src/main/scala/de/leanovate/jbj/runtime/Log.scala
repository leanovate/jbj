package de.leanovate.jbj.runtime

import java.io.PrintStream

class Log(out: PrintStream, err: PrintStream) {
  def warn(msg: String) {
    err.println("PHP Warning: " + msg)
    out.println("Warning: " + msg)
  }
}
