/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class ThisSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "$this" should {
    "ZE2 $this can be an argument to a static function" in {
      // classes/static_this.phpt
      script(
        """<?php
          |
          |class TestClass
          |{
          |	function __construct()
          |	{
          |		self::Test1();
          |		$this->Test1();
          |	}
          |
          |	static function Test1()
          |	{
          |		var_dump($this);
          |	}
          |
          |	static function Test2($this)
          |	{
          |		var_dump($this);
          |	}
          |}
          |
          |$obj = new TestClass;
          |TestClass::Test2(new stdClass);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Undefined variable: this in /classes/ThisSpec.inlinePhp on line 13
          |NULL
          |
          |Notice: Undefined variable: this in /classes/ThisSpec.inlinePhp on line 13
          |NULL
          |object(stdClass)#2 (0) {
          |}
          |===DONE===
          |""".stripMargin
      )
    }
  }
}
