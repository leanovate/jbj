package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcMocSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcmod" should {
    "bcmod() function" in {
      // php-src/ext/bcmath/tests/bcmod.phpt
      script(
        """<?php
          |echo bcmod("11", "2"),"\n";
          |echo bcmod("-1", "5"),"\n";
          |echo bcmod("8728932001983192837219398127471", "1928372132132819737213"),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """1
          |-1
          |1459434331351930289678
          |""".stripMargin
      )
    }

    "bcmod — Get modulus of an arbitrary precision number" in {
      // php-src/ext/bcmath/tests/bcmod_error1.phpt
      script(
        """<?php
          |echo bcmod('1', '2', '3');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcmod() expects exactly 2 parameters, 3 given in /basic/BcMocSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    "bcmod() - mod by 0" in {
      // php-src/ext/bcmath/tests/bcmod_error2.phpt
      script(
        """<?php
          |echo bcmod("10", "0");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcmod(): Division by zero in /basic/BcMocSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

  }
}
