/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class StaticMixSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "static mix" should {
    "ZE2 You cannot overload a static method with a non static method" in {
      // classes/static_mix_1.phpt
      script(
        """<?php
          |
          |class pass {
          |	static function show() {
          |		echo "Call to function pass::show()\n";
          |	}
          |}
          |
          |class fail extends pass {
          |	function show() {
          |		echo "Call to function fail::show()\n";
          |	}
          |}
          |
          |pass::show();
          |fail::show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot make static method pass::show() non static in class fail in /classes/StaticMixSpec.inlinePhp on line 10
          |""".stripMargin
      )
    }

    "ZE2 You cannot overload a non static method with a static method" in {
      // classes/static_mix_2.phpt
      script(
        """<?php
          |
          |class pass {
          |	function show() {
          |		echo "Call to function pass::show()\n";
          |	}
          |}
          |
          |class fail extends pass {
          |	static function show() {
          |		echo "Call to function fail::show()\n";
          |	}
          |}
          |
          |$t = new pass();
          |$t->show();
          |fail::show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot make non static method pass::show() static in class fail in /classes/StaticMixSpec.inlinePhp on line 10
          |""".stripMargin
      )
    }
  }
}
