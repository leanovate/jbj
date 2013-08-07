package de.leanovate.jbj.tests.lang

import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Bug24xxxSpec extends FreeSpec with TestJbjExecutor with MustMatchers{
  "Bugs #24xxx" - {
    "Bug #24396 (global $$variable broken)" in {
      // lang/bug24396
      script(
        """<?php
          |
          |$arr = array('a' => 1, 'b' => 2, 'c' => 3);
          |
          |foreach($arr as $k=>$v)  {
          |    global $$k; // comment this out and it works in PHP 5 too..
          |
          |    echo "($k => $v)\n";
          |
          |    $$k = $v;
          |}
          |echo "a:$a\n";
          |echo "b:$b\n";
          |echo "c:$c\n";
          |echo "d:$d\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """(a => 1)
          |(b => 2)
          |(c => 3)
          |a:1
          |b:2
          |c:3
          |d:
          |""".stripMargin
      )
    }
  }
}
