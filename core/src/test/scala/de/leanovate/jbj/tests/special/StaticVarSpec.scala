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

    "Another static variable example" in {
      script(
        """<?php
          |
          |$a = 10;
          |echo "First: $a\n";
          |
          |static $a = 20;
          |echo "Second: $a\n";
          |
          |if ( $a < 35 ) {
          |	static $a = 30;
          |	echo "Third: $a\n";
          |} else {
          |	static $a = 40;
          |	echo "Fourth: $a\n";
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """First: 10
          |Second: 40
          |Fourth: 40
          |""".stripMargin
      )
    }
  }
}
