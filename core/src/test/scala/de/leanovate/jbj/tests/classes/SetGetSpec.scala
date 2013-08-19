package de.leanovate.jbj.tests.classes

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class SetGetSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "__set __get" should {
    "ZE2 __set() and __get() 1" in {
      // classes/__set__get_001
      script(
        """<?php
          |class setter {
          |	public $n;
          |	public $x = array('a' => 1, 'b' => 2, 'c' => 3);
          |
          |	function __get($nm) {
          |		echo "Getting [$nm]\n";
          |
          |		if (isset($this->x[$nm])) {
          |			$r = $this->x[$nm];
          |			echo "Returning: $r\n";
          |			return $r;
          |		}
          |		else {
          |			echo "Nothing!\n";
          |		}
          |	}
          |
          |	function __set($nm, $val) {
          |		echo "Setting [$nm] to $val\n";
          |
          |		if (isset($this->x[$nm])) {
          |			$this->x[$nm] = $val;
          |			echo "OK!\n";
          |		}
          |		else {
          |			echo "Not OK!\n";
          |		}
          |	}
          |}
          |
          |$foo = new Setter();
          |
          |// this doesn't go through __set()... should it?
          |$foo->n = 1;
          |
          |// the rest are fine...
          |$foo->a = 100;
          |$foo->a++;
          |$foo->z++;
          |var_dump($foo);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Setting [a] to 100
          |OK!
          |Getting [a]
          |Returning: 100
          |Setting [a] to 101
          |OK!
          |Getting [z]
          |Nothing!
          |Setting [z] to 1
          |Not OK!
          |object(setter)#1 (2) {
          |  ["n"]=>
          |  int(1)
          |  ["x"]=>
          |  array(3) {
          |    ["a"]=>
          |    int(101)
          |    ["b"]=>
          |    int(2)
          |    ["c"]=>
          |    int(3)
          |  }
          |}
          |""".stripMargin
      )
    }

    "ZE2 __get() signature check" in {
      // classes/__set_get_002
      script(
        """<?php
          |class Test {
          |	function __get($x,$y) {
          |	}
          |}
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Method Test::__get() must take exactly 1 argument in /classes/SetGetSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "ZE2 __set() signature check" in {
      // classes/__set_get_003
      script(
        """<?php
          |class Test {
          |	function __set() {
          |	}
          |}
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Method Test::__set() must take exactly 2 arguments in /classes/SetGetSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "ZE2 __set() and __get() 4" in {
      // classes/__set_get_004
      script(
        """<?php
          |class Test {
          |	protected $x;
          |
          |	function __get($name) {
          |		if (isset($this->x[$name])) {
          |			return $this->x[$name];
          |		}
          |		else
          |		{
          |			return NULL;
          |		}
          |	}
          |
          |	function __set($name, $val) {
          |		$this->x[$name] = $val;
          |	}
          |}
          |
          |$foo = new Test();
          |$bar = new Test();
          |$bar->baz = "Check";
          |
          |$foo->bar = $bar;
          |
          |var_dump($bar->baz);
          |var_dump($foo->bar->baz);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """string(5) "Check"
          |string(5) "Check"
          |""".stripMargin
      )
    }

    "ZE2 __set() and __get() 5" in {
      // classes/__set_get_005
      script(
        """<?php
          |class Test
          |{
          |	protected $x;
          |
          |	function __get($name) {
          |		echo __METHOD__ . "\n";
          |		if (isset($this->x[$name])) {
          |			return $this->x[$name];
          |		}
          |		else
          |		{
          |			return NULL;
          |		}
          |	}
          |
          |	function __set($name, $val) {
          |		echo __METHOD__ . "\n";
          |		$this->x[$name] = $val;
          |	}
          |}
          |
          |class AutoGen
          |{
          |	protected $x;
          |
          |	function __get($name) {
          |		echo __METHOD__ . "\n";
          |		if (!isset($this->x[$name])) {
          |			$this->x[$name] = new Test();
          |		}
          |		return $this->x[$name];
          |	}
          |
          |	function __set($name, $val) {
          |		echo __METHOD__ . "\n";
          |		$this->x[$name] = $val;
          |	}
          |}
          |
          |$foo = new AutoGen();
          |$foo->bar->baz = "Check";
          |
          |var_dump($foo->bar);
          |var_dump($foo->bar->baz);
          |
          |?>
          |===DONE===""".stripMargin
      ).result must haveOutput(
        """AutoGen::__get
          |Test::__set
          |AutoGen::__get
          |object(Test)#2 (1) {
          |  ["x":protected]=>
          |  array(1) {
          |    ["baz"]=>
          |    string(5) "Check"
          |  }
          |}
          |AutoGen::__get
          |Test::__get
          |string(5) "Check"
          |===DONE===""".stripMargin
      )
    }
  }
}
