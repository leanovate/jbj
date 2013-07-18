package de.leanovate.jbj.tests.lang

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class LangSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Language test" - {
    "Simple If condition test" in {      // lang/001
      resultOf(
        """<?php $a=1; if($a>0) { echo "Yes"; } ?>"""
      ) must be(
        """Yes"""
      )
    }

    "Simple While Loop Test" in { // lang/002
      resultOf(
        """<?php
          |$a=1;
          |while ($a<10) {
          |	echo $a;
          |	$a++;
          |}
          |?>""".stripMargin
      ) must be(
        """123456789"""
      )
    }
    "Simple Switch Test" in {      // lang/003
      resultOf(
        """<?php
          |$a=1;
          |switch($a) {
          |	case 0:
          |		echo "bad";
          |		break;
          |	case 1:
          |		echo "good";
          |		break;
          |	default:
          |		echo "bad";
          |		break;
          |}
          |?>""".stripMargin
      ) must be(
        """good"""
      )
    }

    "Simple If/Else Test" in {      // lang/004
      resultOf(
        """<?php
          |$a=1;
          |if($a==0) {
          |	echo "bad";
          |} else {
          |	echo "good";
          |}
          |?>""".stripMargin
      ) must be(
        """good"""
      )
    }

    "Simple If/ElseIf/Else Test" in {      // lang/005
      resultOf(
        """<?php
          |$a=1;
          |
          |if($a==0) {
          |	echo "bad";
          |} elseif($a==3) {
          |	echo "bad";
          |} else {
          |	echo "good";
          |}
          |?>""".stripMargin
      ) must be(
        """good"""
      )
    }

    "Nested If/ElseIf/Else Test" in {      // lang/006
      resultOf(
        """<?php
          |$a=1;
          |$b=2;
          |
          |if($a==0) {
          |	echo "bad";
          |} elseif($a==3) {
          |	echo "bad";
          |} else {
          |	if($b==1) {
          |		echo "bad";
          |	} elseif($b==2) {
          |		echo "good";
          |	} else {
          |		echo "bad";
          |	}
          |}
          |?>""".stripMargin
      ) must be(
        """good"""
      )
    }

    "Function call with global and static variables" in { // lang/007
      resultOf(
        """<?php
          |error_reporting(0);
          |$a = 10;
          |
          |function Test()
          |{
          |	static $a=1;
          |	global $b;
          |	$c = 1;
          |	$b = 5;
          |	echo "$a $b ";
          |	$a++;
          |	$c++;
          |	echo "$a $c ";
          |}
          |
          |Test();
          |echo "$a $b $c ";
          |Test();
          |echo "$a $b $c ";
          |Test();
          |?>""".stripMargin
      ) must be(
        """1 5 2 2 10 5  2 5 3 2 10 5  3 5 4 2 """
      )
    }

    "Testing function parameter passing" in { // lang/009
      resultOf(
        """<?php
          |function test ($a,$b) {
          |	echo $a+$b;
          |}
          |test(1,2);
          |?>""".stripMargin
      ) must be(
        """3"""
      )
    }

    "Testing function parameter passing with a return value" in { // lang/010
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

    "Testing nested functions" in { // lang/011
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

    "Testing stack after early function return" in { // lang/012
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

    "Testing user-defined function falling out of an If into another" in { // lang/017
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