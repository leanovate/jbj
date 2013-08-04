package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class ObjectReferenceSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Object reference" - {
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
