/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.runtime.output

import java.io.OutputStream

case class DirectOutputHandler(out: OutputStream, level: Int) extends OutputStream with OutputHandler {
  override def name = None

  override def bufferUsed = 0

  override def bufferSize = 0

  override def bufferType = PHP_OUTPUT_HANDLER_INTERNAL

  override def bufferFlags = PHP_OUTPUT_HANDLER_STDFLAGS | bufferType

  override def bufferChunkSize = 0

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

  override def endClean() {
  }

  override def endFlush() {
  }

  override def clean() {
  }

  override def contents = None
}
