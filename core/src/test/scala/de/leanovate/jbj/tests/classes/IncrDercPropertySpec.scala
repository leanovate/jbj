package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class IncrDercPropertySpec extends FreeSpec with TestJbjExecutor with MustMatchers{
  "Increment decrement property" - {
    "ZE2 post increment/decrement property of overloaded object" in {
      // classes/incdec_property_001
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
          |$obj->a++;
          |var_dump($obj->a);
          |echo "---Done---\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """int(2)
          |int(3)
          |---Done---
          |""".stripMargin
      )
    }

    "ZE2 post increment/decrement property of overloaded object with assignment" in {
      // classes/incdec_property_002
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
          |$t1 = $obj->a++;
          |var_dump($obj->a);
          |echo "---Done---\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """int(2)
          |int(3)
          |---Done---
          |""".stripMargin
      )
    }

    "ZE2 pre increment/decrement property of overloaded object" in {
      // classes/incdec_property_003
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
          |++$obj->a;
          |var_dump($obj->a);
          |echo "---Done---\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """int(2)
          |int(3)
          |---Done---
          |""".stripMargin
      )
    }

    "ZE2 pre increment/decrement property of overloaded object with assignment" in {
      // classes/incdec_property_003
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
          |$t1 = ++$obj->a;
          |var_dump($obj->a);
          |echo "---Done---\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """int(2)
          |int(3)
          |---Done---
          |""".stripMargin
      )
    }
  }
}
