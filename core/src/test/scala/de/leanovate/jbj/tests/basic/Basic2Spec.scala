package de.leanovate.jbj.tests.basic

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class Basic2Spec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Basic test 2" - {
    "Testing $argc and $argv handling (GET)" in {
      // basic/011
      script(
        """<?php
          |$argc = $_SERVER['argc'];
          |$argv = $_SERVER['argv'];
          |
          |for ($i=0; $i<$argc; $i++) {
          |	echo "$i: ".$argv[$i]."\n";
          |}
          |
          |?>""".stripMargin
      ).withGet("?ab+cd+ef+123+test").result must haveOutput(
        """0: ab
          |1: cd
          |2: ef
          |3: 123
          |4: test
          |""".stripMargin
      )
    }
  }
}
