/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug25xxxSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs #25xxx and #26xxx" should {
    """Bug #25145 (SEGV on recpt of form input with name like "123[]")""" in {
      // lang/bug25145.phpt
      script(
        """<?php
          |
          |var_dump($_REQUEST);
          |echo "Done\n";
          |
          |?>
          |""".stripMargin
      ).withGet("?123[]=SEGV").result must haveOutput(
        """array(1) {
          |  [123]=>
          |  array(1) {
          |    [0]=>
          |    string(4) "SEGV"
          |  }
          |}
          |Done
          |""".stripMargin
      )
    }

    "Bug #25547 (error_handler and array index with function call)" in {
      // lang/bug25547.phpt
      script(
        """<?php
          |
          |function handler($errno, $errstr, $errfile, $errline, $context)
          |{
          |	echo __FUNCTION__ . "($errstr)\n";
          |}
          |
          |set_error_handler('handler');
          |
          |function foo($x) {
          |	return "foo";
          |}
          |
          |$output = array();
          |++$output[foo("bar")];
          |
          |print_r($output);
          |
          |echo "Done";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """handler(Undefined index: foo)
          |Array
          |(
          |    [foo] => 1
          |)
          |Done""".stripMargin
      )
    }

    "Bug #25652 (Calling Global functions dynamically fails from Class scope)" in {
      // lang/bug25652.phpt
      script(
        """<?php
          |
          |	function testfunc ($var) {
          |		echo "testfunc $var\n";
          |	}
          |
          |	class foo {
          |		public $arr = array('testfunc');
          |		function bar () {
          |			$this->arr[0]('testvalue');
          |		}
          |	}
          |
          |	$a = new foo ();
          |	$a->bar ();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """testfunc testvalue
          |""".stripMargin
      )
    }

    "Bug #25922 (SEGV in error_handler when context is destroyed)" in {
      // lang/bug25922.phpt
      script(
        """<?php
          |function my_error_handler($error, $errmsg='', $errfile='', $errline=0, $errcontext='')
          |{
          |	echo "$errmsg\n";
          |	$errcontext = '';
          |}
          |
          |set_error_handler('my_error_handler');
          |
          |function test()
          |{
          |	echo "Undefined index here: '{$data['HTTP_HEADER']}'\n";
          |}
          |test();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Undefined variable: data
          |Undefined index here: ''
          |""".stripMargin
      )
    }

    "Bug #26182 (Object properties created redundantly)" in {
      // lang/bug26182.phpt
      script(
        """<?php
          |
          |class A {
          |    function NotAConstructor ()
          |    {
          |        if (isset($this->x)) {
          |            //just for demo
          |        }
          |    }
          |}
          |
          |$t = new A ();
          |
          |print_r($t);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """A Object
          |(
          |)
          |""".stripMargin
      )
    }

    "Bug #26696 (string index in a switch() crashes with multiple matches)" in {
      // lang/bug26696.phpt
      script(
        """<?php
          |
          |$str = 'asdd/?';
          |$len = strlen($str);
          |for ($i = 0; $i < $len; $i++) {
          |	switch ($str[$i]) {
          |		case '?':
          |			echo "OK\n";
          |			break;
          |	}
          |}
          |
          |$str = '*';
          |switch ($str[0]) {
          |	case '*';
          |		echo "OK\n";
          |		break;
          |	default:
          |		echo 'Default RAN!';
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """OK
          |OK
          |""".stripMargin
      )
    }

    "Bug #26866 (segfault when exception raised in __get)" in {
      // lang/bug26866.phpt
      script(
        """<?php
          |class bar {
          |	function get_name() {
          |		return 'bar';
          |	}
          |}
          |class foo {
          |	function __get($sName) {
          |		throw new Exception('Exception!');
          |		return new bar();
          |	}
          |}
          |$foo = new foo();
          |try {
          |	echo $foo->bar->get_name();
          |}
          |catch (Exception $E) {
          |	echo "Exception raised!\n";
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Exception raised!
          |""".stripMargin
      )
    }
  }
}
