/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace9Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace tests 080-" should {
    "080: bracketed namespaces and __HALT_COMPILER();" in {
      // ../php-src/Zend/tests/ns_080.phpt
      script(
        """<?php
          |namespace foo {
          |echo "hi\n";
          |}
          |__HALT_COMPILER();
          |namespace unprocessed {
          |echo "should not echo\n";
          |}
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """hi
          |""".stripMargin
      )
    }
  }

}
