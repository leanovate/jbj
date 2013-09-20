/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class IteratorsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "iterators" should {
    "ZE2 iterators and foreach" in {
      // classes/iterators_001.phpt
      script(
        """<?php
          |class c_iter implements Iterator {
          |
          |	private $obj;
          |	private $num = 0;
          |
          |	function __construct($obj) {
          |		echo __METHOD__ . "\n";
          |		$this->num = 0;
          |		$this->obj = $obj;
          |	}
          |	function rewind() {
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
          |}
          |
          |echo "===Array===\n";
          |
          |$a = array(0,1,2);
          |foreach($a as $v) {
          |	echo "array:$v\n";
          |}
          |
          |echo "===Manual===\n";
          |$t = new c();
          |for ($iter = $t->getIterator(); $iter->valid(); $iter->next()) {
          |	echo $iter->current() . "\n";
          |}
          |
          |echo "===foreach/std===\n";
          |foreach($t as $v) {
          |	echo "object:$v\n";
          |}
          |
          |echo "===foreach/rec===\n";
          |foreach($t as $v) {
          |	foreach($t as $w) {
          |		echo "double:$v:$w\n";
          |	}
          |}
          |
          |echo "===foreach/key===\n";
          |foreach($t as $i => $v) {
          |	echo "object:$i=>$v\n";
          |}
          |
          |print "Done\n";
          |exit(0);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """===Array===
          |array:0
          |array:1
          |array:2
          |===Manual===
          |c::getIterator
          |c_iter::__construct
          |c_iter::valid = true
          |c_iter::current
          |0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |1
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |2
          |c_iter::next
          |c_iter::valid = false
          |===foreach/std===
          |c::getIterator
          |c_iter::__construct
          |c_iter::valid = true
          |c_iter::current
          |object:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |object:1
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |object:2
          |c_iter::next
          |c_iter::valid = false
          |===foreach/rec===
          |c::getIterator
          |c_iter::__construct
          |c_iter::valid = true
          |c_iter::current
          |c::getIterator
          |c_iter::__construct
          |c_iter::valid = true
          |c_iter::current
          |double:0:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |double:0:1
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |double:0:2
          |c_iter::next
          |c_iter::valid = false
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c::getIterator
          |c_iter::__construct
          |c_iter::valid = true
          |c_iter::current
          |double:1:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |double:1:1
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |double:1:2
          |c_iter::next
          |c_iter::valid = false
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c::getIterator
          |c_iter::__construct
          |c_iter::valid = true
          |c_iter::current
          |double:2:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |double:2:1
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |double:2:2
          |c_iter::next
          |c_iter::valid = false
          |c_iter::next
          |c_iter::valid = false
          |===foreach/key===
          |c::getIterator
          |c_iter::__construct
          |c_iter::valid = true
          |c_iter::current
          |c_iter::key
          |object:1st=>0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c_iter::key
          |object:2nd=>1
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c_iter::key
          |object:3rd=>2
          |c_iter::next
          |c_iter::valid = false
          |Done
          |""".stripMargin
      )
    }

    "ZE2 iterators and break" in {
      // classes/iterators_002.phpt
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
          |		echo __METHOD__ . "\n";
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
          |		echo __METHOD__ . "\n";
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
          |print "Done\n";
          |?>
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
          |c_iter::__destruct
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
          |c_iter::__destruct
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
          |c_iter::__destruct
          |c_iter::next
          |c_iter::valid = false
          |c_iter::__destruct
          |c::__destruct
          |Done
          |""".stripMargin
      )
    }

    "ZE2 iterators and break" in {
      // classes/iterators_003.phpt
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
          |		return $this->num;
          |	}
          |}
          |
          |class c implements IteratorAggregate {
          |
          |	public $max = 4;
          |
          |	function getIterator() {
          |		echo __METHOD__ . "\n";
          |		return new c_iter($this);
          |	}
          |}
          |
          |$t = new c();
          |
          |foreach($t as $v) {
          |	if ($v == 0) {
          |		echo "continue outer\n";
          |		continue;
          |	}
          |	foreach($t as $w) {
          |		if ($w == 1) {
          |			echo "continue inner\n";
          |			continue;
          |		}
          |		if ($w == 2) {
          |			echo "break inner\n";
          |			break;
          |		}
          |		echo "double:$v:$w\n";
          |	}
          |	if ($v == 2) {
          |		echo "break outer\n";
          |		break;
          |	}
          |}
          |
          |print "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """c::getIterator
          |c_iter::__construct
          |c_iter::rewind
          |c_iter::valid = true
          |c_iter::current
          |continue outer
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c::getIterator
          |c_iter::__construct
          |c_iter::rewind
          |c_iter::valid = true
          |c_iter::current
          |double:1:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |continue inner
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |break inner
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |c::getIterator
          |c_iter::__construct
          |c_iter::rewind
          |c_iter::valid = true
          |c_iter::current
          |double:2:0
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |continue inner
          |c_iter::next
          |c_iter::valid = true
          |c_iter::current
          |break inner
          |break outer
          |Done
          |""".stripMargin
      )
    }

    "ZE2 iterators and array wrapping" in {
      // classes/iterators_006.phpt
      script(
        """<?php
          |
          |class ai implements Iterator {
          |
          |	private $array;
          |
          |	function __construct() {
          |		$this->array = array('foo', 'bar', 'baz');
          |	}
          |
          |	function rewind() {
          |		reset($this->array);
          |		$this->next();
          |	}
          |
          |	function valid() {
          |		return $this->key !== NULL;
          |	}
          |
          |	function key() {
          |		return $this->key;
          |	}
          |
          |	function current() {
          |		return $this->current;
          |	}
          |
          |	function next() {
          |		list($this->key, $this->current) = each($this->array);
          |//		list($key, $current) = each($this->array);
          |//		$this->key = $key;
          |//		$this->current = $current;
          |	}
          |}
          |
          |class a implements IteratorAggregate {
          |
          |	public function getIterator() {
          |		return new ai();
          |	}
          |}
          |
          |$array = new a();
          |
          |foreach ($array as $property => $value) {
          |	print "$property: $value\n";
          |}
          |
          |#$array = $array->getIterator();
          |#$array->rewind();
          |#$array->valid();
          |#var_dump($array->key());
          |#var_dump($array->current());
          |echo "===2nd===\n";
          |
          |$array = new ai();
          |
          |foreach ($array as $property => $value) {
          |	print "$property: $value\n";
          |}
          |
          |echo "===3rd===\n";
          |
          |foreach ($array as $property => $value) {
          |	print "$property: $value\n";
          |}
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """0: foo
          |1: bar
          |2: baz
          |===2nd===
          |0: foo
          |1: bar
          |2: baz
          |===3rd===
          |0: foo
          |1: bar
          |2: baz
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 iterators and exceptions" in {
      // classes/iterators_007.phpt
      script(
        """<?php
          |class Test implements Iterator
          |{
          |	public $arr = array(1, 2, 3);
          |	public $x = 0;
          |
          |	public function rewind()    { if ($this->x == 0) throw new Exception(__METHOD__); reset($this->arr); }
          |	public function current()   { if ($this->x == 1) throw new Exception(__METHOD__); return current($this->arr); }
          |	public function key()       { if ($this->x == 2) throw new Exception(__METHOD__); return key($this->arr); }
          |	public function next()      { if ($this->x == 3) throw new Exception(__METHOD__); next($this->arr); }
          |	public function valid()     { if ($this->x == 4) throw new Exception(__METHOD__); return (key($this->arr) !== NULL); }
          |}
          |
          |$t = new Test();
          |
          |while($t->x < 5)
          |{
          |	try
          |	{
          |	    foreach($t as $k => $v)
          |	    {
          |	        echo "Current\n";
          |	    }
          |	}
          |	catch(Exception $e)
          |	{
          |	    echo "Caught in " . $e->getMessage() . "()\n";
          |	}
          |	$t->x++;
          |}
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Caught in Test::rewind()
          |Caught in Test::current()
          |Caught in Test::key()
          |Current
          |Caught in Test::next()
          |Caught in Test::valid()
          |===DONE===
          |""".stripMargin
      )
    }

    "Ensure plain userspace superclass does not override special iterator behaviour on child class." in {
      // classes/iterators_008.phpt
      script(
        """<?php
          |Class C {}
          |
          |class D extends C implements Iterator {
          |
          |  private $counter = 2;
          |
          |  public function valid() {
          |    echo __METHOD__ . "($this->counter)\n";
          |    return $this->counter;
          |  }
          |
          |  public function next() {
          |    $this->counter--;
          |    echo __METHOD__ . "($this->counter)\n";
          |  }
          |
          |  public function rewind() {
          |    echo __METHOD__ . "($this->counter)\n";
          |  }
          |
          |  public function current() {
          |    echo __METHOD__ . "($this->counter)\n";
          |  }
          |
          |  public function key() {
          |    echo __METHOD__ . "($this->counter)\n";
          |  }
          |
          |}
          |
          |foreach (new D as $x) {}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """D::rewind(2)
          |D::valid(2)
          |D::current(2)
          |D::next(1)
          |D::valid(1)
          |D::current(1)
          |D::next(0)
          |D::valid(0)
          |""".stripMargin
      )
    }
  }
}
