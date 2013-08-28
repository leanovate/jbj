package de.leanovate.jbj.core.runtime.output

import java.io.OutputStream
import scala.collection.mutable
import de.leanovate.jbj.api.JbjSettings

case class OutputBuffer(out: OutputStream, settings: JbjSettings) {
  var currentOut: OutputStream with OutputHandler = if (settings.getOutputBuffering == 0)
    DirectOutputHandler(out, -1)
  else
    BufferedOutputHandler(out, -1, None, 0, settings.getOutputBuffering)

  private val _bufferStack = new mutable.Stack[OutputStream with OutputHandler]

  def print(str: String) {
    currentOut.write(str.getBytes(settings.getCharset))
  }

  def println() {
    currentOut.write('\n')
  }

  def println(str: String) {
    currentOut.write(str.getBytes(settings.getCharset) :+ '\n'.toByte)
  }

  def bufferStart(optTransformer: Option[OutputTransformer], chunkSize: Int): Boolean = {
    if (settings.getOutputBuffering == 0)
      false
    else {
      val buffer = new BufferedOutputHandler(currentOut, _bufferStack.size, optTransformer, chunkSize, settings.getOutputBuffering)
      _bufferStack.push(currentOut)
      currentOut.suspend()
      currentOut = buffer
      true
    }
  }

  def bufferClean() {
    currentOut.clean()
  }

  def bufferEndClean(): Boolean = {
    if (bufferStack.isEmpty)
      false
    else {
      currentOut.endClean()
      currentOut = _bufferStack.pop()
      currentOut.resume()
      true
    }
  }

  def bufferFlush() {
    currentOut.flush()
  }

  def bufferEndFlush(): Boolean = {
    if (bufferStack.isEmpty)
      false
    else {
      currentOut.endFlush()
      currentOut = _bufferStack.pop()
      true
    }
  }

  def bufferLevel: Int = bufferStack.size

  def bufferContents: Option[Array[Byte]] = {
    currentOut.contents
  }

  def bufferStack: Seq[OutputHandler] =
    if (_bufferStack.isEmpty)
      Seq.empty
    else
      currentOut :: _bufferStack.dropRight(1).toList

  def closeAll() {
    val origCurrentOut = currentOut
    currentOut = DirectOutputHandler(out, -1)
    origCurrentOut.flush()
    _bufferStack.foreach(_.flush())
    out.close()
  }
}

