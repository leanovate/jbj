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
  }

}
