package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcPowModSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bcpowmod" should {

    "bcpowmod() - Raise an arbitrary precision number to another, reduced by a specified modulus" in {
      // php-src/ext/bcmath/tests/bcpowmod.phpt
      script(
        """<?php
          |echo bcpowmod("5", "2", "7") . "\n";
          |echo bcpowmod("-2", "5", "7") . "\n";
          |echo bcpowmod("10", "2147483648", "2047");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """4
          |-4
          |790""".stripMargin
      )
    }

    "bcpowmod — Raise an arbitrary precision number to another, reduced by a specified modulus" in {
      // php-src/ext/bcmath/tests/bcpowmod_error1.phpt
      script(
        """<?php
          |echo bcpowmod('1');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcpowmod() expects at least 3 parameters, 1 given in /basic/BcPowModSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    "bcpowmod — Raise an arbitrary precision number to another, reduced by a specified modulus" in {
      // php-src/ext/bcmath/tests/bcpowmod_error2.phpt
      script(
        """<?php
          |echo bcpowmod('1', '2');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcpowmod() expects at least 3 parameters, 2 given in /basic/BcPowModSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    "bcpowmod — Raise an arbitrary precision number to another, reduced by a specified modulus" in {
      // php-src/ext/bcmath/tests/bcpowmod_error3.phpt
      script(
        """<?php
          |echo bcpowmod('1', '2', '3', '4', '5');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: bcpowmod() expects at most 4 parameters, 5 given in /basic/BcPowModSpec.inlinePhp on line 2
          |""".stripMargin
      )
    }

  }
}
