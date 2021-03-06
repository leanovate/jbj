/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class FactorySpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Fectory" should {
    "ZE2 factory objects" in {
      // classes/factory_001
      script(
        """<?php
          |
          |class Circle {
          |	function draw() {
          |		echo "Circle\n";
          |	}
          |}
          |
          |class Square {
          |	function draw() {
          |		print "Square\n";
          |	}
          |}
          |
          |function ShapeFactoryMethod($shape) {
          |	switch ($shape) {
          |		case "Circle":
          |			return new Circle();
          |		case "Square":
          |			return new Square();
          |	}
          |}
          |
          |ShapeFactoryMethod("Circle")->draw();
          |ShapeFactoryMethod("Square")->draw();
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Circle
          |Square
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 1" in {
      // classes/factory_and_singleton_001.phpt
      script(
        """<?php
          |class test {
          |  protected $x;
          |
          |  static private $test = NULL;
          |  static private $cnt = 0;
          |
          |  static function factory($x) {
          |    if (test::$test) {
          |      return test::$test;
          |    } else {
          |      test::$test = new test($x);
          |      return test::$test;
          |    }
          |  }
          |
          |  protected function __construct($x) {
          |    test::$cnt++;
          |    $this->x = $x;
          |  }
          |
          |  static function destroy() {
          |    test::$test = NULL;
          |  }
          |
          |  protected function __destruct() {
          |  	test::$cnt--;
          |  }
          |
          |  public function get() {
          |    return $this->x;
          |  }
          |
          |  static public function getX() {
          |    if (test::$test) {
          |      return test::$test->x;
          |    } else {
          |      return NULL;
          |    }
          |  }
          |
          |  static public function count() {
          |    return test::$cnt;
          |  }
          |}
          |
          |echo "Access static members\n";
          |var_dump(test::getX());
          |var_dump(test::count());
          |
          |echo "Create x and y\n";
          |$x = test::factory(1);
          |$y = test::factory(2);
          |var_dump(test::getX());
          |var_dump(test::count());
          |var_dump($x->get());
          |var_dump($y->get());
          |
          |echo "Destruct x\n";
          |$x = NULL;
          |var_dump(test::getX());
          |var_dump(test::count());
          |var_dump($y->get());
          |
          |echo "Destruct y\n";
          |$y = NULL;
          |var_dump(test::getX());
          |var_dump(test::count());
          |
          |echo "Destruct static\n";
          |test::destroy();
          |var_dump(test::getX());
          |var_dump(test::count());
          |
          |echo "Done\n";
          |?>
          | """.stripMargin
      ).result must haveOutput(
        """Access static members
          |NULL
          |int(0)
          |Create x and y
          |int(1)
          |int(1)
          |int(1)
          |int(1)
          |Destruct x
          |int(1)
          |int(1)
          |int(1)
          |Destruct y
          |int(1)
          |int(1)
          |Destruct static
          |NULL
          |int(0)
          |Done
          | """.stripMargin
      )
    }

    "ZE2 factory and singleton, test 2" in {
      // classes/factory_and_singleton_002.phpt
      script(
        """<?php
          |class test {
          |  protected $x;
          |
          |  static private $test = NULL;
          |  static private $cnt = 0;
          |
          |  static function factory($x) {
          |    if (test::$test) {
          |      return test::$test;
          |    } else {
          |      test::$test = new test($x);
          |      return test::$test;
          |    }
          |  }
          |
          |  protected function __construct($x) {
          |    test::$cnt++;
          |    $this->x = $x;
          |  }
          |
          |  static function destroy() {
          |    test::$test = NULL;
          |  }
          |
          |  protected function __destruct() {
          |  	test::$cnt--;
          |  }
          |
          |  public function get() {
          |    return $this->x;
          |  }
          |
          |  static public function getX() {
          |    if (test::$test) {
          |      return test::$test->x;
          |    } else {
          |      return NULL;
          |    }
          |  }
          |
          |  static public function count() {
          |    return test::$cnt;
          |  }
          |}
          |
          |echo "Access static members\n";
          |var_dump(test::getX());
          |var_dump(test::count());
          |
          |echo "Create x and y\n";
          |$x = test::factory(1);
          |$y = test::factory(2);
          |var_dump(test::getX());
          |var_dump(test::count());
          |var_dump($x->get());
          |var_dump($y->get());
          |
          |echo "Destruct x\n";
          |$x = NULL;
          |var_dump(test::getX());
          |var_dump(test::count());
          |var_dump($y->get());
          |
          |echo "Destruct y\n";
          |$y = NULL;
          |var_dump(test::getX());
          |var_dump(test::count());
          |
          |//echo "Destruct static\n";
          |//test::destroy();
          |//var_dump(test::getX());
          |//var_dump(test::count());
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Access static members
          |NULL
          |int(0)
          |Create x and y
          |int(1)
          |int(1)
          |int(1)
          |int(1)
          |Destruct x
          |int(1)
          |int(1)
          |int(1)
          |Destruct y
          |int(1)
          |int(1)
          |Done
          |
          |Warning: Call to protected test::__destruct() from context '' during shutdown ignored in Unknown on line 0
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 3" in {
      // classes/factory_and_singleton_003.phpt
      script(
        """<?php
          |class test {
          |
          |  protected function __construct($x) {
          |  }
          |}
          |
          |$obj = new test;
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Call to protected test::__construct() from invalid context in /classes/FactorySpec.inlinePhp on line 8
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 4" in {
      // classes/factory_and_singleton_004.phpt
      script(
        """<?php
          |class test {
          |
          |  private function __construct($x) {
          |  }
          |}
          |
          |$obj = new test;
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Call to private test::__construct() from invalid context in /classes/FactorySpec.inlinePhp on line 8
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 5" in {
      // classes/factory_and_singleton_005.phpt
      script(
        """<?php
          |class test {
          |
          |  protected function __destruct() {
          |  }
          |}
          |
          |$obj = new test;
          |$obj = NULL;
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Call to protected test::__destruct() from context '' in /classes/FactorySpec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 6" in {
      // classes/factory_and_singleton_006.phpt
      script(
        """<?php
          |class test {
          |
          |  private function __destruct() {
          |  }
          |}
          |
          |$obj = new test;
          |$obj = NULL;
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Call to private test::__destruct() from context '' in /classes/FactorySpec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 7" in {
      // classes/factory_and_singleton_007.phpt
      script(
        """<?php
          |class test {
          |
          |  protected function __clone() {
          |  }
          |}
          |
          |$obj = new test;
          |$clone = clone $obj;
          |$obj = NULL;
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Call to protected test::__clone() from context '' in /classes/FactorySpec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 8" in {
      // classes/factory_and_singleton_008.phpt
      script(
        """<?php
          |class test {
          |
          |  private function __clone() {
          |  }
          |}
          |
          |$obj = new test;
          |$clone = clone $obj;
          |$obj = NULL;
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Call to private test::__clone() from context '' in /classes/FactorySpec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 9" in {
      // classes/factory_and_singleton_009.phpt
      script(
        """<?php
          |class test {
          |
          |  protected function __destruct() {
          |  	echo __METHOD__ . "\n";
          |  }
          |}
          |
          |$obj = new test;
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """===DONE===
          |
          |Warning: Call to protected test::__destruct() from context '' during shutdown ignored in Unknown on line 0
          |""".stripMargin
      )
    }

    "ZE2 factory and singleton, test 10" in {
      // classes/factory_and_singleton_010.phpt
      script(
        """<?php
          |class test {
          |
          |  private function __destruct() {
          |  	echo __METHOD__ . "\n";
          |  }
          |}
          |
          |$obj = new test;
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """===DONE===
          |
          |Warning: Call to private test::__destruct() from context '' during shutdown ignored in Unknown on line 0
          |""".stripMargin
      )
    }
  }
}
