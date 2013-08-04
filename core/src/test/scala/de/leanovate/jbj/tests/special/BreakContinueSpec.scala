package de.leanovate.jbj.tests.special

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

@RunWith(classOf[JUnitRunner])
class BreakContinueSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Break/continue" - {
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
          |Fatal error: Cannot break/continue 1 level in - on line 5
          |""".stripMargin
      ) and haveThrown(classOf[FatalErrorJbjException]))
    }
  }
}
