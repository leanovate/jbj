/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class VisibilitySpec extends SpecificationWithJUnit with TestJbjExecutor{
  "visibility" should {
    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_000a.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	protected function f0() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access level to fail::f0() must be public (as in class same) in /classes/VisibilitySpec.inlinePhp on line 22
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_000b.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	private function f0() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access level to fail::f0() must be public (as in class same) in /classes/VisibilitySpec.inlinePhp on line 22
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_000c.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	function f0() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Done
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_001a.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	protected function f1() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access level to fail::f1() must be public (as in class same) in /classes/VisibilitySpec.inlinePhp on line 22
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_001b.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	private function f1() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access level to fail::f1() must be public (as in class same) in /classes/VisibilitySpec.inlinePhp on line 22
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_001c.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	function f1() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Done
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_002a.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	protected function f2() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access level to fail::f2() must be public (as in class same) in /classes/VisibilitySpec.inlinePhp on line 22
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_002b.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	private function f2() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access level to fail::f2() must be public (as in class same) in /classes/VisibilitySpec.inlinePhp on line 22
          |""".stripMargin
      )
    }

    "ZE2 A redeclared method must have the same or higher visibility" in {
      // classes/visibility_002c.phpt
      script(
        """<?php
          |
          |class father {
          |	function f0() {}
          |	function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class same extends father {
          |
          |	// overload fn with same visibility
          |	function f0() {}
          |	public function f1() {}
          |	public function f2() {}
          |	protected function f3() {}
          |	private function f4() {}
          |}
          |
          |class fail extends same {
          |	function f2() {}
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Done
          |""".stripMargin
      )
    }
  }
}
