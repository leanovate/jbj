/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class StaticPropertiesSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Static properties" should {
    "ZE2 Initializing static properties to arrays" in {
      // classes/static_properties_001
      script(
        """<?php
          |
          |class test {
          |	static public $ar = array();
          |}
          |
          |var_dump(test::$ar);
          |
          |test::$ar[] = 1;
          |
          |var_dump(test::$ar);
          |
          |echo "Done\n";
          |?>""".stripMargin
      ).result must haveOutput(
        """array(0) {
          |}
          |array(1) {
          |  [0]=>
          |  int(1)
          |}
          |Done
          |""".stripMargin
      )
    }
    "Attempting to access static properties using instance property syntax" in {
      // classes/static_properties_003.phpt
      script(
        """<?php
          |class C {
          |    public static $x = 'C::$x';
          |    protected static $y = 'C::$y';
          |}
          |
          |$c = new C;
          |
          |echo "\n--> Access visible static prop like instance prop:\n";
          |var_dump(isset($c->x));
          |unset($c->x);
          |echo $c->x;
          |$c->x = 1;
          |$ref = 'ref';
          |$c->x =& $ref;
          |var_dump($c->x, C::$x);
          |
          |echo "\n--> Access non-visible static prop like instance prop:\n";
          |var_dump(isset($c->y));
          |//unset($c->y);		// Fatal error, tested in static_properties_003_error1.phpt
          |//echo $c->y;		// Fatal error, tested in static_properties_003_error2.phpt
          |//$c->y = 1;		// Fatal error, tested in static_properties_003_error3.phpt
          |//$c->y =& $ref;	// Fatal error, tested in static_properties_003_error4.phpt
          |?>
          |==Done==
          |""".stripMargin
      ).result must haveOutput(
        """
          |--> Access visible static prop like instance prop:
          |bool(false)
          |
          |Strict Standards: Accessing static property C::$x as non static in /classes/StaticPropertiesSpec.inlinePhp on line 11
          |
          |Strict Standards: Accessing static property C::$x as non static in /classes/StaticPropertiesSpec.inlinePhp on line 12
          |
          |Notice: Undefined property: C::$x in /classes/StaticPropertiesSpec.inlinePhp on line 12
          |
          |Strict Standards: Accessing static property C::$x as non static in /classes/StaticPropertiesSpec.inlinePhp on line 13
          |
          |Strict Standards: Accessing static property C::$x as non static in /classes/StaticPropertiesSpec.inlinePhp on line 15
          |
          |Strict Standards: Accessing static property C::$x as non static in /classes/StaticPropertiesSpec.inlinePhp on line 16
          |string(3) "ref"
          |string(5) "C::$x"
          |
          |--> Access non-visible static prop like instance prop:
          |bool(false)
          |==Done==
          |""".stripMargin
      )
    }

    "Inherited static properties can be separated from their reference set." in {
      // classes/static_properties_004.phpt
      script(
        """<?php
          |class C { public static $p = 'original'; }
          |class D extends C {	}
          |class E extends D {	}
          |
          |echo "\nInherited static properties refer to the same value accross classes:\n";
          |var_dump(C::$p, D::$p, E::$p);
          |
          |echo "\nChanging one changes all the others:\n";
          |D::$p = 'changed.all';
          |var_dump(C::$p, D::$p, E::$p);
          |
          |echo "\nBut because this is implemented using PHP references, the reference set can easily be split:\n";
          |$ref = 'changed.one';
          |D::$p =& $ref;
          |var_dump(C::$p, D::$p, E::$p);
          |?>
          |==Done==
          |""".stripMargin
      ).result must haveOutput(
        """
          |Inherited static properties refer to the same value accross classes:
          |string(8) "original"
          |string(8) "original"
          |string(8) "original"
          |
          |Changing one changes all the others:
          |string(11) "changed.all"
          |string(11) "changed.all"
          |string(11) "changed.all"
          |
          |But because this is implemented using PHP references, the reference set can easily be split:
          |string(11) "changed.all"
          |string(11) "changed.one"
          |string(11) "changed.all"
          |==Done==
          |""".stripMargin
      )
    }

    "Assigning to a non-existent static property" in {
      // classes/static_properties_undeclared_assign.phpt
      script(
        """<?php
          |Class C {}
          |C::$p = 1;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access to undeclared static property: C::$p in /classes/StaticPropertiesSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }

    "Assigning & incrementing a non-existent static property" in {
      // classes/static_properties_undeclared_assignInc.phpt
      script(
        """<?php
          |Class C {}
          |C::$p += 1;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access to undeclared static property: C::$p in /classes/StaticPropertiesSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }
    "Assigning a non-existent static property by reference" in {
      // classes/static_properties_undeclared_assignRef.phpt
      script(
        """<?php
          |Class C {}
          |$a = 'foo';
          |C::$p =& $a;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access to undeclared static property: C::$p in /classes/StaticPropertiesSpec.inlinePhp on line 4
          |""".stripMargin
      )
    }

    "Incrementing a non-existent static property" in {
      // classes/static_properties_undeclared_inc.phpt
      script(
        """<?php
          |Class C {}
          |C::$p++;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access to undeclared static property: C::$p in /classes/StaticPropertiesSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }
    "Issetting a non-existent static property" in {
      // classes/static_properties_undeclared_isset.phpt
      script(
        """<?php
          |Class C {}
          |var_dump(isset(C::$p));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """bool(false)
          |""".stripMargin
      )
    }

    "Reading a non-existent static property" in {
      // classes/static_properties_undeclared_read.phpt
      script(
        """<?php
          |Class C {}
          |echo C::$p;
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access to undeclared static property: C::$p in /classes/StaticPropertiesSpec.inlinePhp on line 3
          |""".stripMargin
      )
    }
  }
}
