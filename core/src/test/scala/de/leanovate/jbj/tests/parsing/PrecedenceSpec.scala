package de.leanovate.jbj.tests.parsing

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class PrecedenceSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Precedence test" should {
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
