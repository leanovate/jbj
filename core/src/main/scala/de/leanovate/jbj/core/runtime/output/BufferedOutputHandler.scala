package de.leanovate.jbj.core.runtime.output

import java.io.OutputStream
import de.leanovate.jbj.core.JbjExtension
import de.leanovate.jbj.api.JbjException
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

case class BufferedOutputHandler(out: OutputStream, optTransformer: Option[OutputTransformer], size: Int)
  extends OutputStream with OutputHandler {
  private val bufferSize = size
  private val buffer: Array[Byte] = new Array[Byte](bufferSize)
  private var count = 0

  override def write(b: Int) {
    if (count >= bufferSize)
      flushBuffer()
    buffer(count) = b.toByte
    count += 1
  }

  override def flush() {
    flushBuffer()
  }

  override def close() {
    flushBuffer()
  }

  override def clean() {
    count = 0
  }

  override def contents = {
    val result = new Array[Byte](count)
    buffer.copyToArray(result)
    Some(result)
  }

  private def flushBuffer() {
    if (count > 0) {
      optTransformer.map {
        transformer =>
          out.write(transformer.transform(buffer, 0, count))
      }.getOrElse {
        out.write(buffer, 0, count)
      }
      count = 0
    }
  }
}
