package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class ForeachSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Foreach" should {
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
