package de.leanovate.jbj.tests.lang.operators

import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class NegateSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Negate" - {
    "Test -N operator : various numbers as strings" in {
      resultOf(
        """<?php
          |
          |$strVals = array(
          |   "0","65","-44", "1.2", "-7.7", "abc", "123abc", "123e5", "123e5xyz", " 123abc", "123 abc", "123abc ", "3.4a",
          |   "a5.9"
          |);
          |
          |
          |foreach ($strVals as $strVal) {
          |   echo "--- testing: '$strVal' ---\n";
          |   var_dump(-$strVal);
          |}
          |
          |?>""".stripMargin
      ) must be(
        """--- testing: '0' ---
          |int(0)
          |--- testing: '65' ---
          |int(-65)
          |--- testing: '-44' ---
          |int(44)
          |--- testing: '1.2' ---
          |float(-1.2)
          |--- testing: '-7.7' ---
          |float(7.7)
          |--- testing: 'abc' ---
          |int(0)
          |--- testing: '123abc' ---
          |int(-123)
          |--- testing: '123e5' ---
          |float(-1.23E7)
          |--- testing: '123e5xyz' ---
          |float(-1.23E7)
          |--- testing: ' 123abc' ---
          |int(-123)
          |--- testing: '123 abc' ---
          |int(-123)
          |--- testing: '123abc ' ---
          |int(-123)
          |--- testing: '3.4a' ---
          |float(-3.4)
          |--- testing: 'a5.9' ---
          |int(0)
          |""".stripMargin
      )
    }
  }
}
