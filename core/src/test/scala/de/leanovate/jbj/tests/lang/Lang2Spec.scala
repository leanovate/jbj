package de.leanovate.jbj.tests.lang

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class Lang2Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Language test 2" should {
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

    "Testing eval function" in {
      // lang/013
      script(
        """<?php
          |error_reporting(0);
          |$a="echo \"Hello\";";
          |eval($a);
          |?>""".stripMargin
      ).result must haveOutput(
        """Hello""".stripMargin
      )
    }

    "Testing eval function inside user-defined function" in {
      // lang/014
      script(
        """<?php
          |function F ($a) {
          |	eval($a);
          |}
          |
          |error_reporting(0);
          |F("echo \"Hello\";");
          |?>""".stripMargin
      ).result must haveOutput(
        """Hello""".stripMargin
      )
    }

    "Testing include" in {
      // lang/015
      script(
        """<?php
          |include "015.inc";
          |?>""".stripMargin
      ).result must haveOutput(
        """Hello""".stripMargin
      )
    }

    "Testing user-defined function in included file" in {
      // lang/016
      script(
        """<?php
          |include "016.inc";
          |MyFunc("Hello");
          |?>""".stripMargin
      ).result must haveOutput(
        """Hello""".stripMargin
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

  "eval() test" in {
    // lang/018
    script(
      """<?php
        |
        |error_reporting(0);
        |
        |$message = "echo \"hey\n\";";
        |
        |for ($i=0; $i<10; $i++) {
        |  eval($message);
        |  echo $i."\n";
        |}""".stripMargin
    ).result must haveOutput(
      """hey
        |0
        |hey
        |1
        |hey
        |2
        |hey
        |3
        |hey
        |4
        |hey
        |5
        |hey
        |6
        |hey
        |7
        |hey
        |8
        |hey
        |9
        |""".stripMargin
    )
  }

  "eval() test 2" in {
    // lang/019
    script(
      """<?php
        |
        |eval("function test() { echo \"hey, this is a function inside an eval()!\\n\"; }");
        |
        |$i=0;
        |while ($i<10) {
        |  eval("echo \"hey, this is a regular echo'd eval()\\n\";");
        |  test();
        |  $i++;
        |}
        |
        |eval('-');""".stripMargin
    ).result must haveOutput(
      """hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |hey, this is a regular echo'd eval()
        |hey, this is a function inside an eval()!
        |
        |Parse error: syntax error, unexpected end of input in /lang/Lang2Spec.inlinePhp(12) : eval()'d code on line 1
        |""".stripMargin
    )
  }
}
