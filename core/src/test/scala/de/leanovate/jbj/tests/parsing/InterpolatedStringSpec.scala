package de.leanovate.jbj.tests.parsing

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class InterpolatedStringSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Interpolated string" should {
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
