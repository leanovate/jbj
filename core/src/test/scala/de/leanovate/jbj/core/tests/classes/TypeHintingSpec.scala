/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class TypeHintingSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "type hinting" should {
    "ZE2 class type hinting" in {
      // classes/type_hinting_001.phpt
      script(
        """<?php
          |
          |interface Foo {
          |	function a(Foo $foo);
          |}
          |
          |interface Bar {
          |	function b(Bar $bar);
          |}
          |
          |class FooBar implements Foo, Bar {
          |	function a(Foo $foo) {
          |		// ...
          |	}
          |
          |	function b(Bar $bar) {
          |		// ...
          |	}
          |}
          |
          |class Blort {
          |}
          |
          |$a = new FooBar;
          |$b = new Blort;
          |
          |$a->a($b);
          |$a->b($b);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Catchable fatal error: Argument 1 passed to FooBar::a() must implement interface Foo, instance of Blort given, called in /classes/TypeHintingSpec.inlinePhp on line 27 and defined in /classes/TypeHintingSpec.inlinePhp on line 12
          |""".stripMargin
      )
    }

    "ZE2 class type hinting non existing class" in {
      // classes/type_hinting_002.phpt
      script(
        """<?php
          |
          |class Foo {
          |	function a(NonExisting $foo) {}
          |}
          |
          |$o = new Foo;
          |$o->a($o);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Catchable fatal error: Argument 1 passed to Foo::a() must be an instance of NonExisting, instance of Foo given, called in /classes/TypeHintingSpec.inlinePhp on line 8 and defined in /classes/TypeHintingSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "ZE2 class type hinting with arrays" in {
      // classes/type_hinting_003.phpt
      script(
        """<?php
          |
          |class Test
          |{
          |	static function f1(array $ar)
          |	{
          |		echo __METHOD__ . "()\n";
          |		var_dump($ar);
          |	}
          |
          |	static function f2(array $ar = NULL)
          |	{
          |		echo __METHOD__ . "()\n";
          |		var_dump($ar);
          |	}
          |
          |	static function f3(array $ar = array())
          |	{
          |		echo __METHOD__ . "()\n";
          |		var_dump($ar);
          |	}
          |
          |	static function f4(array $ar = array(25))
          |	{
          |		echo __METHOD__ . "()\n";
          |		var_dump($ar);
          |	}
          |}
          |
          |Test::f1(array(42));
          |Test::f2(NULL);
          |Test::f2();
          |Test::f3();
          |Test::f4();
          |Test::f1(1);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Test::f1()
          |array(1) {
          |  [0]=>
          |  int(42)
          |}
          |Test::f2()
          |NULL
          |Test::f2()
          |NULL
          |Test::f3()
          |array(0) {
          |}
          |Test::f4()
          |array(1) {
          |  [0]=>
          |  int(25)
          |}
          |
          |Catchable fatal error: Argument 1 passed to Test::f1() must be of the type array, integer given, called in /classes/TypeHintingSpec.inlinePhp on line 35 and defined in /classes/TypeHintingSpec.inlinePhp on line 5
          |""".stripMargin
      )
    }

    "Ensure type hints are enforced for functions invoked as callbacks." in {
      // classes/type_hinting_004.phpt
      script(
        """<?php
          |  set_error_handler('myErrorHandler', E_RECOVERABLE_ERROR);
          |  function myErrorHandler($errno, $errstr, $errfile, $errline) {
          |      echo "$errno: $errstr - $errfile($errline)\n";
          |      return true;
          |  }
          |
          |  echo "---> Type hints with callback function:\n";
          |  class A  {  }
          |  function f1(A $a)  {
          |      echo "in f1;\n";
          |  }
          |  function f2(A $a = null)  {
          |      echo "in f2;\n";
          |  }
          |  call_user_func('f1', 1);
          |  call_user_func('f1', new A);
          |  call_user_func('f2', 1);
          |  call_user_func('f2');
          |  call_user_func('f2', new A);
          |  call_user_func('f2', null);
          |
          |
          |  echo "\n\n---> Type hints with callback static method:\n";
          |  class C {
          |      static function f1(A $a) {
          |          if (isset($this)) {
          |              echo "in C::f1 (instance);\n";
          |          } else {
          |              echo "in C::f1 (static);\n";
          |          }
          |      }
          |      static function f2(A $a = null) {
          |          if (isset($this)) {
          |              echo "in C::f2 (instance);\n";
          |          } else {
          |              echo "in C::f2 (static);\n";
          |          }
          |      }
          |  }
          |  call_user_func(array('C', 'f1'), 1);
          |  call_user_func(array('C', 'f1'), new A);
          |  call_user_func(array('C', 'f2'), 1);
          |  call_user_func(array('C', 'f2'));
          |  call_user_func(array('C', 'f2'), new A);
          |  call_user_func(array('C', 'f2'), null);
          |
          |
          |  echo "\n\n---> Type hints with callback instance method:\n";
          |  class D {
          |      function f1(A $a) {
          |          if (isset($this)) {
          |              echo "in C::f1 (instance);\n";
          |          } else {
          |              echo "in C::f1 (static);\n";
          |          }
          |      }
          |      function f2(A $a = null) {
          |          if (isset($this)) {
          |              echo "in C::f2 (instance);\n";
          |          } else {
          |              echo "in C::f2 (static);\n";
          |          }
          |      }
          |  }
          |  $d = new D;
          |  call_user_func(array($d, 'f1'), 1);
          |  call_user_func(array($d, 'f1'), new A);
          |  call_user_func(array($d, 'f2'), 1);
          |  call_user_func(array($d, 'f2'));
          |  call_user_func(array($d, 'f2'), new A);
          |  call_user_func(array($d, 'f2'), null);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """---> Type hints with callback function:
          |4096: Argument 1 passed to f1() must be an instance of A, integer given - /classes/TypeHintingSpec.inlinePhp(10)
          |in f1;
          |in f1;
          |4096: Argument 1 passed to f2() must be an instance of A, integer given - /classes/TypeHintingSpec.inlinePhp(13)
          |in f2;
          |in f2;
          |in f2;
          |in f2;
          |
          |
          |---> Type hints with callback static method:
          |4096: Argument 1 passed to C::f1() must be an instance of A, integer given - /classes/TypeHintingSpec.inlinePhp(26)
          |in C::f1 (static);
          |in C::f1 (static);
          |4096: Argument 1 passed to C::f2() must be an instance of A, integer given - /classes/TypeHintingSpec.inlinePhp(33)
          |in C::f2 (static);
          |in C::f2 (static);
          |in C::f2 (static);
          |in C::f2 (static);
          |
          |
          |---> Type hints with callback instance method:
          |4096: Argument 1 passed to D::f1() must be an instance of A, integer given - /classes/TypeHintingSpec.inlinePhp(51)
          |in C::f1 (instance);
          |in C::f1 (instance);
          |4096: Argument 1 passed to D::f2() must be an instance of A, integer given - /classes/TypeHintingSpec.inlinePhp(58)
          |in C::f2 (instance);
          |in C::f2 (instance);
          |in C::f2 (instance);
          |in C::f2 (instance);
          |""".stripMargin
      )
    }
  }
}
