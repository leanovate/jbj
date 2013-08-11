package de.leanovate.jbj.tests.special

import de.leanovate.jbj.tests.TestJbjExecutor
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException
import org.specs2.mutable.SpecificationWithJUnit

class BreakContinueSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Break/continue" should {
    "break in function" in {
      script(
        """<?php
          |
          |function bla() {
          |  echo "First\n";
          |  break;
          |  echo "Error\n";
          |}
          |
          |bla();
          |?>""".stripMargin
      ).result must (haveOutput(
        """First
          |
          |Fatal error: Cannot break/continue 1 level in /special/BreakContinueSpec.inlinePhp on line 5
          |""".stripMargin
      ) and haveThrown(classOf[FatalErrorJbjException]))
    }
  }
}
