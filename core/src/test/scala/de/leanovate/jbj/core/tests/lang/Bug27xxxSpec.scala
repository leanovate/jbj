/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Bug27xxxSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Bug #27xxx and #28xxxx and #29xxxx" should {
    "Bug #27354 (Modulus operator crashes PHP)" in {
      // lang/bug27354.phpt
      script(
        """<?php
          |	var_dump(-2147483647 % -1);
          |	var_dump(-2147483649 % -1);
          |	var_dump(-2147483648 % -1);
          |	var_dump(-2147483648 % -2);
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """int(0)
          |int(0)
          |int(0)
          |int(0)
          |""".stripMargin
      )
    }

    "Bug #27443 (defined() returns wrong type)" in {
      // lang/bug27443.phpt
      script(
        """<?php
          |echo gettype(defined('test'));
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """boolean""".stripMargin
      )
    }

    "Bug #27535 (Objects pointing to each other cause Apache to crash)" in {
      // lang/bug27535.phpt
      script(
        """<?php
          |
          |class Class1
          |{
          |	public $_Class2_obj;
          |}
          |
          |class Class2
          |{
          |	public $storage = '';
          |
          |	function Class2()
          |	{
          |		$this->storage = new Class1();
          |
          |		$this->storage->_Class2_obj = $this;
          |	}
          |}
          |
          |$foo = new Class2();
          |
          |?>
          |Alive!
          |""".stripMargin
      ).result must haveOutput(
        """Alive!
          |""".stripMargin
      )
    }

    "Bug #28800 (Incorrect string to number conversion for strings starting with 'inf')" in {
      // lang/bug28800.phpt
      script(
        """<?php
          |	$strings = array('into', 'info', 'inf', 'infinity', 'infin', 'inflammable');
          |	foreach ($strings as $v) {
          |		echo ($v+0)."\n";
          |	}
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """0
          |0
          |0
          |0
          |0
          |0
          |""".stripMargin
      )
    }

    "Bug #29566 (foreach/string handling strangeness)" in {
      // lang/bug29566.phpt
      script(
        """<?php
          |$var="This is a string";
          |
          |$dummy="";
          |unset($dummy);
          |
          |foreach($var['nosuchkey'] as $v) {
          |}
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: Illegal string offset 'nosuchkey' in /lang/Bug27xxxSpec.inlinePhp on line 7
          |
          |Warning: Invalid argument supplied for foreach() in /lang/Bug27xxxSpec.inlinePhp on line 7
          |===DONE===
          |""".stripMargin
      )
    }

    "Bug #29893 (segfault when using array as index)" in {
      // lang/bug29893.phpt
      script(
        """<?php
          |$base = 50;
          |$base[$base] -= 0;
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Warning: Cannot use a scalar value as an array in /lang/Bug27xxxSpec.inlinePhp on line 3
          |===DONE===
          |""".stripMargin
      )
    }
  }
}
