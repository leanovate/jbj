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
      // classes/private_003.phpt
      script(
        """<?php
          |ini_set("error_reporting",2039);
          |class pass {
          |	private static function show() {
          |		echo "Call show()\n";
          |	}
          |
          |	protected static function good() {
          |		pass::show();
          |	}
          |}
          |
          |class fail extends pass {
          |	static function ok() {
          |		pass::good();
          |	}
          |
          |	static function not_ok() {
          |		pass::show();
          |	}
          |}
          |
          |fail::ok();
          |fail::not_ok(); // calling a private function
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

    "ZE2 A private method cannot be called in a derived class" in {
      // classes/private_004.phpt
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
          |class fail extends pass {
          |	static function do_show() {
          |		fail::show();
          |	}
          |}
          |
          |pass::do_show();
          |fail::do_show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call show()
          |
          |Fatal error: Call to private method pass::show() from context 'fail' in /classes/PrivateSpec.inlinePhp on line 15
          |""".stripMargin
      )
    }

    "ZE2 A private method cannot be called in a derived class" in {
      // classes/private_004b.phpt
      script(
        """<?php
          |
          |class pass {
          |	private function show() {
          |		echo "Call show()\n";
          |	}
          |
          |	public function do_show() {
          |		$this->show();
          |	}
          |}
          |
          |class fail extends pass {
          |	function do_show() {
          |		$this->show();
          |	}
          |}
          |
          |$t = new pass();
          |$t->do_show();
          |
          |$t2 = new fail();
          |$t2->do_show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call show()
          |
          |Fatal error: Call to private method pass::show() from context 'fail' in /classes/PrivateSpec.inlinePhp on line 15
          |""".stripMargin
      )
    }

    "ZE2 A private method cannot be called in a derived class" in {
      // classes/private_005.phpt
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
          |class fail extends pass {
          |	static function do_show() {
          |		pass::show();
          |	}
          |}
          |
          |pass::do_show();
          |fail::do_show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call show()
          |
          |Fatal error: Call to private method pass::show() from context 'fail' in /classes/PrivateSpec.inlinePhp on line 15
          |""".stripMargin
      )
    }

    "ZE2 A private method cannot be called in a derived class" in {
      // classes/private_005b.phpt
      script(
        """<?php
          |
          |class pass {
          |	private function show() {
          |		echo "Call show()\n";
          |	}
          |
          |	public function do_show() {
          |		$this->show();
          |	}
          |}
          |
          |class fail extends pass {
          |	function do_show() {
          |		$this->show();
          |	}
          |}
          |
          |$t = new pass();
          |$t->do_show();
          |
          |$t2 = new fail();
          |$t2->do_show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call show()
          |
          |Fatal error: Call to private method pass::show() from context 'fail' in /classes/PrivateSpec.inlinePhp on line 15
          |""".stripMargin
      )
    }

    "ZE2 A private method can be overwritten in a second derived class" in {
      // classes/private_006.phpt
      script(
        """<?php
          |class first {
          |	private static function show() {
          |		echo "Call show()\n";
          |	}
          |
          |	public static function do_show() {
          |		first::show();
          |	}
          |}
          |
          |first::do_show();
          |
          |class second extends first {
          |}
          |
          |second::do_show();
          |
          |class third extends second {
          |}
          |
          |third::do_show();
          |
          |class fail extends third {
          |	static function show() {  // cannot be redeclared
          |		echo "Call show()\n";
          |	}
          |}
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call show()
          |Call show()
          |Call show()
          |Done
          |""".stripMargin
      )
    }
  }
}
