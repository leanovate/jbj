package de.leanovate.jbj.converter

import de.leanovate.jbj.api.http.JbjSettings
import java.io.StringWriter

object TestBed {
  def main(args: Array[String]) {
    val settings = new JbjSettings
    val transcoder = new Transcoder(settings)

    val scropt =
      """<?php
        |    $a = "Hello";
        |    $b = "world";
        |    $c = $a . " " . $b;
        |
        |    echo $c;
        |?>""".stripMargin

    val out = new StringWriter()

    transcoder.toCodeUnit("test.php", scropt, None, out)

    println(out.toString)
  }
}
