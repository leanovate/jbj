package de.leanovate.jbj.bcmath.tests.basic

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.bcmath.tests.TestJbjExecutor

class BcAddSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic test 1" should {
    "bcadd() function" in {
      // php-src/ext/bcmath/tests/bcadd.phpt
      script(
        """<?php
          |echo bcadd("1", "2"),"\n";
          |echo bcadd("-1", "5", 4),"\n";
          |echo bcadd("1928372132132819737213", "8728932001983192837219398127471", 2),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """3
          |4.0000
          |8728932003911564969352217864684.00
          |""".stripMargin
      )
    }

    "bcadd() incorrect argument count" in {
      // php-src/ext/bcmath/tests/bcadd_error1.phpt
      script(
        """<?php
          |echo bcadd();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcadd() expects at least 2 parameters, 0 given in /basic/BcAddSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    "bcadd() with non-integers" in {
      // php-src/ext/bcmath/tests/bcadd_variation001.phpt
      script(
        """<?php
          |echo bcadd("2.2", "4.3", "2")."\n";
          |echo bcadd("2.2", "-7.3", "1")."\n";
          |echo bcadd("-4.27", "7.3");
          |?>
          |""".stripMargin
      ).withBcScaleFactor(5).result must haveOutput(
        """6.50
          |-5.1
          |3.03000""".stripMargin
      )
    }


  }
}
