/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class ArrayAccessSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "object array access" should {
    "ZE2 ArrayAccess" in {
      // classes/array_access_001.phpt
      script(
        """<?php
          |class object implements ArrayAccess {
          |
          |	public $a = array('1st', 1, 2=>'3rd', '4th'=>4);
          |
          |	function offsetExists($index) {
          |		echo __METHOD__ . "($index)\n";
          |		return array_key_exists($index, $this->a);
          |	}
          |	function offsetGet($index) {
          |		echo __METHOD__ . "($index)\n";
          |		return $this->a[$index];
          |	}
          |	function offsetSet($index, $newval) {
          |		echo __METHOD__ . "($index,$newval)\n";
          |		return $this->a[$index] = $newval;
          |	}
          |	function offsetUnset($index) {
          |		echo __METHOD__ . "($index)\n";
          |		unset($this->a[$index]);
          |	}
          |}
          |
          |$obj = new Object;
          |
          |var_dump($obj->a);
          |
          |echo "===EMPTY===\n";
          |var_dump(empty($obj[0]));
          |var_dump(empty($obj[1]));
          |var_dump(empty($obj[2]));
          |var_dump(empty($obj['4th']));
          |var_dump(empty($obj['5th']));
          |var_dump(empty($obj[6]));
          |
          |echo "===isset===\n";
          |var_dump(isset($obj[0]));
          |var_dump(isset($obj[1]));
          |var_dump(isset($obj[2]));
          |var_dump(isset($obj['4th']));
          |var_dump(isset($obj['5th']));
          |var_dump(isset($obj[6]));
          |
          |echo "===offsetGet===\n";
          |var_dump($obj[0]);
          |var_dump($obj[1]);
          |var_dump($obj[2]);
          |var_dump($obj['4th']);
          |var_dump($obj['5th']);
          |var_dump($obj[6]);
          |
          |echo "===offsetSet===\n";
          |echo "WRITE 1\n";
          |$obj[1] = 'Changed 1';
          |var_dump($obj[1]);
          |echo "WRITE 2\n";
          |$obj['4th'] = 'Changed 4th';
          |var_dump($obj['4th']);
          |echo "WRITE 3\n";
          |$obj['5th'] = 'Added 5th';
          |var_dump($obj['5th']);
          |echo "WRITE 4\n";
          |$obj[6] = 'Added 6';
          |var_dump($obj[6]);
          |
          |var_dump($obj[0]);
          |var_dump($obj[2]);
          |
          |$x = $obj[6] = 'changed 6';
          |var_dump($obj[6]);
          |var_dump($x);
          |
          |echo "===unset===\n";
          |var_dump($obj->a);
          |unset($obj[2]);
          |unset($obj['4th']);
          |unset($obj[7]);
          |unset($obj['8th']);
          |var_dump($obj->a);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """array(4) {
          |  [0]=>
          |  string(3) "1st"
          |  [1]=>
          |  int(1)
          |  [2]=>
          |  string(3) "3rd"
          |  ["4th"]=>
          |  int(4)
          |}
          |===EMPTY===
          |object::offsetExists(0)
          |object::offsetGet(0)
          |bool(false)
          |object::offsetExists(1)
          |object::offsetGet(1)
          |bool(false)
          |object::offsetExists(2)
          |object::offsetGet(2)
          |bool(false)
          |object::offsetExists(4th)
          |object::offsetGet(4th)
          |bool(false)
          |object::offsetExists(5th)
          |bool(true)
          |object::offsetExists(6)
          |bool(true)
          |===isset===
          |object::offsetExists(0)
          |bool(true)
          |object::offsetExists(1)
          |bool(true)
          |object::offsetExists(2)
          |bool(true)
          |object::offsetExists(4th)
          |bool(true)
          |object::offsetExists(5th)
          |bool(false)
          |object::offsetExists(6)
          |bool(false)
          |===offsetGet===
          |object::offsetGet(0)
          |string(3) "1st"
          |object::offsetGet(1)
          |int(1)
          |object::offsetGet(2)
          |string(3) "3rd"
          |object::offsetGet(4th)
          |int(4)
          |object::offsetGet(5th)
          |
          |Notice: Undefined index: 5th in /classes/ArrayAccessSpec.inlinePhp on line 12
          |NULL
          |object::offsetGet(6)
          |
          |Notice: Undefined offset: 6 in /classes/ArrayAccessSpec.inlinePhp on line 12
          |NULL
          |===offsetSet===
          |WRITE 1
          |object::offsetSet(1,Changed 1)
          |object::offsetGet(1)
          |string(9) "Changed 1"
          |WRITE 2
          |object::offsetSet(4th,Changed 4th)
          |object::offsetGet(4th)
          |string(11) "Changed 4th"
          |WRITE 3
          |object::offsetSet(5th,Added 5th)
          |object::offsetGet(5th)
          |string(9) "Added 5th"
          |WRITE 4
          |object::offsetSet(6,Added 6)
          |object::offsetGet(6)
          |string(7) "Added 6"
          |object::offsetGet(0)
          |string(3) "1st"
          |object::offsetGet(2)
          |string(3) "3rd"
          |object::offsetSet(6,changed 6)
          |object::offsetGet(6)
          |string(9) "changed 6"
          |string(9) "changed 6"
          |===unset===
          |array(6) {
          |  [0]=>
          |  string(3) "1st"
          |  [1]=>
          |  string(9) "Changed 1"
          |  [2]=>
          |  string(3) "3rd"
          |  ["4th"]=>
          |  string(11) "Changed 4th"
          |  ["5th"]=>
          |  string(9) "Added 5th"
          |  [6]=>
          |  string(9) "changed 6"
          |}
          |object::offsetUnset(2)
          |object::offsetUnset(4th)
          |object::offsetUnset(7)
          |object::offsetUnset(8th)
          |array(4) {
          |  [0]=>
          |  string(3) "1st"
          |  [1]=>
          |  string(9) "Changed 1"
          |  ["5th"]=>
          |  string(9) "Added 5th"
          |  [6]=>
          |  string(9) "changed 6"
          |}
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess::offsetSet without return" in {
      // classes/array_access_002.phpt
      script(
        """<?php
          |class object implements ArrayAccess {
          |
          |	public $a = array('1st', 1, 2=>'3rd', '4th'=>4);
          |
          |	function offsetExists($index) {
          |		echo __METHOD__ . "($index)\n";
          |		return array_key_exists($index, $this->a);
          |	}
          |	function offsetGet($index) {
          |		echo __METHOD__ . "($index)\n";
          |		return $this->a[$index];
          |	}
          |	function offsetSet($index, $newval) {
          |		echo __METHOD__ . "($index,$newval)\n";
          |		/*return*/ $this->a[$index] = $newval;
          |	}
          |	function offsetUnset($index) {
          |		echo __METHOD__ . "($index)\n";
          |		unset($this->a[$index]);
          |	}
          |}
          |
          |$obj = new Object;
          |
          |var_dump($obj->a);
          |
          |echo "===EMPTY===\n";
          |var_dump(empty($obj[0]));
          |var_dump(empty($obj[1]));
          |var_dump(empty($obj[2]));
          |var_dump(empty($obj['4th']));
          |var_dump(empty($obj['5th']));
          |var_dump(empty($obj[6]));
          |
          |echo "===isset===\n";
          |var_dump(isset($obj[0]));
          |var_dump(isset($obj[1]));
          |var_dump(isset($obj[2]));
          |var_dump(isset($obj['4th']));
          |var_dump(isset($obj['5th']));
          |var_dump(isset($obj[6]));
          |
          |echo "===offsetGet===\n";
          |var_dump($obj[0]);
          |var_dump($obj[1]);
          |var_dump($obj[2]);
          |var_dump($obj['4th']);
          |var_dump($obj['5th']);
          |var_dump($obj[6]);
          |
          |echo "===offsetSet===\n";
          |echo "WRITE 1\n";
          |$obj[1] = 'Changed 1';
          |var_dump($obj[1]);
          |echo "WRITE 2\n";
          |$obj['4th'] = 'Changed 4th';
          |var_dump($obj['4th']);
          |echo "WRITE 3\n";
          |$obj['5th'] = 'Added 5th';
          |var_dump($obj['5th']);
          |echo "WRITE 4\n";
          |$obj[6] = 'Added 6';
          |var_dump($obj[6]);
          |
          |var_dump($obj[0]);
          |var_dump($obj[2]);
          |
          |$x = $obj[6] = 'changed 6';
          |var_dump($obj[6]);
          |var_dump($x);
          |
          |echo "===unset===\n";
          |var_dump($obj->a);
          |unset($obj[2]);
          |unset($obj['4th']);
          |unset($obj[7]);
          |unset($obj['8th']);
          |var_dump($obj->a);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """array(4) {
          |  [0]=>
          |  string(3) "1st"
          |  [1]=>
          |  int(1)
          |  [2]=>
          |  string(3) "3rd"
          |  ["4th"]=>
          |  int(4)
          |}
          |===EMPTY===
          |object::offsetExists(0)
          |object::offsetGet(0)
          |bool(false)
          |object::offsetExists(1)
          |object::offsetGet(1)
          |bool(false)
          |object::offsetExists(2)
          |object::offsetGet(2)
          |bool(false)
          |object::offsetExists(4th)
          |object::offsetGet(4th)
          |bool(false)
          |object::offsetExists(5th)
          |bool(true)
          |object::offsetExists(6)
          |bool(true)
          |===isset===
          |object::offsetExists(0)
          |bool(true)
          |object::offsetExists(1)
          |bool(true)
          |object::offsetExists(2)
          |bool(true)
          |object::offsetExists(4th)
          |bool(true)
          |object::offsetExists(5th)
          |bool(false)
          |object::offsetExists(6)
          |bool(false)
          |===offsetGet===
          |object::offsetGet(0)
          |string(3) "1st"
          |object::offsetGet(1)
          |int(1)
          |object::offsetGet(2)
          |string(3) "3rd"
          |object::offsetGet(4th)
          |int(4)
          |object::offsetGet(5th)
          |
          |Notice: Undefined index: 5th in /classes/ArrayAccessSpec.inlinePhp on line 12
          |NULL
          |object::offsetGet(6)
          |
          |Notice: Undefined offset: 6 in /classes/ArrayAccessSpec.inlinePhp on line 12
          |NULL
          |===offsetSet===
          |WRITE 1
          |object::offsetSet(1,Changed 1)
          |object::offsetGet(1)
          |string(9) "Changed 1"
          |WRITE 2
          |object::offsetSet(4th,Changed 4th)
          |object::offsetGet(4th)
          |string(11) "Changed 4th"
          |WRITE 3
          |object::offsetSet(5th,Added 5th)
          |object::offsetGet(5th)
          |string(9) "Added 5th"
          |WRITE 4
          |object::offsetSet(6,Added 6)
          |object::offsetGet(6)
          |string(7) "Added 6"
          |object::offsetGet(0)
          |string(3) "1st"
          |object::offsetGet(2)
          |string(3) "3rd"
          |object::offsetSet(6,changed 6)
          |object::offsetGet(6)
          |string(9) "changed 6"
          |string(9) "changed 6"
          |===unset===
          |array(6) {
          |  [0]=>
          |  string(3) "1st"
          |  [1]=>
          |  string(9) "Changed 1"
          |  [2]=>
          |  string(3) "3rd"
          |  ["4th"]=>
          |  string(11) "Changed 4th"
          |  ["5th"]=>
          |  string(9) "Added 5th"
          |  [6]=>
          |  string(9) "changed 6"
          |}
          |object::offsetUnset(2)
          |object::offsetUnset(4th)
          |object::offsetUnset(7)
          |object::offsetUnset(8th)
          |array(4) {
          |  [0]=>
          |  string(3) "1st"
          |  [1]=>
          |  string(9) "Changed 1"
          |  ["5th"]=>
          |  string(9) "Added 5th"
          |  [6]=>
          |  string(9) "changed 6"
          |}
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess::offsetGet ambiguties" in {
      // classes/array_access_003.phpt
      script(
        """<?php
          |class object implements ArrayAccess {
          |
          |	public $a = array('1st', 1, 2=>'3rd', '4th'=>4);
          |
          |	function offsetExists($index) {
          |		echo __METHOD__ . "($index)\n";
          |		return array_key_exists($index, $this->a);
          |	}
          |	function offsetGet($index) {
          |		echo __METHOD__ . "($index)\n";
          |		switch($index) {
          |		case 1:
          |			$a = 'foo';
          |			return $a . 'Bar';
          |		case 2:
          |			static $a=1;
          |			return $a;
          |		}
          |		return $this->a[$index];
          |	}
          |	function offsetSet($index, $newval) {
          |		echo __METHOD__ . "($index,$newval)\n";
          |		if ($index==3) {
          |			$this->cnt = $newval;
          |		}
          |		return $this->a[$index] = $newval;
          |	}
          |	function offsetUnset($index) {
          |		echo __METHOD__ . "($index)\n";
          |		unset($this->a[$index]);
          |	}
          |}
          |
          |$obj = new Object;
          |
          |var_dump($obj[1]);
          |var_dump($obj[2]);
          |$obj[2]++;
          |var_dump($obj[2]);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """object::offsetGet(1)
          |string(6) "fooBar"
          |object::offsetGet(2)
          |int(1)
          |object::offsetGet(2)
          |
          |Notice: Indirect modification of overloaded element of object has no effect in /classes/ArrayAccessSpec.inlinePhp on line 39
          |object::offsetGet(2)
          |int(1)
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess::offsetGet ambiguties" in {
      // classes/array_access_004.phpt
      script(
        """<?php
          |class object implements ArrayAccess {
          |
          |	public $a = array('1st', 1, 2=>'3rd', '4th'=>4);
          |
          |	function offsetExists($index) {
          |		echo __METHOD__ . "($index)\n";
          |		return array_key_exists($index, $this->a);
          |	}
          |	function offsetGet($index) {
          |		echo __METHOD__ . "($index)\n";
          |		switch($index) {
          |		case 1:
          |			$a = 'foo';
          |			return $a . 'Bar';
          |		case 2:
          |			static $a=1;
          |			return $a;
          |		}
          |		return $this->a[$index];
          |	}
          |	function offsetSet($index, $newval) {
          |		echo __METHOD__ . "($index,$newval)\n";
          |		if ($index==3) {
          |			$this->cnt = $newval;
          |		}
          |		return $this->a[$index] = $newval;
          |	}
          |	function offsetUnset($index) {
          |		echo __METHOD__ . "($index)\n";
          |		unset($this->a[$index]);
          |	}
          |}
          |
          |$obj = new Object;
          |
          |var_dump($obj[1]);
          |var_dump($obj[2]);
          |$obj[2]++;
          |var_dump($obj[2]);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """object::offsetGet(1)
          |string(6) "fooBar"
          |object::offsetGet(2)
          |int(1)
          |object::offsetGet(2)
          |
          |Notice: Indirect modification of overloaded element of object has no effect in /classes/ArrayAccessSpec.inlinePhp on line 39
          |object::offsetGet(2)
          |int(1)
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess and sub Arrays" in {
      // classes/array_access_005.phpt
      script(
        """<?php
          |
          |class Peoples implements ArrayAccess {
          |	public $person;
          |
          |	function __construct() {
          |		$this->person = array(array('name'=>'Joe'));
          |	}
          |
          |	function offsetExists($index) {
          |		return array_key_exists($this->person, $index);
          |	}
          |
          |	function offsetGet($index) {
          |		return $this->person[$index];
          |	}
          |
          |	function offsetSet($index, $value) {
          |		$this->person[$index] = $value;
          |	}
          |
          |	function offsetUnset($index) {
          |		unset($this->person[$index]);
          |	}
          |}
          |
          |$people = new Peoples;
          |
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] = $people->person[0]['name'] . 'Foo';
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] .= 'Bar';
          |var_dump($people->person[0]['name']);
          |
          |echo "---ArrayOverloading---\n";
          |
          |$people = new Peoples;
          |
          |var_dump($people[0]);
          |var_dump($people[0]['name']);
          |var_dump($people->person[0]['name'] . 'Foo'); // impossible to assign this since we don't return references here
          |$x = $people[0]; // creates a copy
          |$x['name'] .= 'Foo';
          |$people[0] = $x;
          |var_dump($people[0]);
          |$people[0]['name'] = 'JoeFoo';
          |var_dump($people[0]['name']);
          |$people[0]['name'] = 'JoeFooBar';
          |var_dump($people[0]['name']);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """string(3) "Joe"
          |string(6) "JoeFoo"
          |string(9) "JoeFooBar"
          |---ArrayOverloading---
          |array(1) {
          |  ["name"]=>
          |  string(3) "Joe"
          |}
          |string(3) "Joe"
          |string(6) "JoeFoo"
          |array(1) {
          |  ["name"]=>
          |  string(6) "JoeFoo"
          |}
          |
          |Notice: Indirect modification of overloaded element of Peoples has no effect in /classes/ArrayAccessSpec.inlinePhp on line 46
          |string(6) "JoeFoo"
          |
          |Notice: Indirect modification of overloaded element of Peoples has no effect in /classes/ArrayAccessSpec.inlinePhp on line 48
          |string(6) "JoeFoo"
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess and ASSIGN_OP operators (+=)" in {
      // classes/array_access_006.phpt
      script(
        """<?php
          |
          |class OverloadedArray implements ArrayAccess {
          |	public $realArray;
          |
          |	function __construct() {
          |		$this->realArray = array(1,2,3);
          |	}
          |
          |	function offsetExists($index) {
          |		return array_key_exists($this->realArray, $index);
          |	}
          |
          |	function offsetGet($index) {
          |		return $this->realArray[$index];
          |	}
          |
          |	function offsetSet($index, $value) {
          |		$this->realArray[$index] = $value;
          |	}
          |
          |	function offsetUnset($index) {
          |		unset($this->realArray[$index]);
          |	}
          |}
          |
          |$a = new OverloadedArray;
          |$a[1] += 10;
          |var_dump($a[1]);
          |echo "---Done---\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(12)
          |---Done---
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess and [] assignment" in {
      // classes/array_access_007.phpt
      script(
        """<?php
          |
          |class OverloadedArray implements ArrayAccess {
          |	public $realArray;
          |
          |	function __construct() {
          |		$this->realArray = array();
          |	}
          |
          |	function offsetExists($index) {
          |		return array_key_exists($this->realArray, $index);
          |	}
          |
          |	function offsetGet($index) {
          |		return $this->realArray[$index];
          |	}
          |
          |	function offsetSet($index, $value) {
          |		if (is_null($index)) {
          |			$this->realArray[] = $value;
          |		} else {
          |			$this->realArray[$index] = $value;
          |		}
          |	}
          |
          |	function offsetUnset($index) {
          |		unset($this->realArray[$index]);
          |	}
          |
          |	function dump() {
          |		var_dump($this->realArray);
          |	}
          |}
          |
          |$a = new OverloadedArray;
          |$a[] = 1;
          |$a[1] = 2;
          |$a[2] = 3;
          |$a[] = 4;
          |$a->dump();
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """array(4) {
          |  [0]=>
          |  int(1)
          |  [1]=>
          |  int(2)
          |  [2]=>
          |  int(3)
          |  [3]=>
          |  int(4)
          |}
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess and ASSIGN_OP operators (.=)" in {
      // classes/array_access_008.phpt
      script(
        """<?php
          |
          |class Peoples implements ArrayAccess {
          |	public $person;
          |
          |	function __construct() {
          |		$this->person = array(array('name'=>'Foo'));
          |	}
          |
          |	function offsetExists($index) {
          |		return array_key_exists($this->person, $index);
          |	}
          |
          |	function offsetGet($index) {
          |		return $this->person[$index];
          |	}
          |
          |	function offsetSet($index, $value) {
          |		$this->person[$index] = $value;
          |	}
          |
          |	function offsetUnset($index) {
          |		unset($this->person[$index]);
          |	}
          |}
          |
          |$people = new Peoples;
          |
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] = $people->person[0]['name'] . 'Bar';
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] .= 'Baz';
          |var_dump($people->person[0]['name']);
          |
          |echo "===ArrayOverloading===\n";
          |
          |$people = new Peoples;
          |
          |var_dump($people[0]['name']);
          |$people[0]['name'] = 'FooBar';
          |var_dump($people[0]['name']);
          |$people[0]['name'] = $people->person[0]['name'] . 'Bar';
          |var_dump($people[0]['name']);
          |$people[0]['name'] .= 'Baz';
          |var_dump($people[0]['name']);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """string(3) "Foo"
          |string(6) "FooBar"
          |string(9) "FooBarBaz"
          |===ArrayOverloading===
          |string(3) "Foo"
          |
          |Notice: Indirect modification of overloaded element of Peoples has no effect in /classes/ArrayAccessSpec.inlinePhp on line 40
          |string(3) "Foo"
          |
          |Notice: Indirect modification of overloaded element of Peoples has no effect in /classes/ArrayAccessSpec.inlinePhp on line 42
          |string(3) "Foo"
          |
          |Notice: Indirect modification of overloaded element of Peoples has no effect in /classes/ArrayAccessSpec.inlinePhp on line 44
          |string(3) "Foo"
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess and ArrayProxyAccess, ArrayProxy" in {
      // classes/array_access_009.phpt
      script(
        """<?php
          |
          |// NOTE: This will become part of SPL
          |
          |interface ArrayProxyAccess extends ArrayAccess
          |{
          |	function proxyGet($element);
          |	function proxySet($element, $index, $value);
          |	function proxyUnset($element, $index);
          |}
          |
          |class ArrayProxy implements ArrayAccess
          |{
          |	private $object;
          |	private $element;
          |
          |	function __construct(ArrayProxyAccess $object, $element)
          |	{
          |		echo __METHOD__ . "($element)\n";
          |		if (!$object->offsetExists($element))
          |		{
          |			$object[$element] = array();
          |		}
          |		$this->object = $object;
          |		$this->element = $element;
          |	}
          |
          |	function offsetExists($index) {
          |		echo __METHOD__ . "($this->element, $index)\n";
          |		return array_key_exists($index, $this->object->proxyGet($this->element));
          |	}
          |
          |	function offsetGet($index) {
          |		echo __METHOD__ . "($this->element, $index)\n";
          |		$tmp = $this->object->proxyGet($this->element);
          |		return isset($tmp[$index]) ? $tmp[$index] : NULL;
          |	}
          |
          |	function offsetSet($index, $value) {
          |		echo __METHOD__ . "($this->element, $index, $value)\n";
          |		$this->object->proxySet($this->element, $index, $value);
          |	}
          |
          |	function offsetUnset($index) {
          |		echo __METHOD__ . "($this->element, $index)\n";
          |		$this->object->proxyUnset($this->element, $index);
          |	}
          |}
          |
          |class Peoples implements ArrayProxyAccess
          |{
          |	public $person;
          |
          |	function __construct()
          |	{
          |		$this->person = array(array('name'=>'Foo'));
          |	}
          |
          |	function offsetExists($index)
          |	{
          |		return array_key_exists($index, $this->person);
          |	}
          |
          |	function offsetGet($index)
          |	{
          |		return new ArrayProxy($this, $index);
          |	}
          |
          |	function offsetSet($index, $value)
          |	{
          |		$this->person[$index] = $value;
          |	}
          |
          |	function offsetUnset($index)
          |	{
          |		unset($this->person[$index]);
          |	}
          |
          |	function proxyGet($element)
          |	{
          |		return $this->person[$element];
          |	}
          |
          |	function proxySet($element, $index, $value)
          |	{
          |		$this->person[$element][$index] = $value;
          |	}
          |
          |	function proxyUnset($element, $index)
          |	{
          |		unset($this->person[$element][$index]);
          |	}
          |}
          |
          |$people = new Peoples;
          |
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] = $people->person[0]['name'] . 'Bar';
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] .= 'Baz';
          |var_dump($people->person[0]['name']);
          |
          |echo "===ArrayOverloading===\n";
          |
          |$people = new Peoples;
          |
          |var_dump($people[0]);
          |var_dump($people[0]['name']);
          |$people[0]['name'] = 'FooBar';
          |var_dump($people[0]['name']);
          |$people[0]['name'] = $people->person[0]['name'] . 'Bar';
          |var_dump($people[0]['name']);
          |$people[0]['name'] .= 'Baz';
          |var_dump($people[0]['name']);
          |unset($people[0]['name']);
          |var_dump($people[0]);
          |var_dump($people[0]['name']);
          |$people[0]['name'] = 'BlaBla';
          |var_dump($people[0]['name']);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """string(3) "Foo"
          |string(6) "FooBar"
          |string(9) "FooBarBaz"
          |===ArrayOverloading===
          |ArrayProxy::__construct(0)
          |object(ArrayProxy)#3 (2) {
          |  ["object":"ArrayProxy":private]=>
          |  object(Peoples)#2 (1) {
          |    ["person"]=>
          |    array(1) {
          |      [0]=>
          |      array(1) {
          |        ["name"]=>
          |        string(3) "Foo"
          |      }
          |    }
          |  }
          |  ["element":"ArrayProxy":private]=>
          |  int(0)
          |}
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetGet(0, name)
          |string(3) "Foo"
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetSet(0, name, FooBar)
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetGet(0, name)
          |string(6) "FooBar"
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetSet(0, name, FooBarBar)
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetGet(0, name)
          |string(9) "FooBarBar"
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetGet(0, name)
          |ArrayProxy::offsetSet(0, name, FooBarBarBaz)
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetGet(0, name)
          |string(12) "FooBarBarBaz"
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetUnset(0, name)
          |ArrayProxy::__construct(0)
          |object(ArrayProxy)#12 (2) {
          |  ["object":"ArrayProxy":private]=>
          |  object(Peoples)#2 (1) {
          |    ["person"]=>
          |    array(1) {
          |      [0]=>
          |      array(0) {
          |      }
          |    }
          |  }
          |  ["element":"ArrayProxy":private]=>
          |  int(0)
          |}
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetGet(0, name)
          |NULL
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetSet(0, name, BlaBla)
          |ArrayProxy::__construct(0)
          |ArrayProxy::offsetGet(0, name)
          |string(6) "BlaBla"
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 ArrayAccess and ArrayReferenceProxy with references" in {
      // classes/array_access_010.phpt
      script(
        """<?php
          |
          |// NOTE: This will become part of SPL
          |
          |class ArrayReferenceProxy implements ArrayAccess
          |{
          |	private $object;
          |	private $element;
          |
          |	function __construct(ArrayAccess $object, array &$element)
          |	{
          |		echo __METHOD__ . "(Array)\n";
          |		$this->object = $object;
          |		$this->element = &$element;
          |	}
          |
          |	function offsetExists($index) {
          |		echo __METHOD__ . "($this->element, $index)\n";
          |		return array_key_exists($index, $this->element);
          |	}
          |
          |	function offsetGet($index) {
          |		echo __METHOD__ . "(Array, $index)\n";
          |		return isset($this->element[$index]) ? $this->element[$index] : NULL;
          |	}
          |
          |	function offsetSet($index, $value) {
          |		echo __METHOD__ . "(Array, $index, $value)\n";
          |		$this->element[$index] = $value;
          |	}
          |
          |	function offsetUnset($index) {
          |		echo __METHOD__ . "(Array, $index)\n";
          |		unset($this->element[$index]);
          |	}
          |}
          |
          |class Peoples implements ArrayAccess
          |{
          |	public $person;
          |
          |	function __construct()
          |	{
          |		$this->person = array(array('name'=>'Foo'));
          |	}
          |
          |	function offsetExists($index)
          |	{
          |		return array_key_exists($index, $this->person);
          |	}
          |
          |	function offsetGet($index)
          |	{
          |		return new ArrayReferenceProxy($this, $this->person[$index]);
          |	}
          |
          |	function offsetSet($index, $value)
          |	{
          |		$this->person[$index] = $value;
          |	}
          |
          |	function offsetUnset($index)
          |	{
          |		unset($this->person[$index]);
          |	}
          |}
          |
          |$people = new Peoples;
          |
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] = $people->person[0]['name'] . 'Bar';
          |var_dump($people->person[0]['name']);
          |$people->person[0]['name'] .= 'Baz';
          |var_dump($people->person[0]['name']);
          |
          |echo "===ArrayOverloading===\n";
          |
          |$people = new Peoples;
          |
          |var_dump($people[0]);
          |var_dump($people[0]['name']);
          |$people[0]['name'] = 'FooBar';
          |var_dump($people[0]['name']);
          |$people[0]['name'] = $people->person[0]['name'] . 'Bar';
          |var_dump($people[0]['name']);
          |$people[0]['name'] .= 'Baz';
          |var_dump($people[0]['name']);
          |unset($people[0]['name']);
          |var_dump($people[0]);
          |var_dump($people[0]['name']);
          |$people[0]['name'] = 'BlaBla';
          |var_dump($people[0]['name']);
          |
          |?>
          |===DONE===
          |<?php exit(0); ?>
          |""".stripMargin
      ).result must haveOutput(
        """string(3) "Foo"
          |string(6) "FooBar"
          |string(9) "FooBarBaz"
          |===ArrayOverloading===
          |ArrayReferenceProxy::__construct(Array)
          |object(ArrayReferenceProxy)#3 (2) {
          |  ["object":"ArrayReferenceProxy":private]=>
          |  object(Peoples)#2 (1) {
          |    ["person"]=>
          |    array(1) {
          |      [0]=>
          |      &array(1) {
          |        ["name"]=>
          |        string(3) "Foo"
          |      }
          |    }
          |  }
          |  ["element":"ArrayReferenceProxy":private]=>
          |  &array(1) {
          |    ["name"]=>
          |    string(3) "Foo"
          |  }
          |}
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetGet(Array, name)
          |string(3) "Foo"
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetSet(Array, name, FooBar)
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetGet(Array, name)
          |string(6) "FooBar"
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetSet(Array, name, FooBarBar)
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetGet(Array, name)
          |string(9) "FooBarBar"
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetGet(Array, name)
          |ArrayReferenceProxy::offsetSet(Array, name, FooBarBarBaz)
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetGet(Array, name)
          |string(12) "FooBarBarBaz"
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetUnset(Array, name)
          |ArrayReferenceProxy::__construct(Array)
          |object(ArrayReferenceProxy)#12 (2) {
          |  ["object":"ArrayReferenceProxy":private]=>
          |  object(Peoples)#2 (1) {
          |    ["person"]=>
          |    array(1) {
          |      [0]=>
          |      &array(0) {
          |      }
          |    }
          |  }
          |  ["element":"ArrayReferenceProxy":private]=>
          |  &array(0) {
          |  }
          |}
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetGet(Array, name)
          |NULL
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetSet(Array, name, BlaBla)
          |ArrayReferenceProxy::__construct(Array)
          |ArrayReferenceProxy::offsetGet(Array, name)
          |string(6) "BlaBla"
          |===DONE===
          |""".stripMargin
      )
    }
  }
}
