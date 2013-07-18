package de.leanovate.jbj.tests.func

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class FuncSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Function test" - {
    "Strlen() function test" in { // func/001
      resultOf(
        """<?php echo strlen("abcdef")?>"""
      ) must be(
        """6"""
      )
    }

    "Static variables in functions" - { // func/002
      resultOf(
        """<?php
          |function blah()
          |{
          |  static $hey=0,$yo=0;
          |
          |  echo "hey=".$hey++.", ",$yo--."\n";
          |}
          |
          |blah();
          |blah();
          |blah();
          |if (isset($hey) || isset($yo)) {
          |  echo "Local variables became global :(\n";
          |}""".stripMargin
      ) must be(
        """hey=0, 0
          |hey=1, -1
          |hey=2, -2
          |""".stripMargin
      )
    }
  }
}
