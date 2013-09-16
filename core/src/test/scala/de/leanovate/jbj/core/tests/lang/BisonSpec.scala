/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class BisonSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "bison" should {
    "Bison weirdness" in {
      // lang/bison1.phpt
      script(
        """<?php
          |error_reporting(E_ALL & ~E_NOTICE);
          |echo "blah-$foo\n";
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """blah-
          | """.stripMargin
      )
    }
  }
}
