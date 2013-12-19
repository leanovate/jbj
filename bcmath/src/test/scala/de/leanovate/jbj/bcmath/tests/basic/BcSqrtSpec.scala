package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcSqrtSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcsqrt" should {

    "bcsqrt() function" in {
      // php-src/ext/bcmath/tests/bcsqrt.phpt
      script(
        """<?php
          |echo bcsqrt("9"),"\n";
          |echo bcsqrt("1928372132132819737213", 5),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """3
          |43913234134.28826
          |""".stripMargin
      )
    }

    "bcsqrt() with argument of 0" in {
      // php-src/ext/bcmath/tests/bcsqrt_variation001.phpt
      script(
        """<?php
          |echo bcsqrt("0");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        "0"
      )
    }

    "bcsqrt — Get the square root of an arbitrary precision number" in {
      // php-src/ext/bcmath/tests/bcsqrt_error1.phpt
      script(
        """<?php
          |echo bcsqrt('-9');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcsqrt(): Square root of negative number in /basic/BcSqrtSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    "bcsqrt() incorrect argument count" in {
      // php-src/ext/bcmath/tests/bcsqrt_error2.phpt
      script(
        """<?php
          |echo bcsqrt();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcsqrt() expects at least 1 parameter, 0 given in /basic/BcSqrtSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

  }
}
