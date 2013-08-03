package de.leanovate.jbj.tests.parsing

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import org.scalatest.matchers.MustMatchers
import de.leanovate.jbj.tests.TestJbjExecutor

@RunWith(classOf[JUnitRunner])
class PrecedenceSpec extends FreeSpec with MustMatchers with TestJbjExecutor {
  "Precedence test" - {
    "mul/div befor add/sub" in {
      script(
        """<?php
          |
          |var_dump(3*4+5*6);
          |var_dump(4*6-4/5);
          |var_dump(3*(1+2)+8/5);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """int(42)
          |float(23.2)
          |float(10.6)
          |""".stripMargin
      )
    }
  }
}
