package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class PassByReferenceSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Pass by reference" should {
    "passing of function parameters by reference" in {
      // lang/passByReference_001
      script(
        """<?php
          |function f($arg1, &$arg2)
          |{
          |	var_dump($arg1++);
          |	var_dump($arg2++);
          |}
          |
          |function g (&$arg1, &$arg2)
          |{
          |	var_dump($arg1);
          |	var_dump($arg2);
          |}
          |$a = 7;
          |$b = 15;
          |
          |f($a, $b);
          |
          |var_dump($a);
          |var_dump($b);
          |
          |$c=array(1);
          |g($c,$c[0]);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """int(7)
          |int(15)
          |int(7)
          |int(16)
          |array(1) {
          |  [0]=>
          |  &int(1)
          |}
          |int(1)
          |""".stripMargin
      )
    }

    "Attempt to pass a constant by reference" in {
      // lang/passByReference_002
      script(
        """<?php
          |
          |function f(&$arg1)
          |{
          |	var_dump($arg1++);
          |}
          |
          |f(2);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Only variables can be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 8
          |""".stripMargin
      )
    }

    "Implicit initialisation when passing by reference" in {
      // lang/passByReference_003
      script(
        """<?php
          |function passbyVal($val) {
          |	echo "\nInside passbyVal call:\n";
          |	var_dump($val);
          |}
          |
          |function passbyRef(&$ref) {
          |	echo "\nInside passbyRef call:\n";
          |	var_dump($ref);
          |}
          |
          |echo "\nPassing undefined by value\n";
          |passbyVal($undef1[0]);
          |echo "\nAfter call\n";
          |var_dump($undef1);
          |
          |echo "\nPassing undefined by reference\n";
          |passbyRef($undef2[0]);
          |echo "\nAfter call\n";
          |var_dump($undef2)
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Passing undefined by value
          |
          |Notice: Undefined variable: undef1 in /lang/PassByReferenceSpec.inlinePhp on line 13
          |
          |Inside passbyVal call:
          |NULL
          |
          |After call
          |
          |Notice: Undefined variable: undef1 in /lang/PassByReferenceSpec.inlinePhp on line 15
          |NULL
          |
          |Passing undefined by reference
          |
          |Inside passbyRef call:
          |NULL
          |
          |After call
          |array(1) {
          |  [0]=>
          |  NULL
          |}
          |""".stripMargin
      )
    }

    "Attempt to pass a constant by reference" in {
      // lang/passByReference_004
      script(
        """<?php
          |
          |function foo(&$ref)
          |{
          |	var_dump($ref);
          |}
          |
          |function bar($value)
          |{
          |	return $value;
          |}
          |
          |foo(bar(5));
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 13
          |int(5)
          |""".stripMargin
      )
    }

    "Pass uninitialised variables by reference and by value to test implicit initialisation." in {
      // lang/passByReference_005
      script(
        """<?php
          |
          |function v($val) {
          |  $val = "Val changed";
          |}
          |
          |function r(&$ref) {
          |  $ref = "Ref changed";
          |}
          |
          |
          |function vv($val1, $val2) {
          |  $val1 = "Val1 changed";
          |  $val2 = "Val2 changed";
          |}
          |
          |function vr($val, &$ref) {
          |  $val = "Val changed";
          |  $ref = "Ref changed";
          |}
          |
          |function rv(&$ref, $val) {
          |  $val = "Val changed";
          |  $ref = "Ref changed";
          |}
          |
          |function rr(&$ref1, &$ref2) {
          |  $ref1 = "Ref1 changed";
          |  $ref2 = "Ref2 changed";
          |}
          |
          |
          |class C {
          |
          |	function __construct($val, &$ref) {
          |	  $val = "Val changed";
          |	  $ref = "Ref changed";
          |	}
          |
          |	function v($val) {
          |	  $val = "Val changed";
          |	}
          |
          |	function r(&$ref) {
          |	  $ref = "Ref changed";
          |	}
          |
          |	function vv($val1, $val2) {
          |	  $val1 = "Val1 changed";
          |	  $val2 = "Val2 changed";
          |	}
          |
          |	function vr($val, &$ref) {
          |	  $val = "Val changed";
          |	  $ref = "Ref changed";
          |	}
          |
          |	function rv(&$ref, $val) {
          |	  $val = "Val changed";
          |	  $ref = "Ref changed";
          |	}
          |
          |	function rr(&$ref1, &$ref2) {
          |	  $ref1 = "Ref1 changed";
          |	  $ref2 = "Ref2 changed";
          |	}
          |
          |}
          |
          |echo "\n ---- Pass by ref / pass by val: functions ----\n";
          |unset($u1, $u2);
          |v($u1);
          |r($u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |vv($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |vr($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |rv($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |rr($u1, $u2);
          |var_dump($u1, $u2);
          |
          |
          |echo "\n\n ---- Pass by ref / pass by val: static method calls ----\n";
          |unset($u1, $u2);
          |C::v($u1);
          |C::r($u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |C::vv($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |C::vr($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |C::rv($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |C::rr($u1, $u2);
          |var_dump($u1, $u2);
          |
          |echo "\n\n ---- Pass by ref / pass by val: instance method calls ----\n";
          |unset($u1, $u2);
          |$c = new C($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |$c->v($u1);
          |$c->r($u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |$c->vv($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |$c->vr($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |$c->rv($u1, $u2);
          |var_dump($u1, $u2);
          |
          |unset($u1, $u2);
          |$c->rr($u1, $u2);
          |var_dump($u1, $u2);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          | ---- Pass by ref / pass by val: functions ----
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 72
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 74
          |NULL
          |string(11) "Ref changed"
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 77
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 77
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 78
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 78
          |NULL
          |NULL
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 81
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 82
          |NULL
          |string(11) "Ref changed"
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 85
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 86
          |string(11) "Ref changed"
          |NULL
          |string(12) "Ref1 changed"
          |string(12) "Ref2 changed"
          |
          |
          | ---- Pass by ref / pass by val: static method calls ----
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 95
          |
          |Strict Standards: Non-static method C::v() should not be called statically in /lang/PassByReferenceSpec.inlinePhp on line 95
          |
          |Strict Standards: Non-static method C::r() should not be called statically in /lang/PassByReferenceSpec.inlinePhp on line 96
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 97
          |NULL
          |string(11) "Ref changed"
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 100
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 100
          |
          |Strict Standards: Non-static method C::vv() should not be called statically in /lang/PassByReferenceSpec.inlinePhp on line 100
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 101
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 101
          |NULL
          |NULL
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 104
          |
          |Strict Standards: Non-static method C::vr() should not be called statically in /lang/PassByReferenceSpec.inlinePhp on line 104
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 105
          |NULL
          |string(11) "Ref changed"
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 108
          |
          |Strict Standards: Non-static method C::rv() should not be called statically in /lang/PassByReferenceSpec.inlinePhp on line 108
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 109
          |string(11) "Ref changed"
          |NULL
          |
          |Strict Standards: Non-static method C::rr() should not be called statically in /lang/PassByReferenceSpec.inlinePhp on line 112
          |string(12) "Ref1 changed"
          |string(12) "Ref2 changed"
          |
          |
          | ---- Pass by ref / pass by val: instance method calls ----
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 117
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 118
          |NULL
          |string(11) "Ref changed"
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 121
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 123
          |NULL
          |string(11) "Ref changed"
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 126
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 126
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 127
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 127
          |NULL
          |NULL
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 130
          |
          |Notice: Undefined variable: u1 in /lang/PassByReferenceSpec.inlinePhp on line 131
          |NULL
          |string(11) "Ref changed"
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 134
          |
          |Notice: Undefined variable: u2 in /lang/PassByReferenceSpec.inlinePhp on line 135
          |string(11) "Ref changed"
          |NULL
          |string(12) "Ref1 changed"
          |string(12) "Ref2 changed"
          |""".stripMargin
      )
    }
  }
}
