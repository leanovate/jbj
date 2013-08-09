package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class IncludeSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Include" - {
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
