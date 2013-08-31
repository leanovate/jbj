/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class AbstractSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "abstract classes" should {
    "ZE2 An abstract method may not be called" in {
      // classes/abstract.phpt
      script(
        """<?php
          |
          |abstract class fail {
          |	abstract function show();
          |}
          |
          |class pass extends fail {
          |	function show() {
          |		echo "Call to function show()\n";
          |	}
          |	function error() {
          |		parent::show();
          |	}
          |}
          |
          |$t = new pass();
          |$t->show();
          |$t->error();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call to function show()
          |
          |Fatal error: Cannot call abstract method fail::show() in /classes/AbstractSpec.inlinePhp on line 12
          |""".stripMargin
      )
    }

    "ZE2 An abstract method may not be called" in {
      // classes/abstract_by_interface_001.phpt
      script(
        """<?php
          |
          |class Root {
          |}
          |
          |interface MyInterface
          |{
          |	function MyInterfaceFunc();
          |}
          |
          |abstract class Derived extends Root implements MyInterface {
          |}
          |
          |class Leaf extends Derived
          |{
          |	function MyInterfaceFunc() {}
          |}
          |
          |var_dump(new Leaf);
          |
          |class Fails extends Root implements MyInterface {
          |}
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """object(Leaf)#1 (0) {
          |}
          |
          |Fatal error: Class Fails contains 1 abstract method and must therefore be declared abstract or implement the remaining methods (MyInterface::MyInterfaceFunc) in /classes/AbstractSpec.inlinePhp on line 21
          |""".stripMargin
      )
    }

    "ZE2 An abstract method may not be called" in {
      // classes/abstract_by_interface_002.phpt
      script(
        """<?php
          |
          |class Root {
          |}
          |
          |interface MyInterface
          |{
          |	static function MyInterfaceFunc();
          |}
          |
          |abstract class Derived extends Root implements MyInterface {
          |}
          |
          |class Leaf extends Derived
          |{
          |	static function MyInterfaceFunc() {}
          |}
          |
          |var_dump(new Leaf);
          |
          |class Fails extends Root implements MyInterface {
          |}
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """object(Leaf)#1 (0) {
          |}
          |
          |Fatal error: Class Fails contains 1 abstract method and must therefore be declared abstract or implement the remaining methods (MyInterface::MyInterfaceFunc) in /classes/AbstractSpec.inlinePhp on line 21
          |""".stripMargin
      )
    }

    "ZE2 An abstract class cannot be instantiated" in {
      // classes/abstract_class.phpt
      script(
        """<?php
          |
          |abstract class fail {
          |	abstract function show();
          |}
          |
          |class pass extends fail {
          |	function show() {
          |		echo "Call to function show()\n";
          |	}
          |}
          |
          |$t2 = new pass();
          |$t2->show();
          |
          |$t = new fail();
          |$t->show();
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Call to function show()
          |
          |Fatal error: Cannot instantiate abstract class fail in /classes/AbstractSpec.inlinePhp on line 16
          |""".stripMargin
      )
    }

    "ZE2 A derived class with an abstract method must be abstract" in {
      // classes/abstract_derived.phpt
      script(
        """<?php
          |
          |class base {
          |}
          |
          |class derived extends base {
          |	abstract function show();
          |}
          |
          |?>
          |===DONE===
          |<?php exit(0); ?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Class derived contains 1 abstract method and must therefore be declared abstract or implement the remaining methods (derived::show) in /classes/AbstractSpec.inlinePhp on line 7
          |""".stripMargin
      )
    }

    "ZE2 A final method cannot be abstract" in {
      // classes/abstract_final.phpt
      script(
        """<?php
          |
          |class fail {
          |	abstract final function show();
          |}
          |
          |echo "Done\n"; // Shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot use the final modifier on an abstract class member in /classes/AbstractSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "ZE2 A class that inherits an abstract method is abstract" in {
      // classes/abstract_inherit.phpt
      script(
        """<?php
          |
          |abstract class pass {
          |	abstract function show();
          |}
          |
          |abstract class fail extends pass {
          |}
          |
          |$t = new fail();
          |$t = new pass();
          |
          |echo "Done\n"; // Shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot instantiate abstract class fail in /classes/AbstractSpec.inlinePhp on line 10
          |""".stripMargin
      )
    }

    "ZE2 An abstract class must be declared abstract" in {
      // classes/abstract_not_declared.phpt
      script(
        """<?php
          |
          |class fail {
          |	abstract function show();
          |}
          |
          |echo "Done\n"; // shouldn't be displayed
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Class fail contains 1 abstract method and must therefore be declared abstract or implement the remaining methods (fail::show) in /classes/AbstractSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }
  }
}
