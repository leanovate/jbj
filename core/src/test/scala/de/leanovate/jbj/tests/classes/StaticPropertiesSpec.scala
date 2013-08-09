package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class StaticPropertiesSpec  extends FreeSpec with TestJbjExecutor with MustMatchers{
  "Static properties" - {
    "ZE2 Initializing static properties to arrays" in {
      // classes/static_properties_001
      script(
        """<?php
          |
          |class test {
          |	static public $ar = array();
          |}
          |
          |var_dump(test::$ar);
          |
          |test::$ar[] = 1;
          |
          |var_dump(test::$ar);
          |
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """array(0) {
          |}
          |array(1) {
          |  [0]=>
          |  int(1)
          |}
          |Done
          |""".stripMargin
      )
    }
  }
}
