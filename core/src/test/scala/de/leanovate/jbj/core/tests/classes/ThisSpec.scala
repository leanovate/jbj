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

    "ZE2 $this cannot be exchanged" in {
      // classes/this.phpt
      script(
        """<?php
          |
          |/* please don't shorten this test. It shows what would happen if
          | * the fatal error would have been a warning.
          | */
          |class Foo
          |{
          |    function replace($other)
          |    {
          |    	echo __METHOD__ . "\n";
          |        $this = $other;
          |        print $this->prop;
          |        print $other->prop;
          |    }
          |
          |    function indirect($other)
          |    {
          |    	echo __METHOD__ . "\n";
          |        $this = $other;
          |        $result = $this = $other;
          |        print $result->prop;
          |        print $this->prop;
          |    }
          |
          |    function retrieve(&$other)
          |    {
          |    	echo __METHOD__ . "\n";
          |    	$other = $this;
          |    }
          |}
          |
          |$object = new Foo;
          |$object->prop = "Hello\n";
          |
          |$other  = new Foo;
          |$other->prop = "World\n";
          |
          |$object->replace($other);
          |$object->indirect($other);
          |
          |print $object->prop; // still shows 'Hello'
          |
          |$object->retrieve($other);
          |print $other->prop;  // shows 'Hello'
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot re-assign $this in /classes/ThisSpec.inlinePhp on line 8
          |""".stripMargin
      )
    }
  }
}
