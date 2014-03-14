package de.leanovate.jbj.converter

import de.leanovate.jbj.api.http.JbjSettings
import java.io.StringWriter

object TestBed {
  def main(args: Array[String]) {
    val settings = new JbjSettings
    val transcoder = new Transcoder(settings)

    val scropt =
      """<?php
        |    $a = array("Hello", "World", 42);
        |
        |    for($i = 0; $i < count($a); $i++) {
        |        echo $a[$i];
        |        echo "\n";
        |    }
        |?>""".stripMargin

    val out = new StringWriter()

    transcoder.toCodeUnit("test.php", scropt, None, out)

    println(out.toString)
  }
}
