package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class ForeachSpec extends FreeSpec with TestJbjExecutor with MustMatchers{
  "Foreach" - {
    "foreach() with foreach($o->mthd()->arr)" in {
      // lang/foreach_with_object_001
      script(
        """<?php
          |class Test {
          |   public $a = array(1,2,3,4,5); // removed, crash too
          |   function c() {
          |      return new Test();
          |   }
          |
          |}
          |$obj = new Test();
          |foreach ($obj->c()->a as $value) {
          |    print "$value\n";
          |}
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """1
          |2
          |3
          |4
          |5
          |===DONE===""".stripMargin
      )
    }
  }
}
