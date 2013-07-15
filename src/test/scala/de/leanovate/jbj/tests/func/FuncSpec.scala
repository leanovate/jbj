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
  }
}
