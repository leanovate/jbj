package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class IncludeSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Include" should {
    "include() a file from the current script directory" in {
      // lang/include_variation1
      script(
        """|<?php
          |include("inc.inc");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Included!
          |""".stripMargin
      )
    }

    "Including a file in the current script directory from an included function" in {
      // lang/include_variation2
      script(
        """<?php
          |require_once 'include_files/function.inc';
          |test();
          |?>""".stripMargin
      ).result must haveOutput(
        """Included!
          |""".stripMargin
      )
    }

    "Including a file in the current script directory from eval'd code" in {
      // lang/include_variation3
      script(
        """<?php
          |require_once 'include_files/eval.inc';
          |?>""".stripMargin
      ).result must haveOutput(
        """Included!
          |""".stripMargin
      )
    }
  }
}
