/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class PrivateSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "private members" should {
    "ZE2 A private method can only be called inside the class" in {
      // classes/private_001.phpt
      script(
        """<?php
          |
          |class pass {
          |	private static function show() {
          |		echo "Call show()\n";
          |	}
          |
          |	public static function do_show() {
          |		pass::show();
          |	}
          |}
          |
          |pass::do_show();
          |pass::show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call show()
          |
          |Fatal error: Call to private method pass::show() from context '' in /classes/PrivateSpec.inlinePhp on line 14
          |""".stripMargin
      )
    }

    "ZE2 A private method cannot be called in another class" in {
      // classes/private_002.phpt
      script(
        """<?php
          |
          |class pass {
          |	private static function show() {
          |		echo "Call pass::show()\n";
          |	}
          |
          |	public static function do_show() {
          |		pass::show();
          |	}
          |}
          |
          |pass::do_show();
          |
          |class fail {
          |	public static function show() {
          |		echo "Call fail::show()\n";
          |		pass::show();
          |	}
          |}
          |
          |fail::show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call pass::show()
          |Call fail::show()
          |
          |Fatal error: Call to private method pass::show() from context 'fail' in /classes/PrivateSpec.inlinePhp on line 18
          |""".stripMargin
      )
    }

    "ZE2 A private method cannot be called in a derived class" in {
      // classes/private_003b.phpt
      script(
        """<?php
          |
          |class pass {
          |	private function show() {
          |		echo "Call show()\n";
          |	}
          |
          |	protected function good() {
          |		$this->show();
          |	}
          |}
          |
          |class fail extends pass {
          |	public function ok() {
          |		$this->good();
          |	}
          |
          |	public function not_ok() {
          |		$this->show();
          |	}
          |}
          |
          |$t = new fail();
          |$t->ok();
          |$t->not_ok(); // calling a private function
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call show()
          |
          |Fatal error: Call to private method pass::show() from context 'fail' in /classes/PrivateSpec.inlinePhp on line 19
          |""".stripMargin
      )
    }
  }
}
