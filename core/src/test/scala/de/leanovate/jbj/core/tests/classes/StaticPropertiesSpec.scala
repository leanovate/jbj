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
  }
}
