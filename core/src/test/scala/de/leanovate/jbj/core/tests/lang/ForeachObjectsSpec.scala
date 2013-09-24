/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class ForeachObjectsSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "foreach with objects" should {
    "Foreach loop on objects - basic loop with just value and key => value." in {
      // lang/foreachLoopObjects.001.phpt
      script(
        """<?php
          |
          |class C {
          |	public $a = "Original a";
          |	public $b = "Original b";
          |	public $c = "Original c";
          |	protected $d = "Original d";
          |	private $e = "Original e";
          |
          |}
          |
          |echo "\n\nSimple loop.\n";
          |$obj = new C;
          |foreach ($obj as $v) {
          |	var_dump($v);
          |}
          |foreach ($obj as $k => $v) {
          |	var_dump($k, $v);
          |}
          |echo "\nCheck key and value after the loop.\n";
          |var_dump($k, $v);
          |
          |
          |echo "\n\nObject instantiated inside loop.\n";
          |foreach (new C as $v) {
          |	var_dump($v);
          |}
          |foreach (new C as $k => $v) {
          |	var_dump($k, $v);
          |}
          |echo "\nCheck key and value after the loop.\n";
          |var_dump($k, $v);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |
          |Simple loop.
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(1) "a"
          |string(10) "Original a"
          |string(1) "b"
          |string(10) "Original b"
          |string(1) "c"
          |string(10) "Original c"
          |
          |Check key and value after the loop.
          |string(1) "c"
          |string(10) "Original c"
          |
          |
          |Object instantiated inside loop.
          |string(10) "Original a"
          |string(10) "Original b"
          |string(10) "Original c"
          |string(1) "a"
          |string(10) "Original a"
          |string(1) "b"
          |string(10) "Original b"
          |string(1) "c"
          |string(10) "Original c"
          |
          |Check key and value after the loop.
          |string(1) "c"
          |string(10) "Original c"
          |""".stripMargin
      )
    }
  }
}
