/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class ToStringSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "toString" should {
    "ZE2 __toString()" in {
      // classes/tostring_001.phpt
      script(
        """<?php
          |
          |function my_error_handler($errno, $errstr, $errfile, $errline) {
          |	var_dump($errstr);
          |}
          |
          |set_error_handler('my_error_handler');
          |
          |class test1
          |{
          |}
          |
          |class test2
          |{
          |    function __toString()
          |    {
          |    	echo __METHOD__ . "()\n";
          |        return "Converted\n";
          |    }
          |}
          |
          |class test3
          |{
          |    function __toString()
          |    {
          |    	echo __METHOD__ . "()\n";
          |        return 42;
          |    }
          |}
          |echo "====test1====\n";
          |$o = new test1;
          |print_r($o);
          |var_dump((string)$o);
          |var_dump($o);
          |
          |echo "====test2====\n";
          |$o = new test2;
          |print_r($o);
          |print $o;
          |var_dump($o);
          |echo "====test3====\n";
          |echo $o;
          |
          |echo "====test4====\n";
          |echo "string:".$o;
          |
          |echo "====test5====\n";
          |echo 1 . $o;
          |echo 1 , $o;
          |
          |echo "====test6====\n";
          |echo $o . $o;
          |echo $o , $o;
          |
          |echo "====test7====\n";
          |$ar = array();
          |$ar[$o->__toString()] = "ERROR";
          |echo $ar[$o];
          |
          |echo "====test8====\n";
          |var_dump(trim($o));
          |var_dump(trim((string)$o));
          |
          |echo "====test9====\n";
          |echo sprintf("%s", $o);
          |
          |echo "====test10====\n";
          |$o = new test3;
          |var_dump($o);
          |echo $o;
          |
          |?>
          |====DONE====
          |""".stripMargin
      ).result must haveOutput(
        """====test1====
          |test1 Object
          |(
          |)
          |string(54) "Object of class test1 could not be converted to string"
          |string(0) ""
          |object(test1)#1 (0) {
          |}
          |====test2====
          |test2 Object
          |(
          |)
          |test2::__toString()
          |Converted
          |object(test2)#2 (0) {
          |}
          |====test3====
          |test2::__toString()
          |Converted
          |====test4====
          |test2::__toString()
          |string:Converted
          |====test5====
          |test2::__toString()
          |1Converted
          |1test2::__toString()
          |Converted
          |====test6====
          |test2::__toString()
          |test2::__toString()
          |Converted
          |Converted
          |test2::__toString()
          |Converted
          |test2::__toString()
          |Converted
          |====test7====
          |test2::__toString()
          |string(19) "Illegal offset type"
          |====test8====
          |test2::__toString()
          |string(9) "Converted"
          |test2::__toString()
          |string(9) "Converted"
          |====test9====
          |test2::__toString()
          |Converted
          |====test10====
          |object(test3)#3 (0) {
          |}
          |test3::__toString()
          |string(53) "Method test3::__toString() must return a string value"
          |====DONE====
          |""".stripMargin
      )
    }

    "ZE2 __toString() in __destruct" in {
      // classes/tostring_002.phpt
      script(
        """<?php
          |
          |class Test
          |{
          |	function __toString()
          |	{
          |		return "Hello\n";
          |	}
          |
          |	function __destruct()
          |	{
          |		echo $this;
          |	}
          |}
          |
          |$o = new Test;
          |$o = NULL;
          |
          |$o = new Test;
          |
          |?>
          |====DONE====
          |""".stripMargin
      ).result must haveOutput(
        """Hello
          |====DONE====
          |Hello
          |""".stripMargin
      )
    }

    "ZE2 __toString() in __destruct/exception" in {
      // classes/tostring_003.phpt
      script(
        """<?php
          |
          |class Test
          |{
          |	function __toString()
          |	{
          |		throw new Exception("Damn!");
          |		return "Hello\n";
          |	}
          |
          |	function __destruct()
          |	{
          |		echo $this;
          |	}
          |}
          |
          |try
          |{
          |	$o = new Test;
          |	$o = NULL;
          |}
          |catch(Exception $e)
          |{
          |	var_dump($e->getMessage());
          |}
          |
          |?>
          |====DONE====
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Method Test::__toString() must not throw an exception in /classes/ToStringSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }
  }
}
