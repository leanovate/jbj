/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class ObjectReferenceSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "new" should {
    "Confirm difference between assigning new directly and by reference." in {
      // classes/new_001.phpt
      script(
        """<?php
          |  echo "Compile-time strict error message should precede this.\n";
          |
          |  class Inc
          |  {
          |      private static $counter = 0;
          |      function __construct()
          |      {
          |          $this->id = ++Inc::$counter;
          |      }
          |  }
          |
          |  $f = new Inc();
          |  $k =& $f;
          |  echo "\$f initially points to the first object:\n";
          |  var_dump($f);
          |
          |  echo "Assigning new object directly to \$k affects \$f:\n";
          |  $k = new Inc();
          |  var_dump($f);
          |
          |  echo "Assigning new object by ref to \$k removes it from \$f's reference set, so \$f is unchanged:\n";
          |  $k =& new Inc();
          |  var_dump($f);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Deprecated: Assigning the return value of new by reference is deprecated in /classes/ObjectReferenceSpec.inlinePhp on line 23
          |Compile-time strict error message should precede this.
          |$f initially points to the first object:
          |object(Inc)#1 (1) {
          |  ["id"]=>
          |  int(1)
          |}
          |Assigning new object directly to $k affects $f:
          |object(Inc)#2 (1) {
          |  ["id"]=>
          |  int(2)
          |}
          |Assigning new object by ref to $k removes it from $f's reference set, so $f is unchanged:
          |object(Inc)#2 (1) {
          |  ["id"]=>
          |  int(2)
          |}
          |""".stripMargin
      )
    }
  }

  "Object reference" should {
    "ZE2 object references" in {
      // classes/object_reference_001
      script(
        """<?php
          |
          |class Foo {
          |	public $name;
          |
          |	function Foo() {
          |		$this->name = "I'm Foo!\n";
          |	}
          |}
          |
          |$foo = new Foo;
          |echo $foo->name;
          |$bar = $foo;
          |$bar->name = "I'm Bar!\n";
          |
          |// In ZE1, we would expect "I'm Foo!"
          |echo $foo->name;
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """I'm Foo!
          |I'm Bar!
          |""".stripMargin
      )
    }
  }

}
