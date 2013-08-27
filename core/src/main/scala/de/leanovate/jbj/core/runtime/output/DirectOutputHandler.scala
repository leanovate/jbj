package de.leanovate.jbj.core.runtime.output

import java.io.OutputStream

case class DirectOutputHandler(out: OutputStream) extends OutputStream with OutputHandler {
  override def write(b: Int) {
    out.write(b)
  }

  override def write(b: Array[Byte]) {
    out.write(b)
  }

  override def write(b: Array[Byte], off: Int, len: Int) {
    out.write(b, off, len)
  }

  override def flush() {
    out.flush()
  }

  override def clean() {
  }

  override def contents = None
}
