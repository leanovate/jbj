package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace4Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace test 030-039" should {
    "030: Name ambiguity (import name & class name)" in {
      // Zend/tests/ns_030.phpt
      script(
        """<?php
          |class Foo {
          |}
          |
          |use A\B as Foo;
          |
          |new Foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot use A\B as Foo because the name is already in use in /zend/Namespace4Spec.inlinePhp on line 5
          |""".stripMargin
      )
    }

    "031: Namespace support for user functions (ns name)" in {
      // Zend/tests/ns_031.phpt
      script(
        """<?php
          |namespace test;
          |
          |class Test {
          |	static function foo() {
          |		echo __CLASS__,"::",__FUNCTION__,"\n";
          |	}
          |}
          |
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |
          |call_user_func(__NAMESPACE__."\\foo");
          |call_user_func(__NAMESPACE__."\\test::foo");
          |""".stripMargin
      ).result must haveOutput(
        """test\foo
          |test\Test::foo
          |""".stripMargin
      )
    }

    "032: Namespace support for user functions (php name)" in {
      // Zend/tests/ns_032.phpt
      script(
        """<?php
          |class Test {
          |	static function foo() {
          |		echo __CLASS__,"::",__FUNCTION__,"\n";
          |	}
          |}
          |
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |
          |call_user_func(__NAMESPACE__."\\foo");
          |call_user_func(__NAMESPACE__."\\test::foo");
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |Test::foo
          |""".stripMargin
      )
    }

    "033: Import statement with non-compound name" in {
      // Zend/tests/ns_033.phpt
      script(
        """<?php
          |use A;
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: The use statement with non-compound name 'A' has no effect in /zend/Namespace4Spec.inlinePhp on line 2
          |""".stripMargin
      )
    }

    "034: Support for namespaces in compile-time constant reference" in {
      // Zend/tests/ns_034.phpt
      script(
        """<?php
          |namespace A;
          |use A as B;
          |class Foo {
          |	const C = "ok\n";
          |}
          |function f1($x=Foo::C) {
          |	echo $x;
          |}
          |function f2($x=B\Foo::C) {
          |	echo $x;
          |}
          |function f3($x=\A\Foo::C) {
          |	echo $x;
          |}
          |echo Foo::C;
          |echo B\Foo::C;
          |echo \A\Foo::C;
          |f1();
          |f2();
          |f3();
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |ok
          |ok
          |ok
          |ok
          |""".stripMargin
      )
    }

    "035: Name ambiguity in compile-time constant reference (php name)" in {
      // Zend/tests/ns_035.phpt
      script(
        """<?php
          |namespace A;
          |use \ArrayObject;
          |
          |function f1($x = ArrayObject::STD_PROP_LIST) {
          |	var_dump($x);
          |}
          |function f2($x = \ArrayObject::STD_PROP_LIST) {
          |	var_dump($x);
          |}
          |var_dump(ArrayObject::STD_PROP_LIST);
          |var_dump(\ArrayObject::STD_PROP_LIST);
          |f1();
          |f2();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(1)
          |int(1)
          |int(1)
          |int(1)
          |""".stripMargin
      )
    }

    "036: Name ambiguity in compile-time constant reference (ns name)" in {
      // Zend/tests/ns_036.phpt
      script(
        """<?php
          |namespace A;
          |use A as B;
          |class ArrayObject {
          |	const STD_PROP_LIST = 2;
          |}
          |function f1($x = ArrayObject::STD_PROP_LIST) {
          |	var_dump($x);
          |}
          |function f2($x = \ArrayObject::STD_PROP_LIST) {
          |	var_dump($x);
          |}
          |function f3($x = \A\ArrayObject::STD_PROP_LIST) {
          |	var_dump($x);
          |}
          |function f4($x = B\ArrayObject::STD_PROP_LIST) {
          |	var_dump($x);
          |}
          |var_dump(ArrayObject::STD_PROP_LIST);
          |var_dump(\ArrayObject::STD_PROP_LIST);
          |var_dump(B\ArrayObject::STD_PROP_LIST);
          |var_dump(\A\ArrayObject::STD_PROP_LIST);
          |f1();
          |f2();
          |f3();
          |f4();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(2)
          |int(1)
          |int(2)
          |int(2)
          |int(2)
          |int(1)
          |int(2)
          |int(2)
          |""".stripMargin
      )
    }

    "037: Name ambiguity (namespace name or namespace's class name)" in {
      // Zend/tests/ns_037.phpt
      script(
        """<?php
          |namespace X;
          |use X as Y;
          |class X {
          |	const C = "const ok\n";
          |	static $var = "var ok\n";
          |	function __construct() {
          |		echo "class ok\n";
          |	}
          |	static function bar() {
          |		echo "method ok\n";
          |	}
          |}
          |new X();
          |new Y\X();
          |new \X\X();
          |X::bar();
          |Y\X::bar();
          |\X\X::bar();
          |echo X::C;
          |echo Y\X::C;
          |echo \X\X::C;
          |echo X::$var;
          |echo Y\X::$var;
          |echo \X\X::$var;
          |""".stripMargin
      ).result must haveOutput(
        """class ok
          |class ok
          |class ok
          |method ok
          |method ok
          |method ok
          |const ok
          |const ok
          |const ok
          |var ok
          |var ok
          |var ok
          |""".stripMargin
      )
    }

    "038: Name ambiguity (namespace name or internal class name)" in {
      // Zend/tests/ns_038.phpt
      script(
        """<?php
          |namespace Exception;
          |function foo() {
          |  echo "ok\n";
          |}
          |\Exception\foo();
          |\Exception::bar();
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |
          |Fatal error: Call to undefined method Exception::bar() in /zend/Namespace4Spec.inlinePhp on line 7
          |""".stripMargin
      )
    }

    "039: Constant declaration" in {
      // Zend/tests/ns_039.phpt
      script(
        """<?php
          |function foo($a = A) {
          |	echo "$a\n";
          |}
          |function bar($a = array(A => B)) {
          |	foreach ($a as $key => $val) {
          |		echo "$key\n";
          |		echo "$val\n";
          |	}
          |}
          |const A = "ok";
          |const B = A;
          |echo A . "\n";
          |echo B . "\n";
          |foo();
          |bar();
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |ok
          |ok
          |ok
          |ok
          |""".stripMargin
      )
    }
  }
}
