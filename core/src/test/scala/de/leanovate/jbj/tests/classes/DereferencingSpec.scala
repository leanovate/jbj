package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class DereferencingSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Dereferencing" - {
    "ZE2 dereferencing of objects from methods" in {
      // classes/dereferencing_001
      script(
        """<?php
          |
          |class Name {
          |	function Name($_name) {
          |		$this->name = $_name;
          |	}
          |
          |	function display() {
          |		echo $this->name . "\n";
          |	}
          |}
          |
          |class Person {
          |	private $name;
          |
          |	function person($_name, $_address) {
          |		$this->name = new Name($_name);
          |	}
          |
          |	function getName() {
          |		return $this->name;
          |	}
          |}
          |
          |$person = new Person("John", "New York");
          |$person->getName()->display();
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """John
          |""".stripMargin
      )
    }
  }

}
