package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcMulSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcmul" should {
    "bcmul() function" in {
      // php-src/ext/bcmath/tests/bcmul.phpt
      script(
        """<?php
          |echo bcmul("1", "2"),"\n";
          |echo bcmul("-3", "5"),"\n";
          |echo bcmul("1234567890", "9876543210"),"\n";
          |echo bcmul("2.5", "1.5", 2),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """2
          |-15
          |12193263111263526900
          |3.75
          |""".stripMargin
      )
    }

    "bcmul() incorrect argument count" in {
      // php-src/ext/bcmath/tests/bcmul_error1.phpt
      script(
        """<?php
          |echo bcmul();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcmul() expects at least 2 parameters, 0 given in /basic/BcMulSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

  }
}
