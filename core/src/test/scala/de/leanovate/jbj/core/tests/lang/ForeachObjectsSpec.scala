/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class ForeachObjectsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "foreach with objects" should {
    "foreach with Iterator." in {
      // lang/foreachLoopIterator.001.phpt
      script(
        """<?php
          |
          |class MealIterator implements Iterator {
          |	private $pos=0;
          |	private $myContent=array("breakfast", "lunch", "dinner");
          |
          |	public function valid() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		return $this->pos<3;
          |	}
          |
          |	public function next() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		return $this->myContent[$this->pos++];
          |	}
          |
          |	public function rewind() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		$this->pos=0;
          |	}
          |
          |	public function current() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		return $this->myContent[$this->pos];
          |	}
          |
          |	public function key() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		return "meal " . $this->pos;
          |	}
          |
          |}
          |
          |$f = new MealIterator;
          |var_dump($f);
          |
          |echo "-----( Simple iteration: )-----\n";
          |foreach ($f as $k=>$v) {
          |	echo "$k => $v\n";
          |}
          |
          |$f->rewind();
          |
          |$indent = " ";
          |
          |echo "\n\n\n-----( Nested iteration: )-----\n";
          |$count=1;
          |foreach ($f as $k=>$v) {
          |	echo "\nTop level "  .  $count++ . ":\n";
          |	echo "$k => $v\n";
          |	$indent = "     ";
          |	foreach ($f as $k=>$v) {
          |		echo "     $k => $v\n";
          |	}
          |	$indent = " ";
          |
          |}
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """object(MealIterator)#1 (2) {
          |  ["pos":"MealIterator":private]=>
          |  int(0)
          |  ["myContent":"MealIterator":private]=>
          |  array(3) {
          |    [0]=>
          |    string(9) "breakfast"
          |    [1]=>
          |    string(5) "lunch"
          |    [2]=>
          |    string(6) "dinner"
          |  }
          |}
          |-----( Simple iteration: )-----
          |--> MealIterator::rewind (0)
          |--> MealIterator::valid (0)
          |--> MealIterator::current (0)
          |--> MealIterator::key (0)
          |meal 0 => breakfast
          |--> MealIterator::next (0)
          |--> MealIterator::valid (1)
          |--> MealIterator::current (1)
          |--> MealIterator::key (1)
          |meal 1 => lunch
          |--> MealIterator::next (1)
          |--> MealIterator::valid (2)
          |--> MealIterator::current (2)
          |--> MealIterator::key (2)
          |meal 2 => dinner
          |--> MealIterator::next (2)
          |--> MealIterator::valid (3)
          |--> MealIterator::rewind (3)
          |
          |
          |
          |-----( Nested iteration: )-----
          | --> MealIterator::rewind (0)
          | --> MealIterator::valid (0)
          | --> MealIterator::current (0)
          | --> MealIterator::key (0)
          |
          |Top level 1:
          |meal 0 => breakfast
          |     --> MealIterator::rewind (0)
          |     --> MealIterator::valid (0)
          |     --> MealIterator::current (0)
          |     --> MealIterator::key (0)
          |     meal 0 => breakfast
          |     --> MealIterator::next (0)
          |     --> MealIterator::valid (1)
          |     --> MealIterator::current (1)
          |     --> MealIterator::key (1)
          |     meal 1 => lunch
          |     --> MealIterator::next (1)
          |     --> MealIterator::valid (2)
          |     --> MealIterator::current (2)
          |     --> MealIterator::key (2)
          |     meal 2 => dinner
          |     --> MealIterator::next (2)
          |     --> MealIterator::valid (3)
          | --> MealIterator::next (3)
          |
          |Notice: Undefined offset: 3 in /lang/ForeachObjectsSpec.inlinePhp on line 16
          | --> MealIterator::valid (4)
          |===DONE===
          |""".stripMargin
      )
    }

    "Foreach loop on objects - basic loop with just value and key => value." in {
      // lang/foreachLoopObjects.001.phpt
      script(
        """<?php
          |
          |class C {
          |	public $a = "Original a";
          |	public $b = "Original b";
          |	public $c = "Original c";
          |	protected $d = "Original d";
          |	private $e = "Original e";
          |
          |}
          |
          |echo "\n\nSimple loop.\n";
          |$obj = new C;
          |foreach ($obj as $v) {
          |	var_dump($v);
          |}
          |foreach ($obj as $k => $v) {
          |	var_dump($k, $v);
          |}
          |echo "\nCheck key and value after the loop.\n";
          |var_dump($k, $v);
          |
          |
          |echo "\n\nObject instantiated inside loop.\n";
          |foreach (new C as $v) {
          |	var_dump($v);
          |}
          |foreach (new C as $k => $v) {
          |	var_dump($k, $v);
          |}
          |echo "\nCheck key and value after the loop.\n";
          |var_dump($k, $v);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |
          |Simple loop.
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(1) "a"
          |string(10) "Original a"
          |string(1) "b"
          |string(10) "Original b"
          |string(1) "c"
          |string(10) "Original c"
          |
          |Check key and value after the loop.
          |string(1) "c"
          |string(10) "Original c"
          |
          |
          |Object instantiated inside loop.
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(1) "a"
          |string(10) "Original a"
          |string(1) "b"
          |string(10) "Original b"
          |string(1) "c"
          |string(10) "Original c"
          |
          |Check key and value after the loop.
          |string(1) "c"
          |string(10) "Original c"
          |""".stripMargin
      )
    }
  }
}
