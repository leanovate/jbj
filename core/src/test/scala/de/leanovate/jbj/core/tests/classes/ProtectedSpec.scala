package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class ProtectedSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "protected members" should {
    "ZE2 A protected method can only be called inside the class" in {
      // classes/protected_001.phpt
      script(
        """<?php
          |
          |class pass {
          |	protected static function fail() {
          |		echo "Call fail()\n";
          |	}
          |
          |	public static function good() {
          |		pass::fail();
          |	}
          |}
          |
          |pass::good();
          |pass::fail();// must fail because we are calling from outside of class pass
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call fail()
          |
          |Fatal error: Call to protected method pass::fail() from context '' in /classes/ProtectedSpec.inlinePhp on line 14
          |""".stripMargin
      )
    }

    "ZE2 A protected method can only be called inside the class" in {
      // classes/protected_001b.phpt
      script(
        """<?php
          |
          |class pass {
          |	protected function fail() {
          |		echo "Call fail()\n";
          |	}
          |
          |	public function good() {
          |		$this->fail();
          |	}
          |}
          |
          |$t = new pass();
          |$t->good();
          |$t->fail();// must fail because we are calling from outside of class pass
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call fail()
          |
          |Fatal error: Call to protected method pass::fail() from context '' in /classes/ProtectedSpec.inlinePhp on line 15
          |""".stripMargin
      )
    }

    "ZE2 A protected method cannot be called in another class" in {
      // classes/protected_002.phpt
      script(
        """<?php
          |
          |class pass {
          |	protected static function show() {
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
          |Fatal error: Call to protected method pass::show() from context 'fail' in /classes/ProtectedSpec.inlinePhp on line 18
          |""".stripMargin
      )
    }
  }
}
