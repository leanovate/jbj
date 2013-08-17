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

    "Returning a reference from a function 3" in {
      // lang/returnByReference003
      script(
        """<?php
          |function returnConstantByValue() {
          |	return 100;
          |}
          |
          |function &returnConstantByRef() {
          |	return 100;
          |}
          |
          |function &returnVariableByRef() {
          |	return $GLOBALS['a'];
          |}
          |
          |echo "\n---> 1. Trying to assign by reference the return value of a function that returns by value:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &returnConstantByValue();
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 2. Trying to assign by reference the return value of a function that returns a constant by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &returnConstantByRef();
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 3. Trying to assign by reference the return value of a function that returns by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &returnVariableByRef();
          |$a++;
          |var_dump($a, $b);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |---> 1. Trying to assign by reference the return value of a function that returns by value:
          |
          |Strict Standards: Only variables should be assigned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 17
          |int(5)
          |int(100)
          |
          |---> 2. Trying to assign by reference the return value of a function that returns a constant by ref:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 7
          |int(5)
          |int(100)
          |
          |---> 3. Trying to assign by reference the return value of a function that returns by ref:
          |int(5)
          |int(5)
          |""".stripMargin
      )
    }

    "Returning a reference from a static method" in {
      // lang/returnByReference004
      script(
        """<?php
          |Class C {
          |	static function returnConstantByValue() {
          |		return 100;
          |	}
          |
          |	static function &returnConstantByRef() {
          |		return 100;
          |	}
          |
          |	static function &returnVariableByRef() {
          |		return $GLOBALS['a'];
          |	}
          |}
          |
          |echo "\n---> 1. Trying to assign by reference the return value of a function that returns by value:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &C::returnConstantByValue();
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 2. Trying to assign by reference the return value of a function that returns a constant by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &C::returnConstantByRef();
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 3. Trying to assign by reference the return value of a function that returns by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &C::returnVariableByRef();
          |$a++;
          |var_dump($a, $b);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |---> 1. Trying to assign by reference the return value of a function that returns by value:
          |
          |Strict Standards: Only variables should be assigned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 19
          |int(5)
          |int(100)
          |
          |---> 2. Trying to assign by reference the return value of a function that returns a constant by ref:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 8
          |int(5)
          |int(100)
          |
          |---> 3. Trying to assign by reference the return value of a function that returns by ref:
          |int(5)
          |int(5)
          |""".stripMargin
      )
    }

    "Returning a reference from a method" in {
      // lang/returnByReference005
      script(
        """<?php
          |Class C {
          |	function returnConstantByValue() {
          |		return 100;
          |	}
          |
          |	function &returnConstantByRef() {
          |		return 100;
          |	}
          |
          |	static function &returnVariableByRef() {
          |		return $GLOBALS['a'];
          |	}
          |}
          |$c = new C;
          |
          |echo "\n---> 1. Trying to assign by reference the return value of a function that returns by value:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &$c->returnConstantByValue();
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 2. Trying to assign by reference the return value of a function that returns a constant by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &$c->returnConstantByRef();
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 3. Trying to assign by reference the return value of a function that returns by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &$c->returnVariableByRef();
          |$a++;
          |var_dump($a, $b);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |---> 1. Trying to assign by reference the return value of a function that returns by value:
          |
          |Strict Standards: Only variables should be assigned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 20
          |int(5)
          |int(100)
          |
          |---> 2. Trying to assign by reference the return value of a function that returns a constant by ref:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 8
          |int(5)
          |int(100)
          |
          |---> 3. Trying to assign by reference the return value of a function that returns by ref:
          |int(5)
          |int(5)
          |""".stripMargin
      )
    }

    "Returning a reference from a function via another function" in {
      // lang/returnByReference006
      script(
        """<?php
          |function returnConstantByValue() {
          |	return 100;
          |}
          |
          |function &returnConstantByRef() {
          |	return 100;
          |}
          |
          |function &returnVariableByRef() {
          |	return $GLOBALS['a'];
          |}
          |
          |function &returnFunctionCallByRef($functionToCall) {
          |	return $functionToCall();
          |}
          |
          |echo "\n---> 1. Via a return by ref function call, assign by reference the return value of a function that returns by value:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &returnFunctionCallByRef('returnConstantByValue');
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 2. Via a return by ref function call, assign by reference the return value of a function that returns a constant by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &returnFunctionCallByRef('returnConstantByRef');
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 3. Via a return by ref function call, assign by reference the return value of a function that returns by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &returnFunctionCallByRef('returnVariableByRef');
          |$a++;
          |var_dump($a, $b);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |---> 1. Via a return by ref function call, assign by reference the return value of a function that returns by value:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 15
          |int(5)
          |int(100)
          |
          |---> 2. Via a return by ref function call, assign by reference the return value of a function that returns a constant by ref:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 7
          |int(5)
          |int(100)
          |
          |---> 3. Via a return by ref function call, assign by reference the return value of a function that returns by ref:
          |int(5)
          |int(5)
          |""".stripMargin
      )
    }

    "Returning a reference from a static method via another static method" in {
      // lang/returnByReference007
      script(
        """<?php
          |class C {
          |	static function returnConstantByValue() {
          |		return 100;
          |	}
          |
          |	static function &returnConstantByRef() {
          |		return 100;
          |	}
          |
          |	static function &returnVariableByRef() {
          |		return $GLOBALS['a'];
          |	}
          |
          |	static function &returnFunctionCallByRef($functionToCall) {
          |		return C::$functionToCall();
          |	}
          |}
          |
          |echo "\n---> 1. Via a return by ref function call, assign by reference the return value of a function that returns by value:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &C::returnFunctionCallByRef('returnConstantByValue');
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 2. Via a return by ref function call, assign by reference the return value of a function that returns a constant by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &C::returnFunctionCallByRef('returnConstantByRef');
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 3. Via a return by ref function call, assign by reference the return value of a function that returns by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &C::returnFunctionCallByRef('returnVariableByRef');
          |$a++;
          |var_dump($a, $b);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |---> 1. Via a return by ref function call, assign by reference the return value of a function that returns by value:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 16
          |int(5)
          |int(100)
          |
          |---> 2. Via a return by ref function call, assign by reference the return value of a function that returns a constant by ref:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 8
          |int(5)
          |int(100)
          |
          |---> 3. Via a return by ref function call, assign by reference the return value of a function that returns by ref:
          |int(5)
          |int(5)
          |""".stripMargin
      )
    }

    "Returning a reference from a non-static method via another non-static method" in {
      // lang/returnByReference008
      script(
        """<?php
          |class C {
          |	function returnConstantByValue() {
          |		return 100;
          |	}
          |
          |	function &returnConstantByRef() {
          |		return 100;
          |	}
          |
          |	function &returnVariableByRef() {
          |		return $GLOBALS['a'];
          |	}
          |
          |	function &returnFunctionCallByRef($functionToCall) {
          |		return $this->$functionToCall();
          |	}
          |}
          |$c = new C;
          |
          |echo "\n---> 1. Via a return by ref function call, assign by reference the return value of a function that returns by value:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &$c->returnFunctionCallByRef('returnConstantByValue');
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 2. Via a return by ref function call, assign by reference the return value of a function that returns a constant by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &$c->returnFunctionCallByRef('returnConstantByRef');
          |$a++;
          |var_dump($a, $b);
          |
          |echo "\n---> 3. Via a return by ref function call, assign by reference the return value of a function that returns by ref:\n";
          |unset($a, $b);
          |$a = 4;
          |$b = &$c->returnFunctionCallByRef('returnVariableByRef');
          |$a++;
          |var_dump($a, $b);
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """
          |---> 1. Via a return by ref function call, assign by reference the return value of a function that returns by value:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 16
          |int(5)
          |int(100)
          |
          |---> 2. Via a return by ref function call, assign by reference the return value of a function that returns a constant by ref:
          |
          |Notice: Only variable references should be returned by reference in /lang/ReturnByReferenceSpec.inlinePhp on line 8
          |int(5)
          |int(100)
          |
          |---> 3. Via a return by ref function call, assign by reference the return value of a function that returns by ref:
          |int(5)
          |int(5)
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
