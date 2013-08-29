/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import de.leanovate.jbj.core.tests.TestJbjExecutor
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

    "foreach() with references" in {
      // lang/foreach_with_reference_001
      script(
        """<?php
          |
          |$arr = array(1 => "one", 2 => "two", 3 => "three");
          |
          |foreach($arr as $key => $val) {
          |	$val = $key;
          |}
          |
          |print_r($arr);
          |
          |foreach($arr as $key => &$val) {
          |	$val = $key;
          |}
          |
          |print_r($arr);
          |
          |""".stripMargin
      ).result must haveOutput(
        """Array
          |(
          |    [1] => one
          |    [2] => two
          |    [3] => three
          |)
          |Array
          |(
          |    [1] => 1
          |    [2] => 2
          |    [3] => 3
          |)
          |""".stripMargin
      )
    }
  }
}
