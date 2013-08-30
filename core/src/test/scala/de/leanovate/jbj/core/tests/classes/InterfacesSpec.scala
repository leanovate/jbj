package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class InterfacesSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "interfaces" should {
    "ZE2 interfaces" in {
      // classes/interfaces_001.phpt
      script(
        """<?php
          |
          |interface Throwable {
          |	public function getMessage();
          |}
          |
          |class Exception_foo implements Throwable {
          |	public $foo = "foo";
          |
          |	public function getMessage() {
          |		return $this->foo;
          |	}
          |}
          |
          |$foo = new Exception_foo;
          |echo $foo->getMessage() . "\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |""".stripMargin
      )
    }

    "ZE2 interface with an unimplemented method" in {
      // classes/interfaces_002.phpt
      script(
        """<?php
          |
          |interface Throwable {
          |	public function getMessage();
          |	public function getErrno();
          |}
          |
          |class Exception_foo implements Throwable {
          |	public $foo = "foo";
          |
          |	public function getMessage() {
          |		return $this->foo;
          |	}
          |}
          |
          |// this should die -- Exception class must be abstract...
          |$foo = new Exception_foo;
          |echo "Message: " . $foo->getMessage() . "\n";
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Class Exception_foo contains 1 abstract method and must therefore be declared abstract or implement the remaining methods (Throwable::getErrno) in /classes/InterfacesSpec.inlinePhp on line 11
          |""".stripMargin
      )
    }
  }
}
