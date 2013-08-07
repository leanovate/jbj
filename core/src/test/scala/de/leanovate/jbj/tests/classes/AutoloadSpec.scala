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
  }
}
