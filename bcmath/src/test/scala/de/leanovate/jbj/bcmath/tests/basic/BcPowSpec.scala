package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcPowSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcpow" should {
    "bcpow() function" in {
      // php-src/ext/bcmath/tests/bcpow.phpt
      script(
        """<?php
          |echo bcpow("1", "2"),"\n";
          |echo bcpow("-2", "5", 4),"\n";
          |echo bcpow("2", "64"),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """1
          |-32
          |18446744073709551616
          |""".stripMargin
      )
    }

    "bcpow() with a negative exponent" in {
      // php-src/ext/bcmath/tests/bcpow_variation001.phpt
      script(
        """<?php
          |echo bcpow("2", "-4");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """0""".stripMargin
      )
    }

    "bcpow() incorrect argument count" in {
      // /home/simon/projects/php-src/ext/bcmath/tests/bcpow_error3.phpt
      script(
        """<?php
          |echo bcpow();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcpow() expects at least 2 parameters, 0 given in /basic/BcPowSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

  }
}
