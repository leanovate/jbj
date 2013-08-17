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

    "Pass uninitialised objects and arrays by reference to test implicit initialisation." in {
      // lang/passByReference_006
      script(
        """<?php
          |
          |function refs(&$ref1, &$ref2, &$ref3, &$ref4, &$ref5) {
          |  $ref1 = "Ref1 changed";
          |  $ref2 = "Ref2 changed";
          |  $ref3 = "Ref3 changed";
          |  $ref4 = "Ref4 changed";
          |  $ref5 = "Ref5 changed";
          |}
          |
          |
          |class C {
          |
          |	function __construct(&$ref1, &$ref2, &$ref3, &$ref4, &$ref5) {
          |	  $ref1 = "Ref1 changed";
          |	  $ref2 = "Ref2 changed";
          |	  $ref3 = "Ref3 changed";
          |	  $ref4 = "Ref4 changed";
          |	  $ref5 = "Ref5 changed";
          |	}
          |
          |	function refs(&$ref1, &$ref2, &$ref3, &$ref4, &$ref5) {
          |	  $ref1 = "Ref1 changed";
          |	  $ref2 = "Ref2 changed";
          |	  $ref3 = "Ref3 changed";
          |	  $ref4 = "Ref4 changed";
          |	  $ref5 = "Ref5 changed";
          |	}
          |
          |}
          |
          |echo "\n ---- Pass uninitialised array & object by ref: function call ---\n";
          |unset($u1, $u2, $u3, $u4, $u5);
          |refs($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
          |var_dump($u1, $u2, $u3, $u4, $u5);
          |
          |echo "\n ---- Pass uninitialised arrays & objects by ref: static method call ---\n";
          |unset($u1, $u2, $u3, $u4, $u5);
          |C::refs($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
          |var_dump($u1, $u2, $u3, $u4, $u5);
          |
          |echo "\n\n---- Pass uninitialised arrays & objects by ref: constructor ---\n";
          |unset($u1, $u2, $u3, $u4, $u5);
          |$c = new C($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
          |var_dump($u1, $u2, $u3, $u4, $u5);
          |
          |echo "\n ---- Pass uninitialised arrays & objects by ref: instance method call ---\n";
          |unset($u1, $u2, $u3, $u4, $u5);
          |$c->refs($u1[0], $u2[0][1], $u3->a, $u4->a->b, $u5->a->b->c);
          |var_dump($u1, $u2, $u3, $u4, $u5);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          | ---- Pass uninitialised array & object by ref: function call ---
          |array(1) {
          |  [0]=>
          |  string(12) "Ref1 changed"
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [1]=>
          |    string(12) "Ref2 changed"
          |  }
          |}
          |object(stdClass)#1 (1) {
          |  ["a"]=>
          |  string(12) "Ref3 changed"
          |}
          |object(stdClass)#3 (1) {
          |  ["a"]=>
          |  object(stdClass)#2 (1) {
          |    ["b"]=>
          |    string(12) "Ref4 changed"
          |  }
          |}
          |object(stdClass)#6 (1) {
          |  ["a"]=>
          |  object(stdClass)#5 (1) {
          |    ["b"]=>
          |    object(stdClass)#4 (1) {
          |      ["c"]=>
          |      string(12) "Ref5 changed"
          |    }
          |  }
          |}
          |
          | ---- Pass uninitialised arrays & objects by ref: static method call ---
          |
          |Strict Standards: Non-static method C::refs() should not be called statically in /lang/PassByReferenceSpec.inlinePhp on line 39
          |array(1) {
          |  [0]=>
          |  string(12) "Ref1 changed"
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [1]=>
          |    string(12) "Ref2 changed"
          |  }
          |}
          |object(stdClass)#7 (1) {
          |  ["a"]=>
          |  string(12) "Ref3 changed"
          |}
          |object(stdClass)#9 (1) {
          |  ["a"]=>
          |  object(stdClass)#8 (1) {
          |    ["b"]=>
          |    string(12) "Ref4 changed"
          |  }
          |}
          |object(stdClass)#12 (1) {
          |  ["a"]=>
          |  object(stdClass)#11 (1) {
          |    ["b"]=>
          |    object(stdClass)#10 (1) {
          |      ["c"]=>
          |      string(12) "Ref5 changed"
          |    }
          |  }
          |}
          |
          |
          |---- Pass uninitialised arrays & objects by ref: constructor ---
          |array(1) {
          |  [0]=>
          |  string(12) "Ref1 changed"
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [1]=>
          |    string(12) "Ref2 changed"
          |  }
          |}
          |object(stdClass)#14 (1) {
          |  ["a"]=>
          |  string(12) "Ref3 changed"
          |}
          |object(stdClass)#16 (1) {
          |  ["a"]=>
          |  object(stdClass)#15 (1) {
          |    ["b"]=>
          |    string(12) "Ref4 changed"
          |  }
          |}
          |object(stdClass)#19 (1) {
          |  ["a"]=>
          |  object(stdClass)#18 (1) {
          |    ["b"]=>
          |    object(stdClass)#17 (1) {
          |      ["c"]=>
          |      string(12) "Ref5 changed"
          |    }
          |  }
          |}
          |
          | ---- Pass uninitialised arrays & objects by ref: instance method call ---
          |array(1) {
          |  [0]=>
          |  string(12) "Ref1 changed"
          |}
          |array(1) {
          |  [0]=>
          |  array(1) {
          |    [1]=>
          |    string(12) "Ref2 changed"
          |  }
          |}
          |object(stdClass)#20 (1) {
          |  ["a"]=>
          |  string(12) "Ref3 changed"
          |}
          |object(stdClass)#22 (1) {
          |  ["a"]=>
          |  object(stdClass)#21 (1) {
          |    ["b"]=>
          |    string(12) "Ref4 changed"
          |  }
          |}
          |object(stdClass)#25 (1) {
          |  ["a"]=>
          |  object(stdClass)#24 (1) {
          |    ["b"]=>
          |    object(stdClass)#23 (1) {
          |      ["c"]=>
          |      string(12) "Ref5 changed"
          |    }
          |  }
          |}
          |""".stripMargin
      )
    }

    "Pass function and method calls by reference and by value." in {
      // lang/passByReReference_007
      script(
        """<?php
          |class C {
          |	static function sreturnVal() {
          |		global $a;
          |		return $a;
          |	}
          |
          |	static function &sreturnReference() {
          |		global $a;
          |		return $a;
          |	}
          |
          |	function returnVal() {
          |		global $a;
          |		return $a;
          |	}
          |
          |	function &returnReference() {
          |		global $a;
          |		return $a;
          |	}
          |}
          |
          |function returnVal() {
          |		global $a;
          |		return $a;
          |}
          |
          |function &returnReference() {
          |		global $a;
          |		return $a;
          |}
          |
          |
          |
          |function foo(&$ref) {
          |	var_dump($ref);
          |	$ref = "changed";
          |}
          |
          |
          |echo "Pass a function call that returns a value:\n";
          |$a = "original";
          |foo(returnVal());
          |var_dump($a);
          |
          |echo "Pass a function call that returns a reference:\n";
          |$a = "original";
          |foo(returnReference());
          |var_dump($a);
          |
          |
          |echo "\nPass a static method call that returns a value:\n";
          |$a = "original";
          |foo(C::sreturnVal());
          |var_dump($a);
          |
          |echo "Pass a static method call that returns a reference:\n";
          |$a = "original";
          |foo(C::sreturnReference());
          |var_dump($a);
          |
          |
          |$myC = new C;
          |echo "\nPass a method call that returns a value:\n";
          |$a = "original";
          |foo($myC->returnVal());
          |var_dump($a);
          |
          |echo "Pass a method call that returns a reference:\n";
          |$a = "original";
          |foo($myC->returnReference());
          |var_dump($a);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Pass a function call that returns a value:
          |
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 44
          |string(8) "original"
          |string(8) "original"
          |Pass a function call that returns a reference:
          |string(8) "original"
          |string(7) "changed"
          |
          |Pass a static method call that returns a value:
          |
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 55
          |string(8) "original"
          |string(8) "original"
          |Pass a static method call that returns a reference:
          |string(8) "original"
          |string(7) "changed"
          |
          |Pass a method call that returns a value:
          |
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 67
          |string(8) "original"
          |string(8) "original"
          |Pass a method call that returns a reference:
          |string(8) "original"
          |string(7) "changed"
          |""".stripMargin
      )
    }

    "Pass same variable by ref and by value. " in {
      // lang/passByReference_008
      script(
        """<?php
          |function valRef($x, &$y) {
          |	var_dump($x, $y);
          |	$x = 'changed.x';
          |	$y = 'changed.y';
          |}
          |
          |function refVal(&$x, $y) {
          |	var_dump($x, $y);
          |	$x = 'changed.x';
          |	$y = 'changed.y';
          |}
          |
          |
          |echo "\n\n-- Val, Ref --\n";
          |$a = 'original.a';
          |valRef($a, $a);
          |var_dump($a);
          |
          |echo "\n\n-- Ref, Val --\n";
          |$b = 'original.b';
          |refVal($b, $b);
          |var_dump($b);
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |
          |-- Val, Ref --
          |string(10) "original.a"
          |string(10) "original.a"
          |string(9) "changed.y"
          |
          |
          |-- Ref, Val --
          |string(10) "original.b"
          |string(10) "original.b"
          |string(9) "changed.x"
          |""".stripMargin
      )
    }

    "Assignement as argument" in {
      // lang/passByReference_009
      script(
        """<?php
          |    function foo(&$x, &$y) { $x = 1; echo $y ; }
          |
          |    $x = 0;
          |    foo($x, $x); // prints 1 ..
          |
          |
          |    function foo2($x, &$y, $z)
          |    {
          |      echo $x; // 0
          |      echo $y; // 1
          |      $y = 2;
          |    }
          |
          |    $x = 0;
          |
          |    foo2($x, $x, $x = 1);
          |    echo $x; // 2
          |?>""".stripMargin
      ).result must haveOutput(
        """1012""".stripMargin
      )
    }

    "Passing assignments by reference" in {
      // lang/passByReference_010
      script(
        """<?php
          |
          |function f(&$a) {
          |  var_dump($a);
          |  $a = "a.changed";
          |}
          |
          |echo "\n\n---> Pass constant assignment by reference:\n";
          |f($a="a.original");
          |var_dump($a);
          |
          |echo "\n\n---> Pass variable assignment by reference:\n";
          |unset($a);
          |$a = "a.original";
          |f($b = $a);
          |var_dump($a);
          |
          |echo "\n\n---> Pass reference assignment by reference:\n";
          |unset($a, $b);
          |$a = "a.original";
          |f($b =& $a);
          |var_dump($a);
          |
          |echo "\n\n---> Pass concat assignment by reference:\n";
          |unset($a, $b);
          |$b = "b.original";
          |$a = "a.original";
          |f($b .= $a);
          |var_dump($a);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |
          |---> Pass constant assignment by reference:
          |
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 9
          |string(10) "a.original"
          |string(10) "a.original"
          |
          |
          |---> Pass variable assignment by reference:
          |
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 15
          |string(10) "a.original"
          |string(10) "a.original"
          |
          |
          |---> Pass reference assignment by reference:
          |string(10) "a.original"
          |string(9) "a.changed"
          |
          |
          |---> Pass concat assignment by reference:
          |
          |Strict Standards: Only variables should be passed by reference in /lang/PassByReferenceSpec.inlinePhp on line 28
          |string(20) "b.originala.original"
          |string(10) "a.original"
          |""".stripMargin
      )
    }

  }
}
