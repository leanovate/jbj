/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug3xxxxSpec extends SpecificationWithJUnit with TestJbjExecutor  {
  "Bug #3xxxx" should {
    "Bug #30578 (Output buffers flushed before calling __desctruct functions)" in {
      // lang/bug30578.phpt
      script(
        """<?php
          |
          |error_reporting(E_ALL);
          |
          |class Example
          |{
          |    function __construct()
          |    {
          |        ob_start();
          |        echo "This should be displayed last.\n";
          |    }
          |
          |    function __destruct()
          |    {
          |        $buffered_data = ob_get_contents();
          |        ob_end_clean();
          |
          |        echo "This should be displayed first.\n";
          |        echo "Buffered data: $buffered_data";
          |    }
          |}
          |
          |$obj = new Example;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """This should be displayed first.
          |Buffered data: This should be displayed last.
          |""".stripMargin
      )
    }

    "Bug #30726 (-.1 like numbers are not being handled correctly)" in {
      // lang/bug30726.phpt
      script(
        """<?php
          |echo (int) is_float('-.1' * 2), "\n";
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """1
          |""".stripMargin
      )
    }

    "Bug #30862 (Static array with boolean indexes)" in {
      // lang/bug30862.phpt
      script(
        """<?php
          |class T {
          |	static $a = array(false=>"false", true=>"true");
          |}
          |print_r(T::$a);
          |?>
          |----------
          |<?php
          |define("X",0);
          |define("Y",1);
          |class T2 {
          |	static $a = array(X=>"false", Y=>"true");
          |}
          |print_r(T2::$a);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """Array
          |(
          |    [0] => false
          |    [1] => true
          |)
          |----------
          |Array
          |(
          |    [0] => false
          |    [1] => true
          |)
          |""".stripMargin
      )
    }

    "Bug #32924 (prepend does not add file to included files)" in {
      // lang/bug32924.phpt
      script(
        """<?php
          |include_once(dirname(__FILE__).'/inc.inc');
          |require_once(dirname(__FILE__).'/inc.inc');
          |?>
          |END
          |""".stripMargin
      ).result must haveOutput(
        """Included!
          |END
          |""".stripMargin
      )
    }

    "Bug #35382 (Comment in end of file produces fatal error)" in {
      // lang/bug35382.phpt
      script(
        """<?php
          |eval("echo 'Hello'; // comment");
          |echo " World";
          |//last line comment
          |""".stripMargin
      ).result must haveOutput(
        """Hello World""".stripMargin
      )
    }

    "Bug #38579 (include_once() may include the same file twice)" in {
      // lang/bug38579.phpt
      script(
        """<?php
          |$file = dirname(__FILE__)."/bug38579.inc";
          |include_once(strtolower($file));
          |include_once(strtoupper($file));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |""".stripMargin
      )
    }
  }
}
