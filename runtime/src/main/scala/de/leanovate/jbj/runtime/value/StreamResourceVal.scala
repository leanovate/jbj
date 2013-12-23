package de.leanovate.jbj.runtime.value

import java.nio.channels.SeekableByteChannel
import de.leanovate.jbj.runtime.context.Context

class StreamResourceVal(id: Long, stream: SeekableByteChannel) extends ResourceVal[SeekableByteChannel](id, "stream", stream) {
  def isOpen = stream.isOpen

  def close() {
    stream.close()
  }
}

object StreamResourceVal {
  def apply(stream: SeekableByteChannel)(implicit ctx: Context): StreamResourceVal =
    new StreamResourceVal(ctx.global.resourceCounter.incrementAndGet(), stream)

  def unapply(resource: StreamResourceVal) = Some(resource.payload)
}