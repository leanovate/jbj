/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.output

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class BugSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs" should {
    "Bug #60321 (ob_get_status(true) no longer returns an array when buffer is empty)" in {
      // output/bug60321.phpt
      script(
        """<?php
          |$return = ob_get_status(true);
          |var_dump($return);
          |""".stripMargin
      ).result must haveOutput(
        """array(0) {
          |}
          |""".stripMargin
      )
    }

    "Bug #60322 (ob_get_clean() now raises an E_NOTICE if no buffers exist)" in {
      // output/bug60322.phpt
      script(
        """<?php
          |ob_start();
          |while(@ob_end_clean());
          |var_dump(ob_get_clean());
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |""".stripMargin
      )
    }
  }
}
