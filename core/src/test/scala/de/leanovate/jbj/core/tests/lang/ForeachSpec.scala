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

    "Foreach loop tests - modifying the array during the loop." in {
      // lang/foreachLoop.002.phpt
      script(
        """<?php
          |
          |echo "\nDirectly changing array values.\n";
          |$a = array("original.1","original.2","original.3");
          |foreach ($a as $k=>$v) {
          |	$a[$k]="changed.$k";
          |	var_dump($v);
          |}
          |var_dump($a);
          |
          |echo "\nModifying the foreach \$value.\n";
          |$a = array("original.1","original.2","original.3");
          |foreach ($a as $k=>$v) {
          |	$v="changed.$k";
          |}
          |var_dump($a);
          |
          |
          |echo "\nModifying the foreach &\$value.\n";
          |$a = array("original.1","original.2","original.3");
          |foreach ($a as $k=>&$v) {
          |	$v="changed.$k";
          |}
          |var_dump($a);
          |
          |echo "\nPushing elements onto an unreferenced array.\n";
          |$a = array("original.1","original.2","original.3");
          |$counter=0;
          |foreach ($a as $v) {
          |	array_push($a, "new.$counter");
          |
          |	//avoid infinite loop if test is failing
          |    if ($counter++>10) {
          |    	echo "Loop detected\n";
          |    	break;
          |    }
          |}
          |var_dump($a);
          |
          |echo "\nPushing elements onto an unreferenced array, using &\$value.\n";
          |$a = array("original.1","original.2","original.3");
          |$counter=0;
          |foreach ($a as &$v) {
          |	array_push($a, "new.$counter");
          |
          |	//avoid infinite loop if test is failing
          |    if ($counter++>10) {
          |    	echo "Loop detected\n";
          |    	break;
          |    }
          |}
          |var_dump($a);
          |
          |echo "\nPopping elements off an unreferenced array.\n";
          |$a = array("original.1","original.2","original.3");
          |foreach ($a as $v) {
          |	array_pop($a);
          |	var_dump($v);
          |}
          |var_dump($a);
          |
          |echo "\nPopping elements off an unreferenced array, using &\$value.\n";
          |$a = array("original.1","original.2","original.3");
          |foreach ($a as &$v) {
          |	array_pop($a);
          |	var_dump($v);
          |}
          |var_dump($a);
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Directly changing array values.
          |string(10) "original.1"
          |string(10) "original.2"
          |string(10) "original.3"
          |array(3) {
          |  [0]=>
          |  string(9) "changed.0"
          |  [1]=>
          |  string(9) "changed.1"
          |  [2]=>
          |  string(9) "changed.2"
          |}
          |
          |Modifying the foreach $value.
          |array(3) {
          |  [0]=>
          |  string(10) "original.1"
          |  [1]=>
          |  string(10) "original.2"
          |  [2]=>
          |  string(10) "original.3"
          |}
          |
          |Modifying the foreach &$value.
          |array(3) {
          |  [0]=>
          |  string(9) "changed.0"
          |  [1]=>
          |  string(9) "changed.1"
          |  [2]=>
          |  &string(9) "changed.2"
          |}
          |
          |Pushing elements onto an unreferenced array.
          |array(6) {
          |  [0]=>
          |  string(10) "original.1"
          |  [1]=>
          |  string(10) "original.2"
          |  [2]=>
          |  string(10) "original.3"
          |  [3]=>
          |  string(5) "new.0"
          |  [4]=>
          |  string(5) "new.1"
          |  [5]=>
          |  string(5) "new.2"
          |}
          |
          |Pushing elements onto an unreferenced array, using &$value.
          |Loop detected
          |array(15) {
          |  [0]=>
          |  string(10) "original.1"
          |  [1]=>
          |  string(10) "original.2"
          |  [2]=>
          |  string(10) "original.3"
          |  [3]=>
          |  string(5) "new.0"
          |  [4]=>
          |  string(5) "new.1"
          |  [5]=>
          |  string(5) "new.2"
          |  [6]=>
          |  string(5) "new.3"
          |  [7]=>
          |  string(5) "new.4"
          |  [8]=>
          |  string(5) "new.5"
          |  [9]=>
          |  string(5) "new.6"
          |  [10]=>
          |  string(5) "new.7"
          |  [11]=>
          |  &string(5) "new.8"
          |  [12]=>
          |  string(5) "new.9"
          |  [13]=>
          |  string(6) "new.10"
          |  [14]=>
          |  string(6) "new.11"
          |}
          |
          |Popping elements off an unreferenced array.
          |string(10) "original.1"
          |string(10) "original.2"
          |string(10) "original.3"
          |array(0) {
          |}
          |
          |Popping elements off an unreferenced array, using &$value.
          |string(10) "original.1"
          |string(10) "original.2"
          |array(1) {
          |  [0]=>
          |  string(10) "original.1"
          |}
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

    "Foreach loop tests - using an array element as the $value" in {
      // lang/foreachLoop.004.phpt
      script(
        """<?php
          |
          |$a=array("a", "b", "c");
          |$v=array();
          |foreach($a as $v[0]) {
          |	var_dump($v);
          |}
          |var_dump($a);
          |var_dump($v);
          |
          |echo "\n";
          |$a=array("a", "b", "c");
          |$v=array();
          |foreach($a as $k=>$v[0]) {
          |	var_dump($k, $v);
          |}
          |var_dump($a);
          |var_dump($k, $v);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """array(1) {
          |  [0]=>
          |  string(1) "a"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "b"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "c"
          |}
          |array(3) {
          |  [0]=>
          |  string(1) "a"
          |  [1]=>
          |  string(1) "b"
          |  [2]=>
          |  string(1) "c"
          |}
          |array(1) {
          |  [0]=>
          |  string(1) "c"
          |}
          |
          |int(0)
          |array(1) {
          |  [0]=>
          |  string(1) "a"
          |}
          |int(1)
          |array(1) {
          |  [0]=>
          |  string(1) "b"
          |}
          |int(2)
          |array(1) {
          |  [0]=>
          |  string(1) "c"
          |}
          |array(3) {
          |  [0]=>
          |  string(1) "a"
          |  [1]=>
          |  string(1) "b"
          |  [2]=>
          |  string(1) "c"
          |}
          |int(2)
          |array(1) {
          |  [0]=>
          |  string(1) "c"
          |}
          |""".stripMargin
      )
    }

    "Foreach loop tests - modifying the array during the loop: special case. Behaviour is good since php 5.2.2." in {
      // lang/foreachLoop.005.phpt
      script(
        """<?php
          |$a = array("original.0","original.1","original.2");
          |foreach ($a as $k=>&$v){
          |  $a[$k] = "changed.$k";
          |  echo "After changing \$a directly, \$v@$k is: $v\n";
          |}
          |//--- Expected output:
          |//After changing $a directly, $v@0 is: changed.0
          |//After changing $a directly, $v@1 is: changed.1
          |//After changing $a directly, $v@2 is: changed.2
          |//--- Actual output from php.net before 5.2.2:
          |//After changing $a directly, $v@0 is: changed.0
          |//After changing $a directly, $v@1 is: original.1
          |//After changing $a directly, $v@2 is: original.2
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """After changing $a directly, $v@0 is: changed.0
          |After changing $a directly, $v@1 is: changed.1
          |After changing $a directly, $v@2 is: changed.2
          |""".stripMargin
      )
    }

    "Foreach loop tests - error case: key is a reference." in {
      // lang/foreachLoop.006.phpt
      script(
        """<?php
          |$a = array("a","b","c");
          |foreach ($a as &$k=>$v) {
          |  var_dump($v);
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Key element cannot be a reference in /lang/ForeachSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "This test illustrates the impact of invoking destructors when refcount is decremented to 0 on foreach." in {
      // lang/foreachLoop.010.phpt
      script(
        """<?php
          |
          |$a = array(1,2,3);
          |$container = array(&$a);
          |
          |// From php.net:
          |//   "Unless the array is referenced, foreach operates on a copy of
          |//    the specified array and not the array itself."
          |// At this point, the array $a is referenced.
          |
          |// The following line ensures $a is no longer references as a consequence
          |// of running the 'destructor' on $container.
          |$container = null;
          |
          |// At this point the array $a is no longer referenced, so foreach should operate on a copy of the array.
          |// However, P8 does not invoke 'destructors' when refcount is decremented to 0.
          |// Consequently, $a thinks it is still referenced, and foreach will operate on the array itself.
          |// This provokes a difference in behaviour when changing the number of elements in the array while
          |// iterating over it.
          |
          |$i=0;
          |foreach ($a as $v) {
          |	array_push($a, 'new');
          |	var_dump($v);
          |
          |	if (++$i>10) {
          |		echo "Infinite loop detected\n";
          |		break;
          |	}
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(1)
          |int(2)
          |int(3)
          |""".stripMargin
      )
    }
  }
}
