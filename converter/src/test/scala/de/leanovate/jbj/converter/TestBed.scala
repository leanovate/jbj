package de.leanovate.jbj.converter

import de.leanovate.jbj.api.http.JbjSettings
import java.io.StringWriter

object TestBed {
  def main(args: Array[String]) {
    val settings = new JbjSettings
    val transcoder = new Transcoder(settings)

    val scropt =
      """<?php
        |
        |function sum2($a, $b) {
        |   echo "In sum2\n";
        |   return $a + $b;
        |}
        |
        |    $a = array("Hello", "World", 42);
        |
        |sum2(1,2);
        |    for($i = 0; $i < count($a); $i++) {
        |        echo $a[$i];
        |        echo "\n";
        |    }
        |    if($a) {
        |       echo "Bla\n";
        |    } elseif ( 1 ) {
        |       echo "nix";
        |    } else {
        |       echo "Blub\n";
        |    }
        |?>""".stripMargin

    val out = new StringWriter()

    transcoder.toCodeUnit("test.php", scropt, None, out)

    println(out.toString)
  }
}
