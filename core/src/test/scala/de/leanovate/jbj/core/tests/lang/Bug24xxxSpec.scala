/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Bug24xxxSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs #24xxx" should {
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
          |
          |// This following was not part of the original, but it stresses out the point of this test
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
          |
          |Notice: Undefined variable: d in /lang/Bug24xxxSpec.inlinePhp on line 17
          |d:
          |""".stripMargin
      )
    }
  }
}
