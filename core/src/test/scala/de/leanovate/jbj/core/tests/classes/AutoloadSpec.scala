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
  }
}
