package de.leanovate.jbj.tests.lang

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.tests.TestJbjExecutor

class ReturnByReferenceSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Return by reference" should {
    "Returning a reference from a function 1" in {
      // lang/returnByReference001
      script(
        """<?php
          |
          |function &returnByRef(&$arg1)
          |{
          |	return $arg1;
          |}
          |
          |$a = 7;
          |$b =& returnByRef($a);
          |var_dump($b);
          |$a++;
          |var_dump($b);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """int(7)
          |int(8)
          |""".stripMargin
      )
    }

    "Returning a reference from a function 2" in {
      // lang/returnByReference002
      script(
        """<?php
          |function &returnRef() {
          |		global $a;
          |		return $a;
          |}
          |
          |function returnVal() {
          |		global $a;
          |		return $a;
          |}
          |
          |$a = "original";
          |$b =& returnVal();
          |$b = "changed";
          |var_dump($a); //expecting warning + "original"
          |
          |$a = "original";
          |$b =& returnRef();
          |$b = "changed";
          |var_dump($a); //expecting "changed"
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |Strict Standards: Only variables should be assigned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 13
          |string(8) "original"
          |string(7) "changed"
          |""".stripMargin
      )
    }

    "Returning a references returned by another function" in {
      // lang/returnByReference009
      script(
        """<?php
          |
          |
          |function &returnVarByRef () {
          |    $b=1;
          |	return $b;
          |}
          |
          |function &testReturnVarByRef() {
          |	return returnVarByRef();
          |}
          |
          |function returnVal () {
          |return 1;
          |}
          |
          |function &testReturnValByRef() {
          |	return returnVal();
          |}
          |
          |echo "\n---> 1. Return a variable by reference -> No warning:\n";
          |
          |var_dump (testReturnVarByRef());
          |
          |echo "\n---> 2. Return a value by reference -> Warning:\n";
          |
          |var_dump (testReturnValByRef());
          |""".stripMargin
      ).result must haveOutput(
        """
          |---> 1. Return a variable by reference -> No warning:
          |int(1)
          |
          |---> 2. Return a value by reference -> Warning:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 18
          |int(1)
          |""".stripMargin
      )
    }
  }
}
