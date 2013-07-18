package de.leanovate.jbj.tests.func

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class FuncSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Function test" - {
    "Strlen() function test" in {
      // func/001
      resultOf(
        """<?php echo strlen("abcdef")?>"""
      ) must be(
        """6"""
      )
    }

    "Static variables in functions" in {
      // func/002
      resultOf(
        """<?php
          |function blah()
          |{
          |  static $hey=0,$yo=0;
          |
          |  echo "hey=".$hey++.", ",$yo--."\n";
          |}
          |
          |blah();
          |blah();
          |blah();
          |if (isset($hey) || isset($yo)) {
          |  echo "Local variables became global :(\n";
          |}""".stripMargin
      ) must be(
        """hey=0, 0
          |hey=1, -1
          |hey=2, -2
          |""".stripMargin
      )
    }

    "General function test" in {
      // func/004
      resultOf(
        """<?php
          |
          |echo "Before function declaration...\n";
          |
          |function print_something_multiple_times($something,$times)
          |{
          |  echo "----\nIn function, printing the string \"$something\" $times times\n";
          |  for ($i=0; $i<$times; $i++) {
          |    echo "$i) $something\n";
          |  }
          |  echo "Done with function...\n-----\n";
          |}
          |
          |function some_other_function()
          |{
          |  echo "This is some other function, to ensure more than just one function works fine...\n";
          |}
          |
          |
          |echo "After function declaration...\n";
          |
          |echo "Calling function for the first time...\n";
          |print_something_multiple_times("This works!",10);
          |echo "Returned from function call...\n";
          |
          |echo "Calling the function for the second time...\n";
          |print_something_multiple_times("This like, really works and stuff...",3);
          |echo "Returned from function call...\n";
          |
          |some_other_function();
          |
          |?>""".stripMargin
      ) must be(
        """Before function declaration...
          |After function declaration...
          |Calling function for the first time...
          |----
          |In function, printing the string "This works!" 10 times
          |0) This works!
          |1) This works!
          |2) This works!
          |3) This works!
          |4) This works!
          |5) This works!
          |6) This works!
          |7) This works!
          |8) This works!
          |9) This works!
          |Done with function...
          |-----
          |Returned from function call...
          |Calling the function for the second time...
          |----
          |In function, printing the string "This like, really works and stuff..." 3 times
          |0) This like, really works and stuff...
          |1) This like, really works and stuff...
          |2) This like, really works and stuff...
          |Done with function...
          |-----
          |Returned from function call...
          |This is some other function, to ensure more than just one function works fine...
          |""".stripMargin
      )
    }
  }
}
