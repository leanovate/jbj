/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug2xxxxSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Bugs #2xxxx" should {
    "Bug #20175 (Static vars can't store ref to new instance)" in {
      // lang/bug20175.phpt
      script(
        """<?php
          |print zend_version()."\n";
          |
          |/* Part 1:
          | * Storing the result of a function in a static variable.
          | * foo_global() increments global variable $foo_count whenever it is executed.
          | * When foo_static() is called it checks for the static variable $foo_value
          | * being initialised. In case initialisation is necessary foo_global() will be
          | * called. Since that must happen only once the return value should be equal.
          | */
          |$foo_count = 0;
          |
          |function foo_global() {
          |	global $foo_count;
          |	echo "foo_global()\n";
          |	return 'foo:' . ++$foo_count;
          |}
          |
          |function foo_static() {
          |	static $foo_value;
          |	echo "foo_static()\n";
          |	if (!isset($foo_value)) {
          |		$foo_value = foo_global();
          |	}
          |	return $foo_value;
          |}
          |
          |/* Part 2:
          | * Storing a reference to the result of a function in a static variable.
          | * Same as Part 1 but:
          | * The return statment transports a copy of the value to return. In other
          | * words the return value of bar_global() is a temporary variable only valid
          | * after the function call bar_global() is done in current local scope.
          | */
          |$bar_count = 0;
          |
          |function bar_global() {
          |	global $bar_count;
          |	echo "bar_global()\n";
          |	return 'bar:' . ++$bar_count;
          |}
          |
          |function bar_static() {
          |	static $bar_value;
          |	echo "bar_static()\n";
          |	if (!isset($bar_value)) {
          |		$bar_value = &bar_global();
          |	}
          |	return $bar_value;
          |}
          |
          |/* Part 3: TO BE DISCUSSED
          | *
          | * Storing a reference to the result of a function in a static variable.
          | * Same as Part 2 but wow_global() returns a reference so $wow_value
          | * should store a reference to $wow_global. Therefor $wow_value is already
          | * initialised in second call to wow_static() and hence shouldn't call
          | * wow_global() again.
          | */ /*
          |$wow_count = 0;
          |$wow_name = '';
          |
          |function &wow_global() {
          |	global $wow_count, $wow_name;
          |	echo "wow_global()\n";
          |	$wow_name = 'wow:' . ++$wow_count;
          |	return $wow_name;
          |}
          |
          |function wow_static() {
          |	static $wow_value;
          |	echo "wow_static()\n";
          |	if (!isset($wow_value)) {
          |		$wow_value = &wow_global();
          |	}
          |	return $wow_value;
          |}*/
          |
          |/* Part 4:
          | * Storing a reference to a new instance (that's where the name of the  test
          | * comes from). First there is the global counter $oop_global again which
          | * counts the calls to the constructor of oop_class and hence counts the
          | * creation of oop_class instances.
          | * The class oop_test uses a static reference to a oop_class instance.
          | * When another oop_test instance is created it must reuse the statically
          | * stored reference oop_value. This way oop_class gets some singleton behavior
          | * since it will be created only once for all insatnces of oop_test.
          | */
          |$oop_global = 0;
          |class oop_class {
          |	var $oop_name;
          |
          |	function oop_class() {
          |		global $oop_global;
          |		echo "oop_class()\n";
          |		$this->oop_name = 'oop:' . ++$oop_global;
          |	}
          |}
          |
          |class oop_test {
          |	static $oop_value;
          |
          |	function oop_test() {
          |		echo "oop_test()\n";
          |	}
          |
          |	function oop_static() {
          |		echo "oop_static()\n";
          |		if (!isset(self::$oop_value)) {
          |			self::$oop_value = & new oop_class;
          |		}
          |		echo self::$oop_value->oop_name;
          |	}
          |}
          |
          |print foo_static()."\n";
          |print foo_static()."\n";
          |print bar_static()."\n";
          |print bar_static()."\n";
          |//print wow_static()."\n";
          |//print wow_static()."\n";
          |echo "wow_static()
          |wow_global()
          |wow:1
          |wow_static()
          |wow:1
          |";
          |$oop_tester = new oop_test;
          |print $oop_tester->oop_static()."\n";
          |print $oop_tester->oop_static()."\n";
          |$oop_tester = new oop_test; // repeated.
          |print $oop_tester->oop_static()."\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Deprecated: Assigning the return value of new by reference is deprecated in /lang/Bug2xxxxSpec.inlinePhp on line 110
          |2.5.0
          |foo_static()
          |foo_global()
          |foo:1
          |foo_static()
          |foo:1
          |bar_static()
          |bar_global()
          |
          |Strict Standards: Only variables should be assigned by reference in /lang/Bug2xxxxSpec.inlinePhp on line 47
          |bar:1
          |bar_static()
          |bar:1
          |wow_static()
          |wow_global()
          |wow:1
          |wow_static()
          |wow:1
          |oop_test()
          |oop_static()
          |oop_class()
          |oop:1
          |oop_static()
          |oop:1
          |oop_test()
          |oop_static()
          |oop:1
          |""".stripMargin
      )
    }

    "Bug #21094 (set_error_handler not accepting methods)" in {
      // lang/bug21094.phpt
      script(
        """<?php
          |class test {
          |	function hdlr($errno, $errstr, $errfile, $errline) {
          |		printf("[%d] errstr: %s, errfile: %s, errline: %d\n", $errno, $errstr, $errfile, $errline, $errstr);
          |	}
          |}
          |
          |set_error_handler(array(new test(), "hdlr"));
          |
          |trigger_error("test");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """[1024] errstr: test, errfile: /lang/Bug2xxxxSpec.inlinePhp, errline: 10
          |""".stripMargin
      )
    }

    "Bug #21600 (assign by reference function call changes variable contents)" in {
      // lang/bug21600.phpt
      script(
        """<?php
          |$tmp = array();
          |$tmp['foo'] = "test";
          |$tmp['foo'] = &bar($tmp['foo']);
          |var_dump($tmp);
          |
          |unset($tmp);
          |
          |$tmp = array();
          |$tmp['foo'] = "test";
          |$tmp['foo'] = &fubar($tmp['foo']);
          |var_dump($tmp);
          |
          |function bar($text){
          |  return $text;
          |}
          |
          |function fubar($text){
          |  $text = &$text;
          |  return $text;
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Only variables should be assigned by reference in /lang/Bug2xxxxSpec.inlinePhp on line 4
          |array(1) {
          |  ["foo"]=>
          |  string(4) "test"
          |}
          |
          |Strict Standards: Only variables should be assigned by reference in /lang/Bug2xxxxSpec.inlinePhp on line 11
          |array(1) {
          |  ["foo"]=>
          |  string(4) "test"
          |}
          |""".stripMargin
      )
    }

    """Bug #21669 ("$obj = new $this->var;" doesn't work)""" in {
      // lang/bug21669.phpt
      script(
        """<?php
          |class Test {
          |	function say_hello() {
          |		echo "Hello world";
          |	}
          |}
          |
          |class Factory {
          |	public $name = "Test";
          |	function create() {
          |		$obj = new $this->name; /* Parse error */
          |		return $obj;
          |	}
          |}
          |$factory = new Factory;
          |$test = $factory->create();
          |$test->say_hello();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Hello world""".stripMargin
      )
    }

    "Bug #21849 (self::constant doesn't work as method's default parameter)" in {
      // lang/bug21849.phpt
      script(
        """<?php
          |class foo {
          |	const bar = "fubar\n";
          |
          |	function foo($arg = self::bar) {
          |		echo $arg;
          |	}
          |}
          |
          |new foo();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """fubar
          |""".stripMargin
      )
    }

    "Bug #21961 (get_parent_class() segfault)" in {
      // lang/bug21961.phpt
      script(
        """<?php
          |
          |class man
          |{
          |	public $name, $bars;
          |	function man()
          |	{
          |		$this->name = 'Mr. X';
          |		$this->bars = array();
          |	}
          |
          |	function getdrunk($where)
          |	{
          |		$this->bars[] = new bar($where);
          |	}
          |
          |	function getName()
          |	{
          |		return $this->name;
          |	}
          |}
          |
          |class bar extends man
          |{
          |	public $name;
          |
          |	function bar($w)
          |	{
          |		$this->name = $w;
          |	}
          |
          |	function getName()
          |	{
          |		return $this->name;
          |	}
          |
          |	function whosdrunk()
          |	{
          |		$who = get_parent_class($this);
          |		if($who == NULL)
          |		{
          |			return 'nobody';
          |		}
          |		return eval("return ".$who.'::getName();');
          |	}
          |}
          |
          |$x = new man;
          |$x->getdrunk('The old Tavern');
          |var_dump($x->bars[0]->whosdrunk());
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(14) "The old Tavern"
          |""".stripMargin
      )
    }

    "Bug #22231 (segfault when returning a global variable by reference)" in {
      // lang/bug22231.phpt
      script(
        """<?php
          |class foo {
          |    public $fubar = 'fubar';
          |}
          |
          |function &foo(){
          |    $GLOBALS['foo'] = &new foo();
          |    return $GLOBALS['foo'];
          |}
          |$bar = &foo();
          |var_dump($bar);
          |var_dump($bar->fubar);
          |unset($bar);
          |$bar = &foo();
          |var_dump($bar->fubar);
          |
          |$foo = &foo();
          |var_dump($foo);
          |var_dump($foo->fubar);
          |unset($foo);
          |$foo = &foo();
          |var_dump($foo->fubar);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Deprecated: Assigning the return value of new by reference is deprecated in /lang/Bug2xxxxSpec.inlinePhp on line 7
          |object(foo)#1 (1) {
          |  ["fubar"]=>
          |  string(5) "fubar"
          |}
          |string(5) "fubar"
          |string(5) "fubar"
          |object(foo)#3 (1) {
          |  ["fubar"]=>
          |  string(5) "fubar"
          |}
          |string(5) "fubar"
          |string(5) "fubar"
          |""".stripMargin
      )
    }

    "Bug #22510 (segfault among complex references)" in {
      // lang/bug22510.phpt
      script(
        """<?php
          |class foo
          |{
          |	public $list = array();
          |
          |	function finalize() {
          |		print __CLASS__."::".__FUNCTION__."\n";
          |		$cl = &$this->list;
          |	}
          |
          |	function &method1() {
          |		print __CLASS__."::".__FUNCTION__."\n";
          |		return @$this->foo;
          |	}
          |
          |	function &method2() {
          |		print __CLASS__."::".__FUNCTION__."\n";
          |		return $this->foo;
          |	}
          |
          |	function method3() {
          |		print __CLASS__."::".__FUNCTION__."\n";
          |		return @$this->foo;
          |	}
          |}
          |
          |class bar
          |{
          |	function run1() {
          |		print __CLASS__."::".__FUNCTION__."\n";
          |		$this->instance = new foo();
          |		$this->instance->method1($this);
          |		$this->instance->method1($this);
          |	}
          |
          |	function run2() {
          |		print __CLASS__."::".__FUNCTION__."\n";
          |		$this->instance = new foo();
          |		$this->instance->method2($this);
          |		$this->instance->method2($this);
          |	}
          |
          |	function run3() {
          |		print __CLASS__."::".__FUNCTION__."\n";
          |		$this->instance = new foo();
          |		$this->instance->method3($this);
          |		$this->instance->method3($this);
          |	}
          |}
          |
          |function ouch(&$bar) {
          |	print __FUNCTION__."\n";
          |	@$a = $a;
          |	$bar->run1();
          |}
          |
          |function ok1(&$bar) {
          |	print __FUNCTION__."\n";
          |	$bar->run1();
          |}
          |
          |function ok2(&$bar) {
          |	print __FUNCTION__."\n";
          |	@$a = $a;
          |	$bar->run2();
          |}
          |
          |function ok3(&$bar) {
          |	print __FUNCTION__."\n";
          |	@$a = $a;
          |	$bar->run3();
          |}
          |
          |$bar = &new bar();
          |ok1($bar);
          |$bar->instance->finalize();
          |print "done!\n";
          |ok2($bar);
          |$bar->instance->finalize();
          |print "done!\n";
          |ok3($bar);
          |$bar->instance->finalize();
          |print "done!\n";
          |ouch($bar);
          |$bar->instance->finalize();
          |print "I'm alive!\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Deprecated: Assigning the return value of new by reference is deprecated in /lang/Bug2xxxxSpec.inlinePhp on line 74
          |ok1
          |bar::run1
          |foo::method1
          |
          |Notice: Only variable references should be returned by reference in /lang/Bug2xxxxSpec.inlinePhp on line 13
          |foo::method1
          |
          |Notice: Only variable references should be returned by reference in /lang/Bug2xxxxSpec.inlinePhp on line 13
          |foo::finalize
          |done!
          |ok2
          |bar::run2
          |foo::method2
          |foo::method2
          |foo::finalize
          |done!
          |ok3
          |bar::run3
          |foo::method3
          |foo::method3
          |foo::finalize
          |done!
          |ouch
          |bar::run1
          |foo::method1
          |
          |Notice: Only variable references should be returned by reference in /lang/Bug2xxxxSpec.inlinePhp on line 13
          |foo::method1
          |
          |Notice: Only variable references should be returned by reference in /lang/Bug2xxxxSpec.inlinePhp on line 13
          |foo::finalize
          |I'm alive!
          |""".stripMargin
      )
    }
  }
}
