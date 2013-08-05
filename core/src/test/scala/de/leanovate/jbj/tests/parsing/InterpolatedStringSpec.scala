package de.leanovate.jbj.tests.parsing

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class InterpolatedStringSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Interpolated string" - {
    "Simple variable" in {
      script(
        """<?php
          |$a=123;
          |$b="Test";
          |
          |echo "Int: $a Str: $b";
          |?>""".stripMargin
      ).result must haveOutput(
        """Int: 123 Str: Test"""
      )
    }

    "Propery ref" in {
      script(
        """<?php
          |
          |$obj=new stdClass;
          |$obj->a=123;
          |$obj->b="Test";
          |
          |echo "Int: $obj->a Str: $obj->b";
          |?>""".stripMargin
      ).result must haveOutput(
        """Int: 123 Str: Test"""
      )
    }
  }
}
