package de.leanovate.jbj.runtime.env

import java.io.InputStream

case class LimitedByteStreams(limit: Long) {
  def read(in: InputStream): Either[Array[Byte], Long] = {
    val builder = Array.newBuilder[Byte]
    val buffer = new Array[Byte](8192)
    var total: Long = 0
    var readed: Int = 0
    try {
      readed = in.read(buffer)
      while (readed > 0) {
        if (total <= limit || limit < 0)
          builder ++= buffer.slice(0, readed)
        total += readed
        readed = in.read(buffer)
      }
      if (total <= limit || limit < 0)
        Left(builder.result())
      else
        Right(total)
    }
    finally {
      in.close()
    }
  }
}
