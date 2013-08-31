/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class CtorDtorSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Constructor and destructor" should {
    "ZE2 The new constructor/destructor is called" in {
      // classes/ctor_dtor.phpt
      script(
        """<?php
          |
          |class early {
          |	function early() {
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |	}
          |	function __destruct() {
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |	}
          |}
          |
          |class late {
          |	function __construct() {
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |	}
          |	function __destruct() {
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |	}
          |}
          |
          |$t = new early();
          |$t->early();
          |unset($t);
          |$t = new late();
          |//unset($t); delay to end of script
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """early::early
          |early::early
          |early::__destruct
          |late::__construct
          |Done
          |late::__destruct
          |""".stripMargin
      )
    }

    "ZE2 A derived class can use the inherited constructor/destructor" in {
      // classes/ctor_dtor_inheritance.phpt
      script(
        """<?php
          |
          |// This test checks for:
          |// - inherited constructors/destructors are not called automatically
          |// - base classes know about derived properties in constructor/destructor
          |// - base class constructors/destructors know the instanciated class name
          |
          |class base {
          |	public $name;
          |
          |	function __construct() {
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |		$this->name = 'base';
          |		print_r($this);
          |	}
          |
          |	function __destruct() {
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |		print_r($this);
          |	}
          |}
          |
          |class derived extends base {
          |	public $other;
          |
          |	function __construct() {
          |		$this->name = 'init';
          |		$this->other = 'other';
          |		print_r($this);
          |		parent::__construct();
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |		$this->name = 'derived';
          |		print_r($this);
          |	}
          |
          |	function __destruct() {
          |		parent::__destruct();
          |		echo __CLASS__ . "::" . __FUNCTION__ . "\n";
          |		print_r($this);
          |	}
          |}
          |
          |echo "Testing class base\n";
          |$t = new base();
          |unset($t);
          |echo "Testing class derived\n";
          |$t = new derived();
          |unset($t);
          |
          |echo "Done\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Testing class base
          |base::__construct
          |base Object
          |(
          |    [name] => base
          |)
          |base::__destruct
          |base Object
          |(
          |    [name] => base
          |)
          |Testing class derived
          |derived Object
          |(
          |    [other] => other
          |    [name] => init
          |)
          |base::__construct
          |derived Object
          |(
          |    [other] => other
          |    [name] => base
          |)
          |derived::__construct
          |derived Object
          |(
          |    [other] => other
          |    [name] => derived
          |)
          |base::__destruct
          |derived Object
          |(
          |    [other] => other
          |    [name] => derived
          |)
          |derived::__destruct
          |derived Object
          |(
          |    [other] => other
          |    [name] => derived
          |)
          |Done
          |""".stripMargin
      )
    }
  }
}
