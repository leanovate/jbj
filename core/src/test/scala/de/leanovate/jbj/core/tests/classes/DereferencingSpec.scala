/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class DereferencingSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Dereferencing" should {
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
