package de.leanovate.jbj.tests.parsing

import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class HeredocSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Heredoc" - {
    "Static heredoc" in {
      script(
        """<?php
          |
          |$a=<<<END
          |First line
          |Second line
          |END;
          |
          |echo $a;
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """First line
          |Second line""".stripMargin
      )
    }

    "Simple variable interpolation" in {
      script(
        """<?php
          |
          |$a=123;
          |$b="Test";
          |
          |$c=<<<SOMETHING
          |Int: $a
          |Str: $b
          |SOMETHING;
          |
          |echo $c;
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Int: 123
          |Str: Test""".stripMargin
      )
    }
  }
}
