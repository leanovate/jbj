package de.leanovate.jbj.transcode

import de.leanovate.jbj.api.http.JbjSettings
import java.io.StringWriter

object TestBed {
  def main(args: Array[String]) {
    val settings = new JbjSettings
    val transcoder = new Transcoder(settings)

    val scropt =
      """This is before
        |<?php
        |print "Hello world";
        |?>
        |This is after
        |""".stripMargin

    val out = new StringWriter()

    transcoder.toCodeUnit("test.php", scropt, None, out)

    println(out.toString)
  }
}
