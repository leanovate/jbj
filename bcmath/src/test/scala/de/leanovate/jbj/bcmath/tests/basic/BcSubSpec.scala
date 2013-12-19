package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcSubSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcsub" should {

    "bcsub() function" in {
      // php-src/ext/bcmath/tests/bcsub.phpt
      script(
        """<?php
          |echo bcsub("1", "2"),"\n";
          |echo bcsub("-1", "5", 4),"\n";
          |echo bcsub("8728932001983192837219398127471", "1928372132132819737213", 2),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """-1
          |-6.0000
          |8728932000054820705086578390258.00
          |""".stripMargin
      )
    }

    "bcsub() incorrect argument count" in {
      // php-src/ext/bcmath/tests/bcsub_error1.phpt
      script(
        """<?php
          |echo bcsub();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcsub() expects at least 2 parameters, 0 given in /basic/BcSubSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

  }
}
