/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class AutoloadSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Autoload" should {
    "ZE2 Autoload and class_exists" in {
      // classes/autoload_001
      script(
        """<?php
          |
          |function __autoload($class_name)
          |{
          |	require_once(dirname(__FILE__) . '/' . $class_name . '.p5c');
          |	echo __FUNCTION__ . '(' . $class_name . ")\n";
          |}
          |
          |var_dump(class_exists('autoload_root'));
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """__autoload(autoload_root)
          |bool(true)
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 Autoload and get_class_methods" in {
      // classes/autoload_002
      script(
        """<?php
          |
          |function __autoload($class_name)
          |{
          |	require_once(dirname(__FILE__) . '/' . $class_name . '.p5c');
          |	echo __FUNCTION__ . '(' . $class_name . ")\n";
          |}
          |
          |var_dump(get_class_methods('autoload_root'));
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """__autoload(autoload_root)
          |array(1) {
          |  [0]=>
          |  string(12) "testFunction"
          |}
          |===DONE===""".stripMargin
      )
    }

    "ZE2 Autoload and derived classes" in {
      // classes/autoload_003
      script(
        """<?php
          |
          |function __autoload($class_name)
          |{
          |	require_once(dirname(__FILE__) . '/' . $class_name . '.p5c');
          |	echo __FUNCTION__ . '(' . $class_name . ")\n";
          |}
          |
          |var_dump(class_exists('autoload_derived'));
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """__autoload(autoload_root)
          |__autoload(autoload_derived)
          |bool(true)
          |===DONE===""".stripMargin
      )
    }

    "ZE2 Autoload and recursion" in {
      // classes/autoload_004
      script(
        """<?php
          |
          |function __autoload($class_name)
          |{
          |	var_dump(class_exists($class_name));
          |	require_once(dirname(__FILE__) . '/' . $class_name . '.p5c');
          |	echo __FUNCTION__ . '(' . $class_name . ")\n";
          |}
          |
          |var_dump(class_exists('autoload_derived'));
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |bool(false)
          |__autoload(autoload_root)
          |__autoload(autoload_derived)
          |bool(true)
          |===DONE===""".stripMargin
      )
    }

    "ZE2 Autoload from destructor" in {
      // classes/autoload_005.phpt
      script(
        """<?php
          |
          |function __autoload($class_name)
          |{
          |	var_dump(class_exists($class_name, false));
          |	require_once(dirname(__FILE__) . '/' . $class_name . '.p5c');
          |	echo __FUNCTION__ . '(' . $class_name . ")\n";
          |}
          |
          |var_dump(class_exists('autoload_derived', false));
          |var_dump(class_exists('autoload_derived', false));
          |
          |class Test
          |{
          |    function __destruct() {
          |        echo __METHOD__ . "\n";
          |        $o = new autoload_derived;
          |        var_dump($o);
          |    }
          |}
          |
          |$o = new Test;
          |unset($o);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |bool(false)
          |Test::__destruct
          |bool(false)
          |bool(false)
          |__autoload(autoload_root)
          |__autoload(autoload_derived)
          |object(autoload_derived)#2 (0) {
          |}
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 Autoload from destructor" in {
      // classes/autoload_006.phpt
      script(
        """<?php
          |
          |function __autoload($class_name)
          |{
          |	require_once(dirname(__FILE__) . '/' . strtolower($class_name) . '.p5c');
          |	echo __FUNCTION__ . '(' . $class_name . ")\n";
          |}
          |
          |var_dump(interface_exists('autoload_interface', false));
          |var_dump(class_exists('autoload_implements', false));
          |
          |$o = new Autoload_Implements;
          |var_dump($o);
          |var_dump($o instanceof autoload_interface);
          |unset($o);
          |
          |var_dump(interface_exists('autoload_interface', false));
          |var_dump(class_exists('autoload_implements', false));
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |bool(false)
          |__autoload(autoload_interface)
          |__autoload(Autoload_Implements)
          |object(autoload_implements)#1 (0) {
          |}
          |bool(true)
          |bool(true)
          |bool(true)
          |===DONE===
          |""".stripMargin
      )
    }

    "Ensure instanceof does not trigger autoload." in {
      // classes/autoload_007
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo "In autoload: ";
          |      var_dump($name);
          |  }
          |
          |  $a = new stdClass;
          |  var_dump($a instanceof UndefC);
          |?>""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |""".stripMargin
      )
    }

    "Ensure catch blocks for unknown exception types do not trigger autoload." in {
      // classes/autoload_008
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo "In autoload: ";
          |      var_dump($name);
          |  }
          |
          |  function f()
          |  {
          |      throw new Exception();
          |  }
          |  try {
          |      f();
          |  }
          |  catch (UndefC $u) {
          |      echo "In UndefClass catch block.\n";
          |  }
          |  catch (Exception $e) {
          |      echo "In Exception catch block. Autoload should not have been triggered.\n";
          |  }
          |?>""".stripMargin
      ).result must haveOutput(
        """In Exception catch block. Autoload should not have been triggered.
          |""".stripMargin
      )
    }

    "Ensure type hints for unknown types do not trigger autoload." in {
      // classes/autoload_009.phpt
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo "In autoload: ";
          |      var_dump($name);
          |  }
          |
          |  function f(UndefClass $x)
          |  {
          |  }
          |  f(new stdClass);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Catchable fatal error: Argument 1 passed to f() must be an instance of UndefClass, instance of stdClass given, called in /classes/AutoloadSpec.inlinePhp on line 11 and defined in /classes/AutoloadSpec.inlinePhp on line 8
          |""".stripMargin
      )
    }

    "Ensure implements does trigger autoload." in {
      // classes/autoload_010.phpt
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo "In autoload: ";
          |      var_dump($name);
          |  }
          |
          |  class C implements UndefI
          |  {
          |  }
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """In autoload: string(6) "UndefI"
          |
          |Fatal error: Interface 'UndefI' not found in /classes/AutoloadSpec.inlinePhp on line 8
          |""".stripMargin
      )
    }

    "Ensure extends does trigger autoload." in {
      // classes/autoload_011
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo "In autoload: ";
          |      var_dump($name);
          |  }
          |
          |  class C extends UndefBase
          |  {
          |  }
          |?>""".stripMargin
      ).result must haveOutput(
        """In autoload: string(9) "UndefBase"
          |
          |Fatal error: Class 'UndefBase' not found in /classes/AutoloadSpec.inlinePhp on line 8
          |""".stripMargin
      )
    }

    "Ensure callback methods in unknown classes trigger autoload." in {
      // classes/autoload_012.phpt
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo "In autoload: ";
          |      var_dump($name);
          |  }
          |  call_user_func("UndefC::test");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """In autoload: string(6) "UndefC"
          |
          |Warning: call_user_func() expects parameter 1 to be a valid callback, class 'UndefC' not found in /classes/AutoloadSpec.inlinePhp on line 7
          |""".stripMargin
      )
    }

    "Ensure __autoload() allows for recursive calls if the class name differs." in {
      // classes/autoload_018.phpt
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo "IN:  " . __METHOD__ . "($name)\n";
          |
          |      static $i = 0;
          |      if ($i++ > 10) {
          |          echo "-> Recursion detected - as expected.\n";
          |          return;
          |      }
          |
          |      class_exists('UndefinedClass' . $i);
          |
          |      echo "OUT: " . __METHOD__ . "($name)\n";
          |  }
          |
          |  var_dump(class_exists('UndefinedClass0'));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """IN:  __autoload(UndefinedClass0)
          |IN:  __autoload(UndefinedClass1)
          |IN:  __autoload(UndefinedClass2)
          |IN:  __autoload(UndefinedClass3)
          |IN:  __autoload(UndefinedClass4)
          |IN:  __autoload(UndefinedClass5)
          |IN:  __autoload(UndefinedClass6)
          |IN:  __autoload(UndefinedClass7)
          |IN:  __autoload(UndefinedClass8)
          |IN:  __autoload(UndefinedClass9)
          |IN:  __autoload(UndefinedClass10)
          |IN:  __autoload(UndefinedClass11)
          |-> Recursion detected - as expected.
          |OUT: __autoload(UndefinedClass10)
          |OUT: __autoload(UndefinedClass9)
          |OUT: __autoload(UndefinedClass8)
          |OUT: __autoload(UndefinedClass7)
          |OUT: __autoload(UndefinedClass6)
          |OUT: __autoload(UndefinedClass5)
          |OUT: __autoload(UndefinedClass4)
          |OUT: __autoload(UndefinedClass3)
          |OUT: __autoload(UndefinedClass2)
          |OUT: __autoload(UndefinedClass1)
          |OUT: __autoload(UndefinedClass0)
          |bool(false)
          |""".stripMargin
      )
    }

    "Ensure __autoload() recursion is guarded for multiple lookups of same class using difference case." in {
      // classes/autoload_019.phpt
      script(
        """<?php
          |  function __autoload($name)
          |  {
          |      echo __FUNCTION__ . " $name\n";
          |      class_exists("undefinedCLASS");
          |  }
          |
          |  class_exists("unDefinedClass");
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """__autoload unDefinedClass
          |""".stripMargin
      )
    }
  }
}
