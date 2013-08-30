package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class InterfacesSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "interfaces" should {
    "ZE2 An interface extends base interfaces" in {
      // classes/interface_doubled.phpt
      script(
        """<?php
          |
          |interface if_a {
          |	function f_a();
          |}
          |
          |interface if_b {
          |	function f_b();
          |}
          |
          |interface if_c extends if_a, if_b {
          |	function f_c();
          |}
          |
          |interface if_d extends if_a, if_b {
          |	function f_d();
          |}
          |
          |interface if_e {
          |	function f_d();
          |}
          |
          |interface if_f extends /*if_e,*/ if_a, if_b, if_c, if_d /*, if_e*/ {
          |}
          |
          |class base {
          |	function test($class) {
          |		echo "is_a(" . get_class($this) . ", $class) ". (($this instanceof $class) ? "yes\n" : "no\n");
          |	}
          |}
          |
          |echo "class_a\n";
          |
          |class class_a extends base implements if_a {
          |	function f_a() {}
          |	function f_b() {}
          |	function f_c() {}
          |	function f_d() {}
          |	function f_e() {}
          |}
          |
          |$t = new class_a();
          |echo $t->test('if_a');
          |echo $t->test('if_b');
          |echo $t->test('if_c');
          |echo $t->test('if_d');
          |echo $t->test('if_e');
          |
          |echo "class_b\n";
          |
          |class class_b extends base implements if_a, if_b {
          |	function f_a() {}
          |	function f_b() {}
          |	function f_c() {}
          |	function f_d() {}
          |	function f_e() {}
          |}
          |
          |$t = new class_b();
          |echo $t->test('if_a');
          |echo $t->test('if_b');
          |echo $t->test('if_c');
          |echo $t->test('if_d');
          |echo $t->test('if_e');
          |
          |echo "class_c\n";
          |
          |class class_c extends base implements if_c {
          |	function f_a() {}
          |	function f_b() {}
          |	function f_c() {}
          |	function f_d() {}
          |	function f_e() {}
          |}
          |
          |$t = new class_c();
          |echo $t->test('if_a');
          |echo $t->test('if_b');
          |echo $t->test('if_c');
          |echo $t->test('if_d');
          |echo $t->test('if_e');
          |
          |echo "class_d\n";
          |
          |class class_d extends base implements if_d{
          |	function f_a() {}
          |	function f_b() {}
          |	function f_c() {}
          |	function f_d() {}
          |	function f_e() {}
          |}
          |
          |$t = new class_d();
          |echo $t->test('if_a');
          |echo $t->test('if_b');
          |echo $t->test('if_c');
          |echo $t->test('if_d');
          |echo $t->test('if_e');
          |
          |echo "class_e\n";
          |
          |class class_e extends base implements if_a, if_b, if_c, if_d {
          |	function f_a() {}
          |	function f_b() {}
          |	function f_c() {}
          |	function f_d() {}
          |	function f_e() {}
          |}
          |
          |$t = new class_e();
          |echo $t->test('if_a');
          |echo $t->test('if_b');
          |echo $t->test('if_c');
          |echo $t->test('if_d');
          |echo $t->test('if_e');
          |
          |echo "class_f\n";
          |
          |class class_f extends base implements if_e {
          |	function f_a() {}
          |	function f_b() {}
          |	function f_c() {}
          |	function f_d() {}
          |	function f_e() {}
          |}
          |
          |$t = new class_f();
          |echo $t->test('if_a');
          |echo $t->test('if_b');
          |echo $t->test('if_c');
          |echo $t->test('if_d');
          |echo $t->test('if_e');
          |
          |echo "class_g\n";
          |
          |class class_g extends base implements if_f {
          |	function f_a() {}
          |	function f_b() {}
          |	function f_c() {}
          |	function f_d() {}
          |	function f_e() {}
          |}
          |
          |$t = new class_g();
          |echo $t->test('if_a');
          |echo $t->test('if_b');
          |echo $t->test('if_c');
          |echo $t->test('if_d');
          |echo $t->test('if_e');
          |
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """class_a
          |is_a(class_a, if_a) yes
          |is_a(class_a, if_b) no
          |is_a(class_a, if_c) no
          |is_a(class_a, if_d) no
          |is_a(class_a, if_e) no
          |class_b
          |is_a(class_b, if_a) yes
          |is_a(class_b, if_b) yes
          |is_a(class_b, if_c) no
          |is_a(class_b, if_d) no
          |is_a(class_b, if_e) no
          |class_c
          |is_a(class_c, if_a) yes
          |is_a(class_c, if_b) yes
          |is_a(class_c, if_c) yes
          |is_a(class_c, if_d) no
          |is_a(class_c, if_e) no
          |class_d
          |is_a(class_d, if_a) yes
          |is_a(class_d, if_b) yes
          |is_a(class_d, if_c) no
          |is_a(class_d, if_d) yes
          |is_a(class_d, if_e) no
          |class_e
          |is_a(class_e, if_a) yes
          |is_a(class_e, if_b) yes
          |is_a(class_e, if_c) yes
          |is_a(class_e, if_d) yes
          |is_a(class_e, if_e) no
          |class_f
          |is_a(class_f, if_a) no
          |is_a(class_f, if_b) no
          |is_a(class_f, if_c) no
          |is_a(class_f, if_d) no
          |is_a(class_f, if_e) yes
          |class_g
          |is_a(class_g, if_a) yes
          |is_a(class_g, if_b) yes
          |is_a(class_g, if_c) yes
          |is_a(class_g, if_d) yes
          |is_a(class_g, if_e) no
          |===DONE===
          |""".stripMargin
      )
    }

    "ZE2 An interface is inherited" in {
      // classes/interface_implemented.phpt
      script(
        """<?php
          |
          |interface if_a {
          |	function f_a();
          |}
          |
          |interface if_b extends if_a {
          |	function f_b();
          |}
          |
          |class base {
          |	function _is_a($sub) {
          |		echo 'is_a('.get_class($this).', '.$sub.') = '.(($this instanceof $sub) ? 'yes' : 'no')."\n";
          |	}
          |	function test() {
          |		echo $this->_is_a('base');
          |		echo $this->_is_a('derived_a');
          |		echo $this->_is_a('derived_b');
          |		echo $this->_is_a('derived_c');
          |		echo $this->_is_a('derived_d');
          |		echo $this->_is_a('if_a');
          |		echo $this->_is_a('if_b');
          |		echo "\n";
          |	}
          |}
          |
          |class derived_a extends base implements if_a {
          |	function f_a() {}
          |}
          |
          |class derived_b extends base implements if_a, if_b {
          |	function f_a() {}
          |	function f_b() {}
          |}
          |
          |class derived_c extends derived_a implements if_b {
          |	function f_b() {}
          |}
          |
          |class derived_d extends derived_c {
          |}
          |
          |$t = new base();
          |$t->test();
          |
          |$t = new derived_a();
          |$t->test();
          |
          |$t = new derived_b();
          |$t->test();
          |
          |$t = new derived_c();
          |$t->test();
          |
          |$t = new derived_d();
          |$t->test();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """is_a(base, base) = yes
          |is_a(base, derived_a) = no
          |is_a(base, derived_b) = no
          |is_a(base, derived_c) = no
          |is_a(base, derived_d) = no
          |is_a(base, if_a) = no
          |is_a(base, if_b) = no
          |
          |is_a(derived_a, base) = yes
          |is_a(derived_a, derived_a) = yes
          |is_a(derived_a, derived_b) = no
          |is_a(derived_a, derived_c) = no
          |is_a(derived_a, derived_d) = no
          |is_a(derived_a, if_a) = yes
          |is_a(derived_a, if_b) = no
          |
          |is_a(derived_b, base) = yes
          |is_a(derived_b, derived_a) = no
          |is_a(derived_b, derived_b) = yes
          |is_a(derived_b, derived_c) = no
          |is_a(derived_b, derived_d) = no
          |is_a(derived_b, if_a) = yes
          |is_a(derived_b, if_b) = yes
          |
          |is_a(derived_c, base) = yes
          |is_a(derived_c, derived_a) = yes
          |is_a(derived_c, derived_b) = no
          |is_a(derived_c, derived_c) = yes
          |is_a(derived_c, derived_d) = no
          |is_a(derived_c, if_a) = yes
          |is_a(derived_c, if_b) = yes
          |
          |is_a(derived_d, base) = yes
          |is_a(derived_d, derived_a) = yes
          |is_a(derived_d, derived_b) = no
          |is_a(derived_d, derived_c) = yes
          |is_a(derived_d, derived_d) = yes
          |is_a(derived_d, if_a) = yes
          |is_a(derived_d, if_b) = yes
          |
          |""".stripMargin
      )
    }

    "ZE2 An interface cannot be instantiated" in {
      // classes/interface_instantiate.phpt
      script(
        """<?php
          |
          |interface if_a {
          |	function f_a();
          |}
          |
          |$t = new if_a();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot instantiate interface if_a in /classes/InterfacesSpec.inlinePhp on line 7
          |""".stripMargin
      )
    }

    "ZE2 An interface cannot have properties" in {
      // classes/interface_member.phpt
      script(
        """<?php
          |
          |interface if_a {
          |	public $member;
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Interfaces may not include member variables in /classes/InterfacesSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "ZE2 An interface method must be abstract" in {
      // classes/interface_method.phpt
      script(
        """<?php
          |
          |interface if_a {
          |	function err() {};
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Interface function if_a::err() cannot contain body in /classes/InterfacesSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "ZE2 An interface method cannot be final" in {
      // classes/interface_method_final.phpt
      script(
        """<?php
          |
          |class if_a {
          |	abstract final function err();
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot use the final modifier on an abstract class member in /classes/InterfacesSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "ZE2 An interface method cannot be private" in {
      // classes/interface_method_private.phpt
      script(
        """<?php
          |
          |interface if_a {
          |	abstract private function err();
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access type for interface method if_a::err() must be omitted in /classes/InterfacesSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "ZE2 An interface must be implemented" in {
      // classes/interface_must_be_implemented.phpt
      script(
        """<?php
          |
          |interface if_a {
          |	function f_a();
          |}
          |
          |class derived_a implements if_a {
          |}
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Class derived_a contains 1 abstract method and must therefore be declared abstract or implement the remaining methods (if_a::f_a) in /classes/InterfacesSpec.inlinePhp on line 7
          |""".stripMargin
      )
    }

    "ZE2 An interface method allows additional default arguments" in {
      // classes/interface_optional_arg.phpt
      script(
        """<?php
          |
          |error_reporting(4095);
          |
          |interface test {
          |	public function bar();
          |}
          |
          |class foo implements test {
          |
          |	public function bar($foo = NULL) {
          |		echo "foo\n";
          |	}
          |}
          |
          |$foo = new foo;
          |$foo->bar();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo
          |""".stripMargin
      )
    }

    "default argument value in interface implementation" in {
      // classes/interface_optional_arg_002.phpt
      script(
        """<?php
          |
          |interface test {
          |	public function bar();
          |}
          |
          |class foo implements test {
          |
          |	public function bar($arg = 2) {
          |		var_dump($arg);
          |	}
          |}
          |
          |$foo = new foo;
          |$foo->bar();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(2)
          |""".stripMargin
      )
    }

    "default argument value in and in implementing class with interface in included file" in {
      // classes/interface_optional_arg_003.phpt
      script(
        """<?php
          |include 'interface_optional_arg_003.inc';
          |
          |class C implements I {
          |  function f($a = 2) {
          |  	var_dump($a);
          |  }
          |}
          |
          |$c = new C;
          |$c->f();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(2)
          |""".stripMargin
      )
    }

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
