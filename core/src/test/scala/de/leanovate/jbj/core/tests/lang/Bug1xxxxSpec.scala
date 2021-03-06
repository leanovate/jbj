/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug1xxxxSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bugs #1xxxx" should {
    "Bug #17115 (lambda functions produce segfault with static vars)" in {
      // lang/bug17115.phpt
      script(
        """<?php
          |$func = create_function('','
          |	static $foo = 0;
          |	return $foo++;
          |');
          |var_dump($func());
          |var_dump($func());
          |var_dump($func());
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(0)
          |int(1)
          |int(2)
          |""".stripMargin
      )
    }

    "Bug #18872 (class constant used as default parameter)" in {
      // lang/bug18872.phpt
      script(
        """<?php
          |class FooBar {
          |	const BIFF = 3;
          |}
          |
          |function foo($biff = FooBar::BIFF) {
          |	echo $biff . "\n";
          |}
          |
          |foo();
          |foo();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """3
          |3
          |""".stripMargin
      )
    }

    "Bug #19566 (get_declared_classes() segfaults)" in {
      // lang/bug19566.phpt
      script(
        """<?php
          |class foo {}
          |$result = get_declared_classes();
          |if(array_search('foo', $result) > 0) {
          |  echo "Success\n";
          |}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Success
          |""".stripMargin
      )
    }

    "Bug #19943 (memleaks)" in {
      // lang/bug19943.phpt
      script(
        """<?php
          |	$ar = array();
          |	for ($count = 0; $count < 10; $count++) {
          |		$ar[$count]        = "$count";
          |		@$ar[$count]['idx'] = "$count";
          |	}
          |
          |	for ($count = 0; $count < 10; $count++) {
          |		echo $ar[$count]." -- ".@$ar[$count]['idx']."\n";
          |	}
          |	$a = "0123456789";
          |	$a[9] = $a[0];
          |	var_dump($a);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """0 -- 0
          |1 -- 1
          |2 -- 2
          |3 -- 3
          |4 -- 4
          |5 -- 5
          |6 -- 6
          |7 -- 7
          |8 -- 8
          |9 -- 9
          |string(10) "0123456780"
          |""".stripMargin
      )
    }
  }
}
