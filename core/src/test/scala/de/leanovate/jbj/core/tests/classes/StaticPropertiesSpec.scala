/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class StaticPropertiesSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Static properties" should {
    "ZE2 Initializing static properties to arrays" in {
      // classes/static_properties_001
      script(
        """<?php
          |
          |class test {
          |	static public $ar = array();
          |}
          |
          |var_dump(test::$ar);
          |
          |test::$ar[] = 1;
          |
          |var_dump(test::$ar);
          |
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """array(0) {
          |}
          |array(1) {
          |  [0]=>
          |  int(1)
          |}
          |Done
          |""".stripMargin
      )
    }
  }
}
