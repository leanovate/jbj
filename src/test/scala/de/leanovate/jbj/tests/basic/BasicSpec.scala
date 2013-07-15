package de.leanovate.jbj.tests.basic

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class BasicSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Basic tests:" - {
    "Trivial \"Hello World\" test" in {
      resultOf(
        """<?php echo "Hello World"?>"""
      ) must be(
        """Hello World"""
      )
    }


  }
}
