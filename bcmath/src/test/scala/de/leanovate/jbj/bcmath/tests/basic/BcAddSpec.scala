package de.leanovate.jbj.bcmath.tests.basic

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.bcmath.tests.TestJbjExecutor

class BcAddSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Basic test 1" should {
    "bcadd() function" in {
      script(
        """<?php echo bcadd("12", "12"); ?>"""
      ).result must haveOutput(
        """1234"""
      )
    }
  }
}
