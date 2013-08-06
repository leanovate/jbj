package de.leanovate.jbj.tests.special

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class StaticVarSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Static variable" - {
    "Static variable declaration crazyness" in {
      script(
        """<?php
          |
          |$a=10;
          |
          |var_dump($a);
          |
          |static $a=20;
          |
          |var_dump($a);
          |
          |static $a=30;
          |
          |var_dump($a);
          |
          |if(0) {
          |	static $a=40;
          |
          |	var_dump($a);
          |}
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """int(10)
          |int(40)
          |int(40)
          |""".stripMargin
      )
    }
  }
}
