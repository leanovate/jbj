package de.leanovate.jbj.core.runtime.output

import java.io.OutputStream
import de.leanovate.jbj.core.JbjExtension
import de.leanovate.jbj.api.JbjException
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException

case class BufferedOutputHandler(out: OutputStream, level: Int, optTransformer: Option[OutputTransformer], chunkSize: Int, maxSize: Int)
  extends OutputStream with OutputHandler {
  private var _bufferSize = if (maxSize < 0) 16384 else maxSize
  private var buffer: Array[Byte] = new Array[Byte](bufferSize)
  private var count = 0
  private var _flags = PHP_OUTPUT_HANDLER_STARTED | PHP_OUTPUT_HANDLER_PROCESSED
  private var _first = true

  override def name = optTransformer.map(_.name)

  override def bufferUsed = count

  override def bufferSize = _bufferSize

  override def bufferType = if (optTransformer.isDefined) PHP_OUTPUT_HANDLER_USER else PHP_OUTPUT_HANDLER_INTERNAL

  override def bufferFlags = _flags

  override def bufferChunkSize = chunkSize

  override def write(b: Int) {
    if (count >= _bufferSize) {
      if (maxSize < 0)
        increaseBuffer(2 * _bufferSize)
      else
        flushBuffer(if (_first) PHP_OUTPUT_HANDLER_START else PHP_OUTPUT_HANDLER_WRITE)
    }
    buffer(count) = b.toByte
    count += 1
    if (chunkSize > 0 && count >= chunkSize)
      flushBuffer(if (_first) PHP_OUTPUT_HANDLER_START else PHP_OUTPUT_HANDLER_WRITE)
  }


  override def write(b: Array[Byte], off: Int, len: Int) {
    if (len >= _bufferSize) {
      flushBuffer(if (_first) PHP_OUTPUT_HANDLER_START else PHP_OUTPUT_HANDLER_WRITE)
      pushBuffer(if (_first) PHP_OUTPUT_HANDLER_START else PHP_OUTPUT_HANDLER_WRITE, b, off, len)
    } else {
      if (len > _bufferSize - count) {
        if (maxSize < 0)
          increaseBuffer(len + count)
        else
          flushBuffer(if (_first) PHP_OUTPUT_HANDLER_START else PHP_OUTPUT_HANDLER_WRITE)
      }
      b.copyToArray(buffer, count)
      count += len
      if (chunkSize > 0 && count >= chunkSize)
        flushBuffer(if (_first) PHP_OUTPUT_HANDLER_START else PHP_OUTPUT_HANDLER_WRITE)
    }
  }

  override def flush() {
    flushBuffer(PHP_OUTPUT_HANDLER_FLUSH)
  }

  override def close() {
    endFlush()
  }

  override def endClean() {
    optTransformer.foreach {
      transformer =>
        transformer.transform(PHP_OUTPUT_HANDLER_CLEAN | PHP_OUTPUT_HANDLER_FINAL, buffer, 0, count)
    }
    count = 0
  }

  override def endFlush() {
    flushBuffer(PHP_OUTPUT_HANDLER_FLUSH | PHP_OUTPUT_HANDLER_FINAL)
  }

  override def clean() {
    optTransformer.foreach {
      transformer =>
        transformer.transform(PHP_OUTPUT_HANDLER_CLEAN, buffer, 0, count)
    }
    count = 0
  }

  override def contents = {
    val result = new Array[Byte](count)
    buffer.copyToArray(result)
    Some(result)
  }

  override def suspend() {
    _flags = PHP_OUTPUT_HANDLER_STDFLAGS | bufferType
  }

  override def resume() {
    _flags = PHP_OUTPUT_HANDLER_STDFLAGS | PHP_OUTPUT_HANDLER_STARTED | PHP_OUTPUT_HANDLER_PROCESSED | bufferType
  }

  private def increaseBuffer(nextBufferSize: Int) {
    _bufferSize = nextBufferSize
    val nextBuffer = new Array[Byte](_bufferSize)
    buffer.copyToArray(nextBuffer)
    buffer = nextBuffer
  }

  private def flushBuffer(flags: Int) {
    if (count > 0) {
      pushBuffer(flags, buffer, 0, count)
      count = 0
    }
  }

  private def pushBuffer(flags: Int, b: Array[Byte], off: Int, len: Int) {
    optTransformer.map {
      transformer =>
        out.write(transformer.transform(flags, b, off, len))
    }.getOrElse {
      out.write(b, off, len)
    }
    _first = false
  }
}
