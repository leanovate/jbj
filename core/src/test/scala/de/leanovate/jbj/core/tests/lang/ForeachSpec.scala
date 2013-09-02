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

    "Foreach loop tests - basic loop with just value and key => value." in {
      // lang/foreachLoop.001.phpt
      script(
        """<?php
          |
          |$a = array("a","b","c");
          |
          |foreach ($a as $v) {
          |	var_dump($v);
          |}
          |foreach ($a as $k => $v) {
          |	var_dump($k, $v);
          |}
          |//check key and value after the loop.
          |var_dump($k, $v);
          |
          |echo "\n";
          |//Dynamic array
          |foreach (array("d","e","f") as $v) {
          |	var_dump($v);
          |}
          |foreach (array("d","e","f") as $k => $v) {
          |	var_dump($k, $v);
          |}
          |//check key and value after the loop.
          |var_dump($k, $v);
          |
          |echo "\n";
          |//Ensure counter is advanced during loop
          |$a=array("a","b","c");
          |foreach ($a as $v);
          |var_dump(current($a));
          |$a=array("a","b","c");
          |foreach ($a as &$v);
          |var_dump(current($a));
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(1) "a"
          |string(1) "b"
          |string(1) "c"
          |int(0)
          |string(1) "a"
          |int(1)
          |string(1) "b"
          |int(2)
          |string(1) "c"
          |int(2)
          |string(1) "c"
          |
          |string(1) "d"
          |string(1) "e"
          |string(1) "f"
          |int(0)
          |string(1) "d"
          |int(1)
          |string(1) "e"
          |int(2)
          |string(1) "f"
          |int(2)
          |string(1) "f"
          |
          |bool(false)
          |bool(false)
          |""".stripMargin
      )
    }

    "Foreach loop tests - error case: not an array." in {
      // lang/foreachLoop.003.phpt
      script(
        """<?php
          |echo "\nNot an array.\n";
          |$a = TRUE;
          |foreach ($a as $v) {
          |	var_dump($v);
          |}
          |
          |$a = null;
          |foreach ($a as $v) {
          |	var_dump($v);
          |}
          |
          |$a = 1;
          |foreach ($a as $v) {
          |	var_dump($v);
          |}
          |
          |$a = 1.5;
          |foreach ($a as $v) {
          |	var_dump($v);
          |}
          |
          |$a = "hello";
          |foreach ($a as $v) {
          |	var_dump($v);
          |}
          |
          |echo "done.\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Not an array.
          |
          |Warning: Invalid argument supplied for foreach() in /lang/ForeachSpec.inlinePhp on line 4
          |
          |Warning: Invalid argument supplied for foreach() in /lang/ForeachSpec.inlinePhp on line 9
          |
          |Warning: Invalid argument supplied for foreach() in /lang/ForeachSpec.inlinePhp on line 14
          |
          |Warning: Invalid argument supplied for foreach() in /lang/ForeachSpec.inlinePhp on line 19
          |
          |Warning: Invalid argument supplied for foreach() in /lang/ForeachSpec.inlinePhp on line 24
          |done.
          |""".stripMargin
      )
    }
  }
}
