/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class CloneSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Clone" should {
    "ZE2 object cloning, 1" in {
      // classes/clone_001
      script(
        """<?php
          |class test {
          |	public $p1 = 1;
          |	public $p2 = 2;
          |	public $p3;
          |};
          |
          |$obj = new test;
          |$obj->p2 = 'A';
          |$obj->p3 = 'B';
          |$copy = clone $obj;
          |$copy->p3 = 'C';
          |echo "Object\n";
          |var_dump($obj);
          |echo "Clown\n";
          |var_dump($copy);
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """Object
          |object(test)#1 (3) {
          |  ["p1"]=>
          |  int(1)
          |  ["p2"]=>
          |  string(1) "A"
          |  ["p3"]=>
          |  string(1) "B"
          |}
          |Clown
          |object(test)#2 (3) {
          |  ["p1"]=>
          |  int(1)
          |  ["p2"]=>
          |  string(1) "A"
          |  ["p3"]=>
          |  string(1) "C"
          |}
          |Done
          |""".stripMargin
      )
    }

    "ZE2 object cloning, 2" in {
      // classes/clone_002
      script(
        """<?php
          |class test {
          |	public $p1 = 1;
          |	public $p2 = 2;
          |	public $p3;
          |	public function __clone() {
          |	}
          |};
          |
          |$obj = new test;
          |$obj->p2 = 'A';
          |$obj->p3 = 'B';
          |$copy = clone $obj;
          |$copy->p3 = 'C';
          |echo "Object\n";
          |var_dump($obj);
          |echo "Clown\n";
          |var_dump($copy);
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """Object
          |object(test)#1 (3) {
          |  ["p1"]=>
          |  int(1)
          |  ["p2"]=>
          |  string(1) "A"
          |  ["p3"]=>
          |  string(1) "B"
          |}
          |Clown
          |object(test)#2 (3) {
          |  ["p1"]=>
          |  int(1)
          |  ["p2"]=>
          |  string(1) "A"
          |  ["p3"]=>
          |  string(1) "C"
          |}
          |Done
          |""".stripMargin
      )
    }

    "ZE2 object cloning, 3" in {
      // classes/clone_003.phpt
      script(
        """<?php
          |class base {
          |	protected $p1 = 'base:1';
          |	public $p2 = 'base:2';
          |	public $p3 = 'base:3';
          |	public $p4 = 'base:4';
          |	public $p5 = 'base:5';
          |	private $p6 = 'base:6';
          |	public function __clone() {
          |	}
          |};
          |
          |class test extends base {
          |	public $p1 = 'test:1';
          |	public $p3 = 'test:3';
          |	public $p4 = 'test:4';
          |	public $p5 = 'test:5';
          |	public function __clone() {
          |		$this->p5 = 'clone:5';
          |	}
          |}
          |
          |$obj = new test;
          |$obj->p4 = 'A';
          |$copy = clone $obj;
          |echo "Object\n";
          |print_r($obj);
          |echo "Clown\n";
          |print_r($copy);
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Object
          |test Object
          |(
          |    [p1] => test:1
          |    [p3] => test:3
          |    [p4] => A
          |    [p5] => test:5
          |    [p2] => base:2
          |    [p6:base:private] => base:6
          |)
          |Clown
          |test Object
          |(
          |    [p1] => test:1
          |    [p3] => test:3
          |    [p4] => A
          |    [p5] => clone:5
          |    [p2] => base:2
          |    [p6:base:private] => base:6
          |)
          |Done
          |""".stripMargin
      )
    }

    "ZE2 object cloning, 4" in {
      // classes/clone_004
      script(
        """<?php
          |abstract class base {
          |  public $a = 'base';
          |
          |  // disallow cloning
          |  private function __clone() {}
          |}
          |
          |class test extends base {
          |  public $b = 'test';
          |
          |  // reenable cloning
          |  public function __clone() {}
          |
          |  public function show() {
          |	var_dump($this);
          |  }
          |}
          |
          |echo "Original\n";
          |$o1 = new test;
          |$o1->a = array(1,2);
          |$o1->b = array(3,4);
          |$o1->show();
          |
          |echo "Clone\n";
          |$o2 = clone $o1;
          |$o2->show();
          |
          |echo "Modify\n";
          |$o2->a = 5;
          |$o2->b = 6;
          |$o2->show();
          |
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """Original
          |object(test)#1 (2) {
          |  ["b"]=>
          |  array(2) {
          |    [0]=>
          |    int(3)
          |    [1]=>
          |    int(4)
          |  }
          |  ["a"]=>
          |  array(2) {
          |    [0]=>
          |    int(1)
          |    [1]=>
          |    int(2)
          |  }
          |}
          |Clone
          |object(test)#2 (2) {
          |  ["b"]=>
          |  array(2) {
          |    [0]=>
          |    int(3)
          |    [1]=>
          |    int(4)
          |  }
          |  ["a"]=>
          |  array(2) {
          |    [0]=>
          |    int(1)
          |    [1]=>
          |    int(2)
          |  }
          |}
          |Modify
          |object(test)#2 (2) {
          |  ["b"]=>
          |  int(6)
          |  ["a"]=>
          |  int(5)
          |}
          |Done
          |""".stripMargin
      )
    }

    "ZE2 object cloning, 5" in {
      // classes/clone_005.phpt
      script(
        """<?php
          |abstract class base {
          |  public $a = 'base';
          |
          |  // disallow cloning once forever
          |  final private function __clone() {}
          |}
          |
          |class test extends base {
          |  // reenabling should fail
          |  public function __clone() {}
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot override final method base::__clone() in /classes/CloneSpec.inlinePhp on line 11
          |""".stripMargin
      )
    }

    "ZE2 object cloning, 6" in {
      // classes/clone_006.phpt
      script(
        """<?php
          |
          |class MyCloneable {
          |	static $id = 0;
          |
          |	function MyCloneable() {
          |		$this->id = self::$id++;
          |	}
          |
          |	function __clone() {
          |		$this->address = "New York";
          |		$this->id = self::$id++;
          |	}
          |}
          |
          |$original = new MyCloneable();
          |
          |$original->name = "Hello";
          |$original->address = "Tel-Aviv";
          |
          |echo $original->id . "\n";
          |
          |$clone = clone $original;
          |
          |echo $clone->id . "\n";
          |echo $clone->name . "\n";
          |echo $clone->address . "\n";
          |
          |?>
          |""".stripMargin
      ).withErrorReporting(2047).result must haveOutput(
        """0
          |1
          |Hello
          |New York
          |""".stripMargin
      )
    }
  }
}
