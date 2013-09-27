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

    "foreach with iterator and &$value reference" in {
      // lang/foreachLoopIterator.002.phpt
      script(
        """<?php
          |
          |class MyIterator implements Iterator {
          |	public function valid() { return true; }
          |	public function next() {	}
          |	public function rewind() {	}
          |	public function current() {	}
          |	public function key() {	}
          |}
          |
          |$f = new MyIterator;
          |echo "-----( Try to iterate with &\$value: )-----\n";
          |foreach ($f as $k=>&$v) {
          |	echo "$k => $v\n";
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """-----( Try to iterate with &$value: )-----
          |
          |Fatal error: An iterator cannot be used with foreach by reference in /lang/ForeachObjectsSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "foreach with iteratorAggregate" in {
      // lang/foreachLoopIteratorAggregate.001.phpt
      script(
        """<?php
          |class EnglishMealIterator implements Iterator {
          |	private $pos=0;
          |	private $myContent=array("breakfast", "dinner", "tea");
          |
          |	public function valid() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		return $this->pos < count($this->myContent);
          |	}
          |
          |	public function next() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		$this->pos++;
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
          |class FrenchMealIterator implements Iterator {
          |	private $pos=0;
          |	private $myContent=array("petit dejeuner", "dejeuner", "gouter", "dinner");
          |
          |	public function valid() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		return $this->pos < count($this->myContent);
          |	}
          |
          |	public function next() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__ . " ($this->pos)\n";
          |		$this->pos++;
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
          |
          |Class EuropeanMeals implements IteratorAggregate {
          |
          |	private $storedEnglishMealIterator;
          |	private $storedFrenchMealIterator;
          |
          |	public function __construct() {
          |		$this->storedEnglishMealIterator = new EnglishMealIterator;
          |		$this->storedFrenchMealIterator = new FrenchMealIterator;
          |	}
          |
          |	public function getIterator() {
          |		global $indent;
          |		echo "$indent--> " . __METHOD__  . "\n";
          |
          |		//Alternate between English and French meals
          |		static $i = 0;
          |		if ($i++%2 == 0) {
          |			return $this->storedEnglishMealIterator;
          |		} else {
          |			return $this->storedFrenchMealIterator;
          |		}
          |	}
          |
          |}
          |
          |$f = new EuropeanMeals;
          |var_dump($f);
          |
          |echo "-----( Simple iteration 1: )-----\n";
          |foreach ($f as $k=>$v) {
          |	echo "$k => $v\n";
          |}
          |echo "-----( Simple iteration 2: )-----\n";
          |foreach ($f as $k=>$v) {
          |	echo "$k => $v\n";
          |}
          |
          |
          |$indent = " ";
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
          |}
          |
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """object(EuropeanMeals)#1 (2) {
          |  ["storedEnglishMealIterator":"EuropeanMeals":private]=>
          |  object(EnglishMealIterator)#2 (2) {
          |    ["pos":"EnglishMealIterator":private]=>
          |    int(0)
          |    ["myContent":"EnglishMealIterator":private]=>
          |    array(3) {
          |      [0]=>
          |      string(9) "breakfast"
          |      [1]=>
          |      string(6) "dinner"
          |      [2]=>
          |      string(3) "tea"
          |    }
          |  }
          |  ["storedFrenchMealIterator":"EuropeanMeals":private]=>
          |  object(FrenchMealIterator)#3 (2) {
          |    ["pos":"FrenchMealIterator":private]=>
          |    int(0)
          |    ["myContent":"FrenchMealIterator":private]=>
          |    array(4) {
          |      [0]=>
          |      string(14) "petit dejeuner"
          |      [1]=>
          |      string(8) "dejeuner"
          |      [2]=>
          |      string(6) "gouter"
          |      [3]=>
          |      string(6) "dinner"
          |    }
          |  }
          |}
          |-----( Simple iteration 1: )-----
          |--> EuropeanMeals::getIterator
          |--> EnglishMealIterator::rewind (0)
          |--> EnglishMealIterator::valid (0)
          |--> EnglishMealIterator::current (0)
          |--> EnglishMealIterator::key (0)
          |meal 0 => breakfast
          |--> EnglishMealIterator::next (0)
          |--> EnglishMealIterator::valid (1)
          |--> EnglishMealIterator::current (1)
          |--> EnglishMealIterator::key (1)
          |meal 1 => dinner
          |--> EnglishMealIterator::next (1)
          |--> EnglishMealIterator::valid (2)
          |--> EnglishMealIterator::current (2)
          |--> EnglishMealIterator::key (2)
          |meal 2 => tea
          |--> EnglishMealIterator::next (2)
          |--> EnglishMealIterator::valid (3)
          |-----( Simple iteration 2: )-----
          |--> EuropeanMeals::getIterator
          |--> FrenchMealIterator::rewind (0)
          |--> FrenchMealIterator::valid (0)
          |--> FrenchMealIterator::current (0)
          |--> FrenchMealIterator::key (0)
          |meal 0 => petit dejeuner
          |--> FrenchMealIterator::next (0)
          |--> FrenchMealIterator::valid (1)
          |--> FrenchMealIterator::current (1)
          |--> FrenchMealIterator::key (1)
          |meal 1 => dejeuner
          |--> FrenchMealIterator::next (1)
          |--> FrenchMealIterator::valid (2)
          |--> FrenchMealIterator::current (2)
          |--> FrenchMealIterator::key (2)
          |meal 2 => gouter
          |--> FrenchMealIterator::next (2)
          |--> FrenchMealIterator::valid (3)
          |--> FrenchMealIterator::current (3)
          |--> FrenchMealIterator::key (3)
          |meal 3 => dinner
          |--> FrenchMealIterator::next (3)
          |--> FrenchMealIterator::valid (4)
          |
          |
          |
          |-----( Nested iteration: )-----
          | --> EuropeanMeals::getIterator
          | --> EnglishMealIterator::rewind (3)
          | --> EnglishMealIterator::valid (0)
          | --> EnglishMealIterator::current (0)
          | --> EnglishMealIterator::key (0)
          |
          |Top level 1:
          |meal 0 => breakfast
          |     --> EuropeanMeals::getIterator
          |     --> FrenchMealIterator::rewind (4)
          |     --> FrenchMealIterator::valid (0)
          |     --> FrenchMealIterator::current (0)
          |     --> FrenchMealIterator::key (0)
          |     meal 0 => petit dejeuner
          |     --> FrenchMealIterator::next (0)
          |     --> FrenchMealIterator::valid (1)
          |     --> FrenchMealIterator::current (1)
          |     --> FrenchMealIterator::key (1)
          |     meal 1 => dejeuner
          |     --> FrenchMealIterator::next (1)
          |     --> FrenchMealIterator::valid (2)
          |     --> FrenchMealIterator::current (2)
          |     --> FrenchMealIterator::key (2)
          |     meal 2 => gouter
          |     --> FrenchMealIterator::next (2)
          |     --> FrenchMealIterator::valid (3)
          |     --> FrenchMealIterator::current (3)
          |     --> FrenchMealIterator::key (3)
          |     meal 3 => dinner
          |     --> FrenchMealIterator::next (3)
          |     --> FrenchMealIterator::valid (4)
          | --> EnglishMealIterator::next (0)
          | --> EnglishMealIterator::valid (1)
          | --> EnglishMealIterator::current (1)
          | --> EnglishMealIterator::key (1)
          |
          |Top level 2:
          |meal 1 => dinner
          |     --> EuropeanMeals::getIterator
          |     --> EnglishMealIterator::rewind (1)
          |     --> EnglishMealIterator::valid (0)
          |     --> EnglishMealIterator::current (0)
          |     --> EnglishMealIterator::key (0)
          |     meal 0 => breakfast
          |     --> EnglishMealIterator::next (0)
          |     --> EnglishMealIterator::valid (1)
          |     --> EnglishMealIterator::current (1)
          |     --> EnglishMealIterator::key (1)
          |     meal 1 => dinner
          |     --> EnglishMealIterator::next (1)
          |     --> EnglishMealIterator::valid (2)
          |     --> EnglishMealIterator::current (2)
          |     --> EnglishMealIterator::key (2)
          |     meal 2 => tea
          |     --> EnglishMealIterator::next (2)
          |     --> EnglishMealIterator::valid (3)
          | --> EnglishMealIterator::next (3)
          | --> EnglishMealIterator::valid (4)
          |===DONE===
          |""".stripMargin
      )
    }

    "IteratorAggregate::getIterator bad return type" in {
      // lang/foreachLoopIteratorAggregate.002.phpt
      script(
        """<?php
          |
          |class bad1 implements IteratorAggregate {
          |	function getIterator() {
          |		return null;
          |	}
          |}
          |
          |class bad2 implements IteratorAggregate {
          |	function getIterator() {
          |		return new stdClass;
          |	}
          |}
          |
          |class bad3 implements IteratorAggregate {
          |	function getIterator() {
          |		return 1;
          |	}
          |}
          |
          |class bad4 implements IteratorAggregate {
          |	function getIterator() {
          |		return array(1,2,3);
          |	}
          |}
          |
          |
          |function f($className) {
          |	try {
          |		foreach (new $className as $k=>$v) {
          |			echo "$k => $v\n";
          |		}
          |	} catch (Exception $e) {
          |			echo $e->getLine() . ": " . $e->getMessage() ."\n";
          |	}
          |}
          |
          |f("bad1");
          |f("bad2");
          |f("bad3");
          |f("bad4");
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """30: Objects returned by bad1::getIterator() must be traversable or implement interface Iterator
          |30: Objects returned by bad2::getIterator() must be traversable or implement interface Iterator
          |30: Objects returned by bad3::getIterator() must be traversable or implement interface Iterator
          |30: Objects returned by bad4::getIterator() must be traversable or implement interface Iterator
          |===DONE===
          |""".stripMargin
      )
    }

    "foreach with nested iteratorAggregates" in {
      // lang/foreachLoopIteratorAggregate.003.phpt
      script(
        """<?php
          |class EnglishMealIterator implements Iterator {
          |	private $pos=0;
          |	private $myContent=array("breakfast", "dinner", "tea");
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
          |class A1 implements IteratorAggregate {
          |	function getIterator() {
          |		return new EnglishMealIterator;
          |	}
          |}
          |
          |class A2 implements IteratorAggregate {
          |	function getIterator() {
          |		return new A1;
          |	}
          |}
          |
          |class A3 implements IteratorAggregate {
          |	function getIterator() {
          |		return new A2;
          |	}
          |}
          |
          |echo "\n-----( A1: )-----\n";
          |foreach (new A1 as $k=>$v) {
          |	echo "$k => $v\n";
          |}
          |
          |echo "\n-----( A2: )-----\n";
          |foreach (new A2 as $k=>$v) {
          |	echo "$k => $v\n";
          |}
          |
          |echo "\n-----( A3: )-----\n";
          |foreach (new A3 as $k=>$v) {
          |	echo "$k => $v\n";
          |}
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |-----( A1: )-----
          |--> EnglishMealIterator::rewind (0)
          |--> EnglishMealIterator::valid (0)
          |--> EnglishMealIterator::current (0)
          |--> EnglishMealIterator::key (0)
          |meal 0 => breakfast
          |--> EnglishMealIterator::next (0)
          |--> EnglishMealIterator::valid (1)
          |--> EnglishMealIterator::current (1)
          |--> EnglishMealIterator::key (1)
          |meal 1 => dinner
          |--> EnglishMealIterator::next (1)
          |--> EnglishMealIterator::valid (2)
          |--> EnglishMealIterator::current (2)
          |--> EnglishMealIterator::key (2)
          |meal 2 => tea
          |--> EnglishMealIterator::next (2)
          |--> EnglishMealIterator::valid (3)
          |
          |-----( A2: )-----
          |--> EnglishMealIterator::rewind (0)
          |--> EnglishMealIterator::valid (0)
          |--> EnglishMealIterator::current (0)
          |--> EnglishMealIterator::key (0)
          |meal 0 => breakfast
          |--> EnglishMealIterator::next (0)
          |--> EnglishMealIterator::valid (1)
          |--> EnglishMealIterator::current (1)
          |--> EnglishMealIterator::key (1)
          |meal 1 => dinner
          |--> EnglishMealIterator::next (1)
          |--> EnglishMealIterator::valid (2)
          |--> EnglishMealIterator::current (2)
          |--> EnglishMealIterator::key (2)
          |meal 2 => tea
          |--> EnglishMealIterator::next (2)
          |--> EnglishMealIterator::valid (3)
          |
          |-----( A3: )-----
          |--> EnglishMealIterator::rewind (0)
          |--> EnglishMealIterator::valid (0)
          |--> EnglishMealIterator::current (0)
          |--> EnglishMealIterator::key (0)
          |meal 0 => breakfast
          |--> EnglishMealIterator::next (0)
          |--> EnglishMealIterator::valid (1)
          |--> EnglishMealIterator::current (1)
          |--> EnglishMealIterator::key (1)
          |meal 1 => dinner
          |--> EnglishMealIterator::next (1)
          |--> EnglishMealIterator::valid (2)
          |--> EnglishMealIterator::current (2)
          |--> EnglishMealIterator::key (2)
          |meal 2 => tea
          |--> EnglishMealIterator::next (2)
          |--> EnglishMealIterator::valid (3)
          |===DONE===
          |""".stripMargin
      )
    }

    "Duplicate of zend test tests/classes/iterators_002.phpt without expected output from destructor" in {
      // lang/foreachLoopIteratorAggregate.004.phpt
      script(
        """<?php
          |class c_iter implements Iterator {
          |
          |	private $obj;
          |	private $num = 0;
          |
          |	function __construct($obj) {
          |		echo __METHOD__ . "\n";
          |		$this->obj = $obj;
          |	}
          |	function rewind() {
          |		echo __METHOD__ . "\n";
          |		$this->num = 0;
          |	}
          |	function valid() {
          |		$more = $this->num < $this->obj->max;
          |		echo __METHOD__ . ' = ' .($more ? 'true' : 'false') . "\n";
          |		return $more;
          |	}
          |	function current() {
          |		echo __METHOD__ . "\n";
          |		return $this->num;
          |	}
          |	function next() {
          |		echo __METHOD__ . "\n";
          |		$this->num++;
          |	}
          |	function key() {
          |		echo __METHOD__ . "\n";
          |		switch($this->num) {
          |			case 0: return "1st";
          |			case 1: return "2nd";
          |			case 2: return "3rd";
          |			default: return "???";
          |		}
          |	}
          |	function __destruct() {
          |	}
          |}
          |
          |class c implements IteratorAggregate {
          |
          |	public $max = 3;
          |
          |	function getIterator() {
          |		echo __METHOD__ . "\n";
          |		return new c_iter($this);
          |	}
          |	function __destruct() {
          |	}
          |}
          |
          |$t = new c();
          |
          |foreach($t as $k => $v) {
          |	foreach($t as $w) {
          |		echo "double:$v:$w\n";
          |		break;
          |	}
          |}
          |
          |unset($t);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """c::getIterator
          |c_iter::__construct
          |c_iter::rewind
          |c_iter::valid = true
          |c_iter::current
          |c_iter::key
          |c::getIterator
          |c_iter::__construct
          |c_iter::rewind
          |c_iter::valid = true
          |c_iter::current
          |double:0:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c_iter::key
          |c::getIterator
          |c_iter::__construct
          |c_iter::rewind
          |c_iter::valid = true
          |c_iter::current
          |double:1:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c_iter::key
          |c::getIterator
          |c_iter::__construct
          |c_iter::rewind
          |c_iter::valid = true
          |c_iter::current
          |double:2:0
          |c_iter::next
          |c_iter::valid = false
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

    "Foreach loop tests - visibility." in {
      // lang/foreachLoopObjects.002.phpt
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
          |	function doForEachC() {
          |		echo "in C::doForEachC\n";
          |		foreach ($this as $k=>&$v) {
          |			var_dump($v);
          |			$v="changed.$k";
          |		}
          |	}
          |
          |	static function doForEach($obj) {
          |		echo "in C::doForEach\n";
          |		foreach ($obj as $k=>&$v) {
          |			var_dump($v);
          |			$v="changed.$k";
          |		}
          |	}
          |
          |	function doForEachOnThis() {
          |		echo "in C::doForEachOnThis\n";
          |		foreach ($this as $k=>&$v) {
          |			var_dump($v);
          |			$v="changed.$k";
          |		}
          |	}
          |
          |}
          |
          |class D extends C {
          |
          |	private $f = "Original f";
          |	protected $g = "Original g";
          |
          |	static function doForEach($obj) {
          |		echo "in D::doForEach\n";
          |		foreach ($obj as $k=>&$v) {
          |			var_dump($v);
          |			$v="changed.$k";
          |		}
          |	}
          |
          |	function doForEachOnThis() {
          |		echo "in D::doForEachOnThis\n";
          |		foreach ($this as $k=>&$v) {
          |			var_dump($v);
          |			$v="changed.$k";
          |		}
          |	}
          |}
          |
          |class E extends D {
          |	public $a = "Overridden a";
          |	public $b = "Overridden b";
          |	public $c = "Overridden c";
          |	protected $d = "Overridden d";
          |	private $e = "Overridden e";
          |
          |	static function doForEach($obj) {
          |		echo "in E::doForEach\n";
          |		foreach ($obj as $k=>&$v) {
          |			var_dump($v);
          |			$v="changed.$k";
          |		}
          |	}
          |
          |	function doForEachOnThis() {
          |		echo "in E::doForEachOnThis\n";
          |		foreach ($this as $k=>&$v) {
          |			var_dump($v);
          |			$v="changed.$k";
          |		}
          |	}
          |}
          |
          |echo "\n\nIterate over various generations from within overridden methods:\n";
          |echo "\n--> Using instance of C:\n";
          |$myC = new C;
          |$myC->doForEachOnThis();
          |var_dump($myC);
          |echo "\n--> Using instance of D:\n";
          |$myD = new D;
          |$myD->doForEachOnThis();
          |var_dump($myD);
          |echo "\n--> Using instance of E:\n";
          |$myE = new E;
          |$myE->doForEachOnThis();
          |var_dump($myE);
          |
          |echo "\n\nIterate over various generations from within an inherited method:\n";
          |echo "\n--> Using instance of C:\n";
          |$myC = new C;
          |$myC->doForEachC();
          |var_dump($myC);
          |echo "\n--> Using instance of D:\n";
          |$myD = new D;
          |$myD->doForEachC();
          |var_dump($myD);
          |echo "\n--> Using instance of E:\n";
          |$myE = new E;
          |$myE->doForEachC();
          |var_dump($myE);
          |
          |echo "\n\nIterate over various generations from within an overridden static method:\n";
          |echo "\n--> Using instance of C:\n";
          |$myC = new C;
          |C::doForEach($myC);
          |var_dump($myC);
          |$myC = new C;
          |D::doForEach($myC);
          |var_dump($myC);
          |$myC = new C;
          |E::doForEach($myC);
          |var_dump($myC);
          |echo "\n--> Using instance of D:\n";
          |$myD = new D;
          |C::doForEach($myD);
          |var_dump($myD);
          |$myD = new D;
          |D::doForEach($myD);
          |var_dump($myD);
          |$myD = new D;
          |E::doForEach($myD);
          |var_dump($myD);
          |echo "\n--> Using instance of E:\n";
          |$myE = new E;
          |C::doForEach($myE);
          |var_dump($myE);
          |$myE = new E;
          |D::doForEach($myE);
          |var_dump($myE);
          |$myE = new E;
          |E::doForEach($myE);
          |var_dump($myE);
          |
          |
          |echo "\n\nIterate over various generations from outside the object:\n";
          |echo "\n--> Using instance of C:\n";
          |$myC = new C;
          |foreach ($myC as $k=>&$v) {
          |	var_dump($v);
          |	$v="changed.$k";
          |}
          |var_dump($myC);
          |echo "\n--> Using instance of D:\n";
          |$myD = new D;
          |foreach ($myD as $k=>&$v) {
          |	var_dump($v);
          |	$v="changed.$k";
          |}
          |var_dump($myD);
          |echo "\n--> Using instance of E:\n";
          |$myE = new E;
          |foreach ($myE as $k=>&$v) {
          |	var_dump($v);
          |	$v="changed.$k";
          |}
          |var_dump($myE);
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |
          |Iterate over various generations from within overridden methods:
          |
          |--> Using instance of C:
          |in C::doForEachOnThis
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |string(10) "Original e"
          |object(C)#1 (5) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(9) "changed.e"
          |}
          |
          |--> Using instance of D:
          |in D::doForEachOnThis
          |string(10) "Original f"
          |string(10) "Original g"
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |object(D)#2 (7) {
          |  ["f":"D":private]=>
          |  string(9) "changed.f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |
          |--> Using instance of E:
          |in E::doForEachOnThis
          |string(12) "Overridden a"
          |string(12) "Overridden b"
          |string(12) "Overridden c"
          |string(12) "Overridden d"
          |string(12) "Overridden e"
          |string(10) "Original g"
          |object(E)#3 (8) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"E":private]=>
          |  string(9) "changed.e"
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |
          |
          |Iterate over various generations from within an inherited method:
          |
          |--> Using instance of C:
          |in C::doForEachC
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |string(10) "Original e"
          |object(C)#4 (5) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(9) "changed.e"
          |}
          |
          |--> Using instance of D:
          |in C::doForEachC
          |string(10) "Original g"
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |string(10) "Original e"
          |object(D)#5 (7) {
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(9) "changed.e"
          |}
          |
          |--> Using instance of E:
          |in C::doForEachC
          |string(12) "Overridden a"
          |string(12) "Overridden b"
          |string(12) "Overridden c"
          |string(12) "Overridden d"
          |string(10) "Original g"
          |string(10) "Original e"
          |object(E)#6 (8) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"E":private]=>
          |  string(12) "Overridden e"
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["e":"C":private]=>
          |  string(9) "changed.e"
          |}
          |
          |
          |Iterate over various generations from within an overridden static method:
          |
          |--> Using instance of C:
          |in C::doForEach
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |string(10) "Original e"
          |object(C)#7 (5) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(9) "changed.e"
          |}
          |in D::doForEach
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |object(C)#8 (5) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |in E::doForEach
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |object(C)#9 (5) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |
          |--> Using instance of D:
          |in C::doForEach
          |string(10) "Original g"
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |string(10) "Original e"
          |object(D)#10 (7) {
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(9) "changed.e"
          |}
          |in D::doForEach
          |string(10) "Original f"
          |string(10) "Original g"
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |object(D)#11 (7) {
          |  ["f":"D":private]=>
          |  string(9) "changed.f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |in E::doForEach
          |string(10) "Original g"
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(10) "Original d"
          |object(D)#12 (7) {
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |
          |--> Using instance of E:
          |in C::doForEach
          |string(12) "Overridden a"
          |string(12) "Overridden b"
          |string(12) "Overridden c"
          |string(12) "Overridden d"
          |string(10) "Original g"
          |string(10) "Original e"
          |object(E)#13 (8) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"E":private]=>
          |  string(12) "Overridden e"
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["e":"C":private]=>
          |  string(9) "changed.e"
          |}
          |in D::doForEach
          |string(12) "Overridden a"
          |string(12) "Overridden b"
          |string(12) "Overridden c"
          |string(12) "Overridden d"
          |string(10) "Original f"
          |string(10) "Original g"
          |object(E)#14 (8) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"E":private]=>
          |  string(12) "Overridden e"
          |  ["f":"D":private]=>
          |  string(9) "changed.f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |in E::doForEach
          |string(12) "Overridden a"
          |string(12) "Overridden b"
          |string(12) "Overridden c"
          |string(12) "Overridden d"
          |string(12) "Overridden e"
          |string(10) "Original g"
          |object(E)#15 (8) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  string(9) "changed.c"
          |  ["d":protected]=>
          |  string(9) "changed.d"
          |  ["e":"E":private]=>
          |  string(9) "changed.e"
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(9) "changed.g"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |
          |
          |Iterate over various generations from outside the object:
          |
          |--> Using instance of C:
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |object(C)#16 (5) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  &string(9) "changed.c"
          |  ["d":protected]=>
          |  string(10) "Original d"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |
          |--> Using instance of D:
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |object(D)#17 (7) {
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(10) "Original g"
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  &string(9) "changed.c"
          |  ["d":protected]=>
          |  string(10) "Original d"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |
          |--> Using instance of E:
          |string(12) "Overridden a"
          |string(12) "Overridden b"
          |string(12) "Overridden c"
          |object(E)#18 (8) {
          |  ["a"]=>
          |  string(9) "changed.a"
          |  ["b"]=>
          |  string(9) "changed.b"
          |  ["c"]=>
          |  &string(9) "changed.c"
          |  ["d":protected]=>
          |  string(12) "Overridden d"
          |  ["e":"E":private]=>
          |  string(12) "Overridden e"
          |  ["f":"D":private]=>
          |  string(10) "Original f"
          |  ["g":protected]=>
          |  string(10) "Original g"
          |  ["e":"C":private]=>
          |  string(10) "Original e"
          |}
          |===DONE===
          |""".stripMargin
      )
    }
  }
}
