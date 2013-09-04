/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class CatchableErrorSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "catchable error" should {
    "Catchable fatal error [1]" in {
      // lang/catchable_error_001.phpt
      script(
        """<?php
          |	class Foo {
          |	}
          |
          |	function blah (Foo $a)
          |	{
          |	}
          |
          |	function error()
          |	{
          |		$a = func_get_args();
          |		var_dump($a);
          |	}
          |
          |	blah (new StdClass);
          |	echo "ALIVE!\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Catchable fatal error: Argument 1 passed to blah() must be an instance of Foo, instance of stdClass given, called in /lang/CatchableErrorSpec.inlinePhp on line 15 and defined in /lang/CatchableErrorSpec.inlinePhp on line 5
          |""".stripMargin
      )
    }

    "Catchable fatal error [2]" in {
      // lang/catchable_error_002.phpt
      script(
        """<?php
          |	class Foo {
          |	}
          |
          |	function blah (Foo $a)
          |	{
          |	}
          |
          |	function error()
          |	{
          |		$a = func_get_args();
          |		var_dump($a);
          |	}
          |
          |	set_error_handler('error');
          |
          |	blah (new StdClass);
          |	echo "ALIVE!\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(5) {
          |  [0]=>
          |  int(4096)
          |  [1]=>
          |  string(151) "Argument 1 passed to blah() must be an instance of Foo, instance of stdClass given, called in /lang/CatchableErrorSpec.inlinePhp on line 17 and defined"
          |  [2]=>
          |  string(34) "/lang/CatchableErrorSpec.inlinePhp"
          |  [3]=>
          |  int(5)
          |  [4]=>
          |  array(0) {
          |  }
          |}
          |ALIVE!
          |""".stripMargin
      )
    }
  }
}
