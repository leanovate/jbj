/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class ConstantsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "class constants" should {
    "Class constant declarations" in {
      // classes/constants_basic_001.phpt
      script(
        """<?php
          |  define('DEFINED', 1234);
          |  $def = 456;
          |  define('DEFINED_TO_VAR', $def);
          |  define('DEFINED_TO_UNDEF_VAR', $undef);
          |
          |  class C
          |  {
          |      const c0 = UNDEFINED;
          |
          |      const c1 = 1, c2 = 1.5;
          |      const c3 =  + 1, c4 =  + 1.5;
          |      const c5 = -1, c6 = -1.5;
          |
          |      const c7 = __LINE__;
          |      const c8 = __FILE__;
          |      const c9 = __CLASS__;
          |      const c10 = __METHOD__;
          |      const c11 = __FUNCTION__;
          |
          |      const c12 = DEFINED;
          |      const c13 = DEFINED_TO_VAR;
          |      const c14 = DEFINED_TO_UNDEF_VAR;
          |
          |      const c15 = "hello1";
          |      const c16 = 'hello2';
          |      const c17 = C::c16;
          |      const c18 = self::c17;
          |  }
          |
          |  echo "\nAttempt to access various kinds of class constants:\n";
          |  var_dump(C::c0);
          |  var_dump(C::c1);
          |  var_dump(C::c2);
          |  var_dump(C::c3);
          |  var_dump(C::c4);
          |  var_dump(C::c5);
          |  var_dump(C::c6);
          |  var_dump(C::c7);
          |  var_dump(C::c8);
          |  var_dump(C::c9);
          |  var_dump(C::c10);
          |  var_dump(C::c11);
          |  var_dump(C::c12);
          |  var_dump(C::c13);
          |  var_dump(C::c14);
          |  var_dump(C::c15);
          |  var_dump(C::c16);
          |  var_dump(C::c17);
          |  var_dump(C::c18);
          |
          |  echo "\nExpecting fatal error:\n";
          |  var_dump(C::c19);
          |
          |  echo "\nYou should not see this.";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Notice: Undefined variable: undef in /classes/ConstantsSpec.inlinePhp on line 5
          |
          |Attempt to access various kinds of class constants:
          |
          |Notice: Use of undefined constant UNDEFINED - assumed 'UNDEFINED' in /classes/ConstantsSpec.inlinePhp on line 9
          |string(9) "UNDEFINED"
          |int(1)
          |float(1.5)
          |int(1)
          |float(1.5)
          |int(-1)
          |float(-1.5)
          |int(15)
          |string(32) "/classes/ConstantsSpec.inlinePhp"
          |string(1) "C"
          |string(1) "C"
          |string(0) ""
          |int(1234)
          |int(456)
          |NULL
          |string(6) "hello1"
          |string(6) "hello2"
          |string(6) "hello2"
          |string(6) "hello2"
          |
          |Expecting fatal error:
          |
          |Fatal error: Undefined class constant 'c19' in /classes/ConstantsSpec.inlinePhp on line 53
          |""".stripMargin
      )
    }

    "Basic class support - defining and reading a class constant." in {
      // classes/constants_basic_002.phpt
      script(
        """<?php
          |  class aclass
          |  {
          |      const myConst = "hello";
          |  }
          |
          |  echo "\nRead class constant.\n";
          |  var_dump(aclass::myConst);
          |
          |  echo "\nFail to read class constant from instance.\n";
          |  $myInstance = new aclass();
          |  var_dump($myInstance->myConst);
          |
          |  echo "\nClass constant not visible in object var_dump.\n";
          |  var_dump($myInstance)
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Read class constant.
          |string(5) "hello"
          |
          |Fail to read class constant from instance.
          |
          |Notice: Undefined property: aclass::$myConst in /classes/ConstantsSpec.inlinePhp on line 12
          |NULL
          |
          |Class constant not visible in object var_dump.
          |object(aclass)#1 (0) {
          |}
          |""".stripMargin
      )
    }

    "Ensure class properties and constants can be defined in terms of constants that are not known at compile time." in {
      // classes/constants_basic_003.phpt
      script(
        """<?php
          |  include 'constants_basic_003.inc';
          |  class B
          |  {
          |      public static $a = A::MY_CONST;
          |      public static $c = C::MY_CONST;
          |      const ca = A::MY_CONST;
          |      const cc = C::MY_CONST;
          |  }
          |
          |  class C
          |  {
          |      const MY_CONST = "hello from C";
          |  }
          |
          |  var_dump(B::$a);
          |  var_dump(B::$c);
          |  var_dump(B::ca);
          |  var_dump(B::cc);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(12) "hello from A"
          |string(12) "hello from C"
          |string(12) "hello from A"
          |string(12) "hello from C"
          |""".stripMargin
      )
    }

    "Test properties with array default values using class constants as keys and values." in {
      // classes/constants_basic_004.phpt
      script(
        """<?php
          |  class X
          |  {
          |      // Static and instance array using class constants
          |      public static $sa_x = array(B::KEY => B::VALUE);
          |      public $a_x = array(B::KEY => B::VALUE);
          |  }
          |
          |  class B
          |  {
          |      const KEY = "key";
          |      const VALUE = "value";
          |
          |      // Static and instance array using class constants with self
          |      public static $sa_b = array(self::KEY => self::VALUE);
          |      public $a_b = array(self::KEY => self::VALUE);
          |  }
          |
          |  class C extends B
          |  {
          |      // Static and instance array using class constants with parent
          |      public static $sa_c_parent = array(parent::KEY => parent::VALUE);
          |      public $a_c_parent = array(parent::KEY => parent::VALUE);
          |
          |      // Static and instance array using class constants with self (constants should be inherited)
          |      public static $sa_c_self = array(self::KEY => self::VALUE);
          |      public $a_c_self = array(self::KEY => self::VALUE);
          |
          |      // Should also include inherited properties from B.
          |  }
          |
          |  echo "\nStatic properties:\n";
          |  var_dump(X::$sa_x, B::$sa_b, C::$sa_b, C::$sa_c_parent, C::$sa_c_self);
          |
          |  echo "\nInstance properties:\n";
          |  $x = new x;
          |  $b = new B;
          |  $c = new C;
          |  var_dump($x, $b, $c);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Static properties:
          |array(1) {
          |  ["key"]=>
          |  string(5) "value"
          |}
          |array(1) {
          |  ["key"]=>
          |  string(5) "value"
          |}
          |array(1) {
          |  ["key"]=>
          |  string(5) "value"
          |}
          |array(1) {
          |  ["key"]=>
          |  string(5) "value"
          |}
          |array(1) {
          |  ["key"]=>
          |  string(5) "value"
          |}
          |
          |Instance properties:
          |object(X)#1 (1) {
          |  ["a_x"]=>
          |  array(1) {
          |    ["key"]=>
          |    string(5) "value"
          |  }
          |}
          |object(B)#2 (1) {
          |  ["a_b"]=>
          |  array(1) {
          |    ["key"]=>
          |    string(5) "value"
          |  }
          |}
          |object(C)#3 (3) {
          |  ["a_c_parent"]=>
          |  array(1) {
          |    ["key"]=>
          |    string(5) "value"
          |  }
          |  ["a_c_self"]=>
          |  array(1) {
          |    ["key"]=>
          |    string(5) "value"
          |  }
          |  ["a_b"]=>
          |  array(1) {
          |    ["key"]=>
          |    string(5) "value"
          |  }
          |}
          |""".stripMargin
      )
    }

    "Test constants with default values based on other constants." in {
      // classes/constants_basic_005.phpt
      script(
        """<?php
          |  class C
          |  {
          |      const CONST_2 = self::CONST_1;
          |      const CONST_1 = self::BASE_CONST;
          |      const BASE_CONST = 'hello';
          |  }
          |  var_dump(C::CONST_1, C::CONST_2);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(5) "hello"
          |string(5) "hello"
          |""".stripMargin
      )
    }

    "Ensure class constants are not evaluated when a class is looked up to resolve inheritance during runtime." in {
      // classes/constants_basic_006.phpt
      script(
        """<?php
          |  class C
          |  {
          |      const X = E::A;
          |      public static $a = array(K => D::V, E::A => K);
          |  }
          |
          |  eval('class D extends C { const V = \'test\'; }');
          |
          |  class E extends D
          |  {
          |      const A = "hello";
          |  }
          |
          |  define('K', "nasty");
          |
          |  var_dump(C::X, C::$a, D::X, D::$a, E::X, E::$a);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(5) "hello"
          |array(2) {
          |  ["nasty"]=>
          |  string(4) "test"
          |  ["hello"]=>
          |  string(5) "nasty"
          |}
          |string(5) "hello"
          |array(2) {
          |  ["nasty"]=>
          |  string(4) "test"
          |  ["hello"]=>
          |  string(5) "nasty"
          |}
          |string(5) "hello"
          |array(2) {
          |  ["nasty"]=>
          |  string(4) "test"
          |  ["hello"]=>
          |  string(5) "nasty"
          |}
          |""".stripMargin
      )
    }
  }
}
