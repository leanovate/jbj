package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class LangSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Language test" - {
    "Simple If condition test" in {
      resultOf(
        """<?php $a=1; if($a>0) { echo "Yes"; } ?>"""
      ) must be(
        """Yes"""
      )
    }
  }
}
