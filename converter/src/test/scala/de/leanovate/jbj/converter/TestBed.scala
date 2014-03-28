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
        |function specialSum($a, $b) {
        |    if ($a < 10) {
        |        return 2 * $a + $b;
        |    } else {
        |        return $a + $b;
        |    }
        |}
        |
        |function specialLoop($a, $count) {
        |    $i = 1;
        |    while($i < $count) {
        |        $a += $i;
        |        ++$i;
        |    }
        |    return $a;
        |}
        |echo specialSum(2, 3);
        |echo "\n";
        |echo specialSum(6, 7);
        |echo "\n";
        |echo specialSum(12, 13);
        |echo "\n";
        |echo specialLoop(2, 5);
        |echo "\n";
        |echo specialLoop(12, 13);
        |echo "\n";
        |?>""".stripMargin

    val out = new StringWriter()

    transcoder.toCodeUnit("test.php", scropt, None, out)

    println(out.toString)
  }
}
