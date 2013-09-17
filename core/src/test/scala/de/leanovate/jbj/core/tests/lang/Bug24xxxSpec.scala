/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Bug24xxxSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs #24xxx" should {
    "Bug #24396 (global $$variable broken)" in {
      // lang/bug24396
      script(
        """<?php
          |
          |$arr = array('a' => 1, 'b' => 2, 'c' => 3);
          |
          |foreach($arr as $k=>$v)  {
          |    global $$k; // comment this out and it works in PHP 5 too..
          |
          |    echo "($k => $v)\n";
          |
          |    $$k = $v;
          |}
          |
          |// This following was not part of the original, but it stresses out the point of this test
          |echo "a:$a\n";
          |echo "b:$b\n";
          |echo "c:$c\n";
          |echo "d:$d\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """(a => 1)
          |(b => 2)
          |(c => 3)
          |a:1
          |b:2
          |c:3
          |
          |Notice: Undefined variable: d in /lang/Bug24xxxSpec.inlinePhp on line 17
          |d:
          |""".stripMargin
      )
    }

    "Bug #24436 (isset() and empty() produce errors with non-existent variables in objects)" in {
      // lang/bug24436.phpt
      script(
        """<?php
          |class test {
          |	function __construct() {
          |		if (empty($this->test[0][0])) { print "test1";}
          |		if (!isset($this->test[0][0])) { print "test2";}
          |	}
          |}
          |
          |$test1 = new test();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """test1test2""".stripMargin
      )
    }

    "Bug #24499 (bogus handling of a public property as a private one)" in {
      // lang/bug24499.phpt
      script(
        """<?php
          |class Id {
          |        private $id="priv";
          |
          |        public function tester($obj)
          |        {
          |	        	$obj->id = "bar";
          |        }
          |}
          |
          |$id = new Id();
          |@$obj->foo = "bar";
          |$id->tester($obj);
          |print_r($obj);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """stdClass Object
          |(
          |    [foo] => bar
          |    [id] => bar
          |)
          |""".stripMargin
      )
    }

    "Bug #24573 (debug_backtrace() crashes if $this is set to null)" in {
      // lang/bug24573.phpt
      script(
        """<?php
          |
          |class Foo {
          |  function Bar() {
          |    $__this = $this;
          |    $this = null;
          |    debug_backtrace();
          |    $this = $__this;
          |  }
          |}
          |
          |$f = new Foo;
          |
          |$f->Bar();
          |
          |echo "OK\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot re-assign $this in /lang/Bug24xxxSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }
  }
}
