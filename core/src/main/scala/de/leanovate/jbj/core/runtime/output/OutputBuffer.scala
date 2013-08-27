package de.leanovate.jbj.core.runtime.output

import java.io.OutputStream
import scala.collection.mutable
import de.leanovate.jbj.api.JbjSettings

case class OutputBuffer(out: OutputStream, settings: JbjSettings) {
  var currentOut: OutputStream with OutputHandler = if (settings.getOutputBuffering == 0)
    DirectOutputHandler(out)
  else
    BufferedOutputHandler(out, None, settings.getOutputBuffering)

  var bufferStack = new mutable.Stack[OutputStream with OutputHandler]

  def print(str: String) {
    currentOut.write(str.getBytes(settings.getCharset))
  }

  def println() {
    currentOut.write('\n')
  }

  def println(str: String) {
    currentOut.write(str.getBytes(settings.getCharset))
    currentOut.write('\n')
  }

  def bufferStart(optTransformer: Option[OutputTransformer], size: Int): Boolean = {
    val buffer = new BufferedOutputHandler(out, optTransformer, size)
    bufferStack.push(currentOut)
    currentOut = buffer
    true
  }

  def bufferClean() {
    currentOut.clean()
  }

  def bufferEndClean(): Boolean = {
    if (bufferStack.isEmpty)
      false
    else {
      currentOut.clean()
      currentOut = bufferStack.pop()
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
      currentOut.flush()
      currentOut = bufferStack.pop()
      true
    }
  }

  def bufferLevel: Int = bufferStack.size

  def bufferContents: Option[Array[Byte]] = {
    currentOut.contents
  }

  def closeAll() {
    currentOut.flush()
    bufferStack.foreach(_.flush())
    out.close()
  }
}

