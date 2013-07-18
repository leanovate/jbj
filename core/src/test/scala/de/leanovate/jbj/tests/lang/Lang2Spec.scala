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
      resultOf(
        """<?php
          |function test ($b) {
          |	$b++;
          |	return($b);
          |}
          |$a = test(1);
          |echo $a;
          |?>""".stripMargin
      ) must be(
        """2"""
      )
    }

    "Testing nested functions" in {
      // lang/011
      resultOf(
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
      ) must be(
        """4 Hello 4"""
      )
    }

    "Testing stack after early function return" in {
      // lang/012
      resultOf(
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
      ) must be(
        """HelloHello"""
      )
    }

    "Testing user-defined function falling out of an If into another" in {
      // lang/017
      resultOf(
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
      ) must be(
        """1
          |""".stripMargin
      )
    }
  }

}
