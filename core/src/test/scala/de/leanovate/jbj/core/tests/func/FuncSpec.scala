/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.func

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class FuncSpec extends SpecificationWithJUnit with TestJbjExecutor{
  "Function test" should {
    "Strlen() function test" in {
      // func/001
      script(
        """<?php echo strlen("abcdef")?>"""
      ).result must haveOutput(
        """6"""
      )
    }

    "Static variables in functions" in {
      // func/002
      script(
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
      ).result must haveOutput(
        """hey=0, 0
          |hey=1, -1
          |hey=2, -2
          |""".stripMargin
      )
    }

    "General function test" in {
      // func/003
      script(
        """<?php
          |
          |function a()
          |{
          |  echo "hey\n";
          |}
          |
          |function b($i)
          |{
          |  echo "$i\n";
          |}
          |
          |
          |function c($i,$j)
          |{
          |  echo "Counting from $i to $j\n";
          |  for ($k=$i; $k<=$j; $k++) {
          |    echo "$k\n";
          |  }
          |}
          |
          |
          |
          |a();
          |b("blah");
          |a();
          |b("blah","blah");
          |c(7,14);
          |
          |a();
          |
          |
          |function factorial($n)
          |{
          |  if ($n==0 || $n==1) {
          |    return 1;
          |  } else {
          |    return factorial($n-1)*$n;
          |  }
          |}
          |
          |
          |function factorial2($start, $n)
          |{
          |  if ($n<=$start) {
          |    return $start;
          |  } else {
          |    return factorial2($start,$n-1)*$n;
          |  }
          |}
          |
          |
          |for ($k=0; $k<10; $k++) {
          |  for ($i=0; $i<=10; $i++) {
          |    $n=factorial($i);
          |    echo "factorial($i) = $n\n";
          |  }
          |}
          |
          |
          |echo "and now, from a function...\n";
          |
          |function call_fact()
          |{
          |  echo "(it should break at 5...)\n";
          |  for ($i=0; $i<=10; $i++) {
          |    if ($i == 5) break;
          |    $n=factorial($i);
          |    echo "factorial($i) = $n\n";
          |  }
          |}
          |
          |function return4() { return 4; }
          |function return7() { return 7; }
          |
          |for ($k=0; $k<10; $k++) {
          |  call_fact();
          |}
          |
          |echo "------\n";
          |$result = factorial(factorial(3));
          |echo "$result\n";
          |
          |$result=factorial2(return4(),return7());
          |echo "$result\n";
          |
          |function andi($i, $j)
          |{
          |	for ($k=$i ; $k<=$j ; $k++) {
          |		if ($k >5) continue;
          |		echo "$k\n";
          |	}
          |}
          |
          |andi (3,10);""".stripMargin
      ).result must haveOutput(
        """hey
          |blah
          |hey
          |blah
          |Counting from 7 to 14
          |7
          |8
          |9
          |10
          |11
          |12
          |13
          |14
          |hey
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |factorial(5) = 120
          |factorial(6) = 720
          |factorial(7) = 5040
          |factorial(8) = 40320
          |factorial(9) = 362880
          |factorial(10) = 3628800
          |and now, from a function...
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |(it should break at 5...)
          |factorial(0) = 1
          |factorial(1) = 1
          |factorial(2) = 2
          |factorial(3) = 6
          |factorial(4) = 24
          |------
          |720
          |840
          |3
          |4
          |5
          |""".stripMargin
      )
    }

    "General function test 2" in {
      // func/004
      script(
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
      ).result must haveOutput(
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

    "Testing register_shutdown_function()" in {
      // func/005.phpt
      script(
        """<?php
          |
          |function foo()
          |{
          |	print "foo";
          |}
          |
          |register_shutdown_function("foo");
          |
          |print "foo() will be called on shutdown...\n";
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """foo() will be called on shutdown...
          |foo""".stripMargin
      )
    }

    "Output buffering tests" in {
      // func/006.phpt
      script(
        """<?php
          |ob_start();
          |echo ob_get_level();
          |echo 'A';
          |  ob_start();
          |  echo ob_get_level();
          |  echo 'B';
          |  $b = ob_get_contents();
          |  ob_end_clean();
          |$a = ob_get_contents();
          |ob_end_clean();
          |
          |var_dump( $b ); // 2B
          |var_dump( $a ); // 1A
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(2) "2B"
          |string(2) "1A"
          |""".stripMargin
      )
    }
  }
}
