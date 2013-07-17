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
  }
}