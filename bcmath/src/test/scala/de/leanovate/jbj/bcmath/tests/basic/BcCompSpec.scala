package de.leanovate.jbj.bcmath.tests.basic

import de.leanovate.jbj.bcmath.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class BcCompSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bccomp" should {
    "bccomp() function" in {
      // php-src/ext/bcmath/tests/bccomp.phpt
      script(
        """<?php
          |echo bccomp("-1", "5", 4),"\n";
          |echo bccomp("1928372132132819737213", "8728932001983192837219398127471"),"\n";
          |echo bccomp("1.00000000000000000001", "1", 2),"\n";
          |echo bccomp("97321", "2321"),"\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """-1
          |-1
          |0
          |1
          |""".stripMargin
      )
    }

    "bccomp() with non-integers" in {
      // php-src/ext/bcmath/tests/bccomp_variation001.phpt
      script(
        """<?php
          |echo bccomp("2.2", "2.2", "2")."\n";
          |echo bccomp("2.32", "2.2", "2")."\n";
          |echo bccomp("2.29", "2.3", "2");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """0
          |1
          |-1""".stripMargin
      )
    }

    "bccomp() with negative value" in {
      // php-src/ext/bcmath/tests/bccomp_variation002.phpt
      script(
        """<?php
          |echo bccomp("-2", "-2")."\n";
          |echo bccomp("-2", "2", "1")."\n";
          |echo bccomp("-2.29", "-2.3", "2");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """0
          |-1
          |1""".stripMargin
      )
    }

  }
}
