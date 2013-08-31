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
  }
}
