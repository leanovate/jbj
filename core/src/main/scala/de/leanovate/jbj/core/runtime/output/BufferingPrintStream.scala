package de.leanovate.jbj.core.runtime.output

import java.io.{ByteArrayOutputStream, OutputStream, PrintStream}
import scala.collection.mutable

class BufferingPrintStream(delegatingStream: DelegatingOutputStream)
  extends PrintStream(delegatingStream, true, "UTF-8") {
  var bufferStack = new mutable.Stack[ByteArrayOutputStream]

  def bufferStart() {
    flush()
    val buffer = new ByteArrayOutputStream
    bufferStack.push(buffer)
    delegatingStream.setDelegate(buffer)
  }
}

object BufferingPrintStream {
  def apply(out: OutputStream) = new BufferingPrintStream(new DelegatingOutputStream(out))
}