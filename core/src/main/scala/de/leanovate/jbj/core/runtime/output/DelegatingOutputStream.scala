package de.leanovate.jbj.core.runtime.output

import java.io.{FilterOutputStream, OutputStream}

class DelegatingOutputStream(delegate: OutputStream) extends FilterOutputStream(delegate) {
  def setDelegate(delegate: OutputStream) {
    flush()
    out = delegate
  }
}
