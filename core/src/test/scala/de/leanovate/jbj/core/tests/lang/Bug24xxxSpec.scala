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

    "Bug #24652 (broken array_flip())" in {
      // lang/bug24652.phpt
      script(
        """<?php
          |  /* This works */
          |  $f = array('7' => 0);
          |  var_dump($f);
          |  var_dump(array_key_exists(7, $f));
          |  var_dump(array_key_exists('7', $f));
          |
          |  print "----------\n";
          |  /* This doesn't */
          |  $f = array_flip(array('7'));
          |  var_dump($f);
          |  var_dump(array_key_exists(7, $f));
          |  var_dump(array_key_exists('7', $f));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(1) {
          |  [7]=>
          |  int(0)
          |}
          |bool(true)
          |bool(true)
          |----------
          |array(1) {
          |  [7]=>
          |  int(0)
          |}
          |bool(true)
          |bool(true)
          |""".stripMargin
      )
    }

    "Bug #24658 (combo of typehint / reference causes crash)" in {
      // lang/bug24658.phpt
      script(
        """<?php
          |class foo {}
          |function no_typehint($a) {
          |	var_dump($a);
          |}
          |function typehint(foo $a) {
          |	var_dump($a);
          |}
          |function no_typehint_ref(&$a) {
          |	var_dump($a);
          |}
          |function typehint_ref(foo &$a) {
          |	var_dump($a);
          |}
          |$v = new foo();
          |$a = array(new foo(), 1, 2);
          |no_typehint($v);
          |typehint($v);
          |no_typehint_ref($v);
          |typehint_ref($v);
          |echo "===no_typehint===\n";
          |array_walk($a, 'no_typehint');
          |echo "===no_typehint_ref===\n";
          |array_walk($a, 'no_typehint_ref');
          |echo "===typehint===\n";
          |array_walk($a, 'typehint');
          |echo "===typehint_ref===\n";
          |array_walk($a, 'typehint_ref');
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """object(foo)#1 (0) {
          |}
          |object(foo)#1 (0) {
          |}
          |object(foo)#1 (0) {
          |}
          |object(foo)#1 (0) {
          |}
          |===no_typehint===
          |object(foo)#2 (0) {
          |}
          |int(1)
          |int(2)
          |===no_typehint_ref===
          |object(foo)#2 (0) {
          |}
          |int(1)
          |int(2)
          |===typehint===
          |object(foo)#2 (0) {
          |}
          |
          |Catchable fatal error: Argument 1 passed to typehint() must be an instance of foo, integer given, called in /lang/Bug24xxxSpec.inlinePhp on line 26 and defined in /lang/Bug24xxxSpec.inlinePhp on line 6
          |""".stripMargin
      )
    }

    """Bug #24783 ($key not binary safe in "foreach($arr as $key => $val)")""" in {
      // lang/bug24783.phpt
      script(
        """<?php
          |error_reporting(E_ALL);
          |	$arr = array ("foo\0bar" => "foo\0bar");
          |	foreach ($arr as $key => $val) {
          |		echo strlen($key), ': ';
          |		echo urlencode($key), ' => ', urlencode($val), "\n";
          |	}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """7: foo%00bar => foo%00bar
          |""".stripMargin
      )
    }

    "Bug #24908 (super-globals can not be used in __destruct())" in {
      // lang/bug24908.phpt
      script(
        """<?php
          |class test {
          |	function __construct() {
          |		if (count($_SERVER)) echo "O";
          |	}
          |	function __destruct() {
          |		if (count($_SERVER)) echo "K\n";
          |	}
          |}
          |$test = new test();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """OK
          |""".stripMargin
      )
    }

    "Bug #24926 (lambda function (create_function()) cannot be stored in a class property)" in {
      // lang/bug24926.phpt
      script(
        """<?php
          |
          |error_reporting (E_ALL);
          |
          |class foo {
          |
          |    public $functions = array();
          |
          |    function foo()
          |    {
          |        $function = create_function('', 'return "FOO\n";');
          |        print($function());
          |
          |        $this->functions['test'] = $function;
          |        print($this->functions['test']());    // werkt al niet meer
          |
          |    }
          |}
          |
          |$a = new foo ();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """FOO
          |FOO
          |""".stripMargin
      )
    }
  }
}
