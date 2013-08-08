package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class AutoloadSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Autoload" - {
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
  }
}
