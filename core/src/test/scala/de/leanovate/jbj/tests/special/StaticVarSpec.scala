package de.leanovate.jbj.tests.special

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class StaticVarSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Static variable" should {
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
