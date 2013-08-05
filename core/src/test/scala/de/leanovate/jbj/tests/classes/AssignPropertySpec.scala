package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class AssignPropertySpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Assign property" - {
    "ZE2 assign_op property of overloaded object" in {
      // classes/assign_op_property_001
      script(
        """<?php
          |
          |class Test {
          |	private $real_a = 2;
          |
          |	function __set($property, $value) {
          |	  if ($property == "a") {
          |	    $this->real_a = $value;
          |	  }
          |	}
          |
          |	function __get($property) {
          |	  if ($property == "a") {
          |	    return $this->real_a;
          |	  }
          |	}
          |}
          |
          |$obj = new Test;
          |var_dump($obj->a);
          |$obj->a += 2;
          |var_dump($obj->a);
          |echo "---Done---\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """int(2)
          |int(4)
          |---Done---
          |""".stripMargin
      )
    }
  }

}
