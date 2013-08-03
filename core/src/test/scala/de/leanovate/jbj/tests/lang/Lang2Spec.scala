package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class Lang2Spec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Language test 2" - {
    "Testing function parameter passing with a return value" in {
      // lang/010
      script(
        """<?php
          |function test ($b) {
          |	$b++;
          |	return($b);
          |}
          |$a = test(1);
          |echo $a;
          |?>""".stripMargin
      ).result must haveOutput(
        """2"""
      )
    }

    "Testing nested functions" in {
      // lang/011
      script(
        """<?php
          |function F()
          |{
          |	$a = "Hello ";
          |	return($a);
          |}
          |
          |function G()
          |{
          |  static $myvar = 4;
          |
          |  echo "$myvar ";
          |  echo F();
          |  echo "$myvar";
          |}
          |
          |G();
          |?>""".stripMargin
      ).result must haveOutput(
        """4 Hello 4"""
      )
    }

    "Testing stack after early function return" in {
      // lang/012
      script(
        """<?php
          |function F () {
          |	if(1) {
          |		return("Hello");
          |	}
          |}
          |
          |$i=0;
          |while ($i<2) {
          |	echo F();
          |	$i++;
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """HelloHello"""
      )
    }

    "Testing user-defined function falling out of an If into another" in {
      // lang/017
      script(
        """<?php
          |$a = 1;
          |function Test ($a) {
          |	if ($a<3) {
          |		return(3);
          |	}
          |}
          |
          |if ($a < Test($a)) {
          |	echo "$a\n";
          |	$a++;
          |}
          |?>""".stripMargin
      ).result must haveOutput(
        """1
          |""".stripMargin
      )
    }
  }

}
