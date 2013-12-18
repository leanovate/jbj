package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcDivSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcdiv" should {

    "bcdiv() function" in {
      // php-src/ext/bcmath/tests/bcdiv.phpt
      script(
        """<?php
          |echo bcdiv("1", "2"),"\n";
          |echo bcdiv("1", "2", 2),"\n";
          |echo bcdiv("-1", "5", 4),"\n";
          |echo bcdiv("8728932001983192837219398127471", "1928372132132819737213", 2),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """0
          |0.50
          |-0.2000
          |4526580661.75
          |""".stripMargin
      )
    }

    "bcdiv — Divide two arbitrary precision numbers" in {
      // php-src/ext/bcmath/tests/bcdiv_error1.phpt
      script(
        """<?php
          |echo bcdiv('10.99', '0');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcdiv(): Division by zero in /basic/BcDivSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    /*
    "bcdiv — Divide two arbitrary precision numbers" in {
      // php-src/ext/bcmath/tests/bcdiv_error2.phpt
      script(
        """<?php
          |echo bcdiv('1', '2', 3, '4');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcdiv() expects at most 3 parameters, 4 given in /basic/BcDivSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }
    */


  }
}
