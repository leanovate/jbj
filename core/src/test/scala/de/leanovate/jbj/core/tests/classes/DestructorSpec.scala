/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class DestructorSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "destructor" should {
    "ZE2 Destructors and echo" in {
      // classes/destructor_and_echo.phpt
      script(
        """<?php
          |
          |class Test
          |{
          |    function __construct() {
          |        echo __METHOD__ . "\n";
          |    }
          |
          |    function __destruct() {
          |        echo __METHOD__ . "\n";
          |    }
          |}
          |
          |$o = new Test;
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Test::__construct
          |===DONE===
          |Test::__destruct
          |""".stripMargin
      )
    }

    "ZE2 accessing globals from destructor in shutdown" in {
      // classes/destructor_and_globals.phpt
      script(
        """<?php
          |$test_cnt = 0;
          |$test_num = 0;
          |
          |function Show() {
          |  global $test_cnt;
          |  echo "Count: $test_cnt\n";
          |}
          |
          |class counter {
          |  protected $id;
          |
          |  public function __construct() {
          |    global $test_cnt, $test_num;
          |    $test_cnt++;
          |    $this->id = $test_num++;
          |  }
          |
          |  public function Show() {
          |    echo 'Id: '.$this->id."\n";
          |  }
          |
          |  // try protected here
          |  public function __destruct() {
          |    global $test_cnt;
          |    $test_cnt--;
          |  }
          |
          |  static public function destroy(&$obj) {
          |  	$obj = NULL;
          |  	}
          |}
          |Show();
          |$obj1 = new counter;
          |$obj1->Show();
          |Show();
          |$obj2 = new counter;
          |$obj2->Show();
          |Show();
          |counter::destroy($obj1);
          |Show();
          |// or uncomment this line and it works
          |//counter::destroy($obj2);
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Count: 0
          |Id: 0
          |Count: 1
          |Id: 1
          |Count: 2
          |Count: 1
          |Done
          |""".stripMargin
      )
    }

    "ZE2 Destructing and references" in {
      // classes/destructor_and_references.phpt
      script(
        """<?php
          |
          |class test1 {public $x;};
          |class test2 {public $x;};
          |class test3 {public $x;};
          |class test4 {public $x;};
          |
          |$o1 = new test1;
          |$o2 = new test2;
          |$o3 = new test3;
          |$o4 = new test4;
          |
          |$o3->x = &$o4;
          |
          |$r1 = &$o1;
          |
          |class once {}
          |
          |$o = new once;
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Done
          |""".stripMargin
      )
    }

    "ZE2 The inherited destructor is called" in {
      // classes/destructor_inheritance.phpt
      script(
        """<?php
          |class base {
          |   function __construct() {
          |      echo __METHOD__ . "\n";
          |   }
          |
          |   function __destruct() {
          |      echo __METHOD__ . "\n";
          |   }
          |}
          |
          |class derived extends base {
          |}
          |
          |$obj = new derived;
          |
          |unset($obj);
          |
          |echo 'Done';
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """base::__construct
          |base::__destruct
          |Done""".stripMargin
      )
    }

    "ZE2 Ensuring destructor visibility" in {
      // classes/destructor_visibility_001.phpt
      script(
        """<?php
          |
          |class Base {
          |	private function __destruct() {
          |    	echo __METHOD__ . "\n";
          |	}
          |}
          |
          |class Derived extends Base {
          |}
          |
          |$obj = new Derived;
          |
          |unset($obj);
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Call to private Derived::__destruct() from context '' in /classes/DestructorSpec.inlinePhp on line 14
          |""".stripMargin
      )
    }

    "ZE2 Ensuring destructor visibility" in {
      // classes/destructor_visibility_002.phpt
      script(
        """<?php
          |
          |class Base {
          |	private function __destruct() {
          |		echo __METHOD__ . "\n";
          |	}
          |}
          |
          |class Derived extends Base {
          |}
          |
          |$obj = new Derived;
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """===DONE===
          |
          |Warning: Call to private Derived::__destruct() from context '' during shutdown ignored in Unknown on line 0
          |""".stripMargin
      )
    }

    "ZE2 Ensuring destructor visibility" in {
      // classes/destructor_visibility_003.phpt
      script(
        """<?php
          |
          |class Base {
          |	private function __destruct() {
          |		echo __METHOD__ . "\n";
          |	}
          |}
          |
          |class Derived extends Base {
          |	public function __destruct() {
          |		echo __METHOD__ . "\n";
          |	}
          |}
          |
          |$obj = new Derived;
          |
          |unset($obj); // Derived::__destruct is being called not Base::__destruct
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Derived::__destruct
          |===DONE===
          |""".stripMargin
      )
    }
  }
}
