package de.leanovate.jbj.core.tests.string

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class OffsetsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Offset chaining" should {
    "testing the behavior of string offset chaining" in {
      // strings/offsets_chaining_1.phpt
      script(
        """<?php
          |$string = "foobar";
          |var_dump($string[0][0][0][0]);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(1) "f"
          |""".stripMargin
      )
    }

    "testing the behavior of string offset chaining" in {
      // strings/offsets_chaining_2.phpt
      script(
        """<?php
          |$string = "foobar";
          |var_dump($string{0}{0}[0][0]);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(1) "f"
          |""".stripMargin
      )
    }

    "testing the behavior of string offset chaining" in {
      // strings/offsets_chaining_3.phpt
      script(
        """<?php
          |$string = "foobar";
          |var_dump(isset($string[0][0][0][0]));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |""".stripMargin
      )
    }

    "testing the behavior of string offset chaining" in {
      // strings/offsets_chaining_4.phpt
      script(
        """<?php
          |$string = "foobar";
          |var_dump(isset($string{0}{0}[0][0]));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(true)
          |""".stripMargin
      )
    }
  }
}
