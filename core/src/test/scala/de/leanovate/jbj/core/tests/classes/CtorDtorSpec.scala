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

    "ZE2 Do not call destructors if constructor fails" in {
      // classes/ctor_failure.phpt
      script(
        """<?php
          |
          |class Test
          |{
          |    function __construct($msg) {
          |        echo __METHOD__ . "($msg)\n";
          |        throw new Exception($msg);
          |    }
          |
          |    function __destruct() {
          |        echo __METHOD__ . "\n";
          |    }
          |}
          |
          |try
          |{
          |    $o = new Test('Hello');
          |    unset($o);
          |}
          |catch (Exception $e)
          |{
          |    echo 'Caught ' . get_class($e) . '(' . $e->getMessage() . ")\n";
          |}
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Test::__construct(Hello)
          |Caught Exception(Hello)
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 A class constructor must keep the signature of an interface" in {
      // classes/ctor_in_interface_01.phpt
      script(
        """<?php
          |interface constr
          |{
          |	function __construct();
          |}
          |
          |class implem implements constr
          |{
          |	function __construct($a)
          |	{
          |	}
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Declaration of implem::__construct() must be compatible with constr::__construct() in /classes/CtorDtorSpec.inlinePhp on line 9
          |""".stripMargin
      )
    }

    "ZE2 A class constructor must keep the signature of all interfaces" in {
      // classes/ctor_in_interface_02.phpt
      script(
        """<?php
          |interface constr1
          |{
          |	function __construct();
          |}
          |
          |interface constr2 extends constr1
          |{
          |}
          |
          |class implem12 implements constr2
          |{
          |	function __construct()
          |	{
          |	}
          |}
          |
          |interface constr3
          |{
          |	function __construct($a);
          |}
          |
          |class implem13 implements constr1, constr3
          |{
          |	function __construct()
          |	{
          |	}
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Declaration of implem13::__construct() must be compatible with constr3::__construct($a) in /classes/CtorDtorSpec.inlinePhp on line 25
          |""".stripMargin
      )
    }

    "ZE2 A class constructor must keep the signature of base class interfaces" in {
      // classes/ctor_in_interface_03.phpt
      script(
        """<?php
          |interface constr
          |{
          |	function __construct();
          |}
          |
          |abstract class implem implements constr
          |{
          |}
          |
          |class derived extends implem
          |{
          |	function __construct($a)
          |	{
          |	}
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Declaration of derived::__construct() must be compatible with constr::__construct() in /classes/CtorDtorSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "ZE2 A class constructor must keep the signature of base class interfaces" in {
      // classes/ctor_in_interface_04.phpt
      script(
        """<?php
          |interface constr
          |{
          |	function __construct();
          |}
          |
          |class implem implements constr
          |{
          |	function __construct()
          |	{
          |	}
          |}
          |
          |class derived extends implem
          |{
          |	function __construct($a)
          |	{
          |	}
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Declaration of derived::__construct() must be compatible with constr::__construct() in /classes/CtorDtorSpec.inlinePhp on line 16
          |""".stripMargin
      )
    }

    "ZE2 The child class can re-use the parent class name for a function member" in {
      // classes/ctor_name_clash.phpt
      script(
        """<?php
          |class base {
          |  function base() {
          |    echo __CLASS__."::".__FUNCTION__."\n";
          |  }
          |}
          |
          |class derived extends base {
          |  function base() {
          |    echo __CLASS__."::".__FUNCTION__."\n";
          |  }
          |}
          |
          |$obj = new derived();
          |$obj->base();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """base::base
          |derived::base
          |""".stripMargin
      )
    }

    "ZE2 A private constructor cannot be called" in {
      // classes/ctor_visibility.phpt
      script(
        """<?php
          |
          |class Test
          |{
          |    function __construct()
          |    {
          |        echo __METHOD__ . "()\n";
          |    }
          |}
          |
          |class Derived extends Test
          |{
          |	function __construct()
          |	{
          |        echo __METHOD__ . "()\n";
          |		parent::__construct();
          |	}
          |
          |	static function f()
          |	{
          |		new Derived;
          |	}
          |}
          |
          |Derived::f();
          |
          |class TestPriv
          |{
          |    private function __construct()
          |    {
          |        echo __METHOD__ . "()\n";
          |    }
          |
          |	static function f()
          |	{
          |		new TestPriv;
          |	}
          |}
          |
          |TestPriv::f();
          |
          |class DerivedPriv extends TestPriv
          |{
          |	function __construct()
          |	{
          |        echo __METHOD__ . "()\n";
          |		parent::__construct();
          |	}
          |
          |	static function f()
          |	{
          |		new DerivedPriv;
          |	}
          |}
          |
          |DerivedPriv::f();
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """Derived::__construct()
          |Test::__construct()
          |TestPriv::__construct()
          |DerivedPriv::__construct()
          |
          |Fatal error: Cannot call private TestPriv::__construct() in /classes/CtorDtorSpec.inlinePhp on line 47
          |""".stripMargin
      )
    }
  }
}
