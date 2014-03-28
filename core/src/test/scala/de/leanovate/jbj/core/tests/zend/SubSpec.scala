/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class SubSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Subtraction" should {
    "subtracting arrays" in {
      // ../php-src/Zend/tests/sub_001.phpt
      script(
        """<?php
          |
          |$a = array(1,2,3);
          |$b = array(1);
          |
          |$c = $a - $b;
          |var_dump($c);
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must
        haveOutput(
          """
            |Fatal error: Unsupported operand types in /zend/SubSpec.inlinePhp on line 6
            |""".
            stripMargin
        )
    }
  }
}
