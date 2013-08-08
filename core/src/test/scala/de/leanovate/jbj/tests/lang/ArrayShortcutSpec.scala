package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class ArrayShortcutSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Array shortcut" - {
    "Square bracket array shortcut test 1" in {
      // lang/array_shortcut_001
      script(
        """<?php
          |print_r([1, 2, 3]);
          |?>""".stripMargin
      ).result must haveOutput(
        """Array
          |(
          |    [0] => 1
          |    [1] => 2
          |    [2] => 3
          |)
          |""".stripMargin
      )
    }

    "Square bracket associative array shortcut test 2" in {
      // lang/array_shortcut_002
      script(
        """<?php
          |print_r(["foo" => "orange", "bar" => "apple", "baz" => "lemon"]);
          |?>""".stripMargin
      ).result must haveOutput(
        """Array
          |(
          |    [foo] => orange
          |    [bar] => apple
          |    [baz] => lemon
          |)
          |""".stripMargin
      )
    }

    "Testing array shortcut and bracket operator" in {
      // lang/array_shortcut_003
      script(
        """<?php
          |$a = [1, 2, 3, 4, 5];
          |print_r([$a[1], $a[3]]);
          |?>""".stripMargin
      ).result must haveOutput(
        """Array
          |(
          |    [0] => 2
          |    [1] => 4
          |)
          |""".stripMargin
      )
    }

    "Testing nested array shortcut" in {
      // lang/array_shortcut_005
      script(
        """<?php
          |print_r([1, 2, 3, ["foo" => "orange", "bar" => "apple", "baz" => "lemon"]]);
          |?>""".stripMargin
      ).result must haveOutput(
        """Array
          |(
          |    [0] => 1
          |    [1] => 2
          |    [2] => 3
          |    [3] => Array
          |        (
          |            [foo] => orange
          |            [bar] => apple
          |            [baz] => lemon
          |        )
          |
          |)
          |""".stripMargin
      )
    }
  }
}
