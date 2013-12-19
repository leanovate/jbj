package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcScaleSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcscale" should {

    "bcscale() function" in {
      // php-src/ext/bcmath/tests/bcscale.phpt
      script(
        """<?php
          |echo bcadd("1", "2"),"\n";
          |bcscale(2);
          |echo bcadd("1", "2"),"\n";
          |bcscale(10);
          |echo bcadd("1", "2"),"\n";
          |bcscale(0);
          |echo bcadd("1", "2"),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """3
          |3.00
          |3.0000000000
          |3
          |""".stripMargin
      )
    }

    "bcscale() with negative argument" in {
      // php-src/ext/bcmath/tests/bcscale_variation001.phpt
      script(
        """<?php
          |bcscale(-4);
          |echo bcdiv("20.56", "4");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """5""".stripMargin
      )
    }

    "bcadd() incorrect argument count" in {
      // php-src/ext/bcmath/tests/bcscale_variation002.phpt
      script(
        """<?php
          |echo bcadd("-4.27", "7.3");
          |?>
          |""".stripMargin
      ).withBcScaleFactor(-2).result must haveOutput(
        """3""".stripMargin
      )
    }

  }
}
