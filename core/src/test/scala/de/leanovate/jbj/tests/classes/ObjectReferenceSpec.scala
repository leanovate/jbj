package de.leanovate.jbj.tests.classes

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class ObjectReferenceSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Object reference" should {
    "ZE2 object references" in {
      // classes/object_reference_001
      script(
        """<?php
          |
          |class Foo {
          |	public $name;
          |
          |	function Foo() {
          |		$this->name = "I'm Foo!\n";
          |	}
          |}
          |
          |$foo = new Foo;
          |echo $foo->name;
          |$bar = $foo;
          |$bar->name = "I'm Bar!\n";
          |
          |// In ZE1, we would expect "I'm Foo!"
          |echo $foo->name;
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """I'm Foo!
          |I'm Bar!
          |""".stripMargin
      )
    }
  }

}
