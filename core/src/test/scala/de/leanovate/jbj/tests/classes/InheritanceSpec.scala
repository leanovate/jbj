package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class InheritanceSpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Inheritance" - {
    "Classes inheritance test" in {
      // class/inheritance
      script(
        """<?php
          |
          |/* Inheritance test.  Pretty nifty if I do say so myself! */
          |
          |class foo {
          |  public $a;
          |  public $b;
          |  function display() {
          |  	echo "This is class foo\n";
          |    echo "a = ".$this->a."\n";
          |    echo "b = ".$this->b."\n";
          |  }
          |  function mul() {
          |    return $this->a*$this->b;
          |  }
          |};
          |
          |class bar extends foo {
          |  public $c;
          |  function display() {  /* alternative display function for class bar */
          |    echo "This is class bar\n";
          |    echo "a = ".$this->a."\n";
          |    echo "b = ".$this->b."\n";
          |    echo "c = ".$this->c."\n";
          |  }
          |};
          |
          |
          |$foo1 = new foo;
          |$foo1->a = 2;
          |$foo1->b = 5;
          |$foo1->display();
          |echo $foo1->mul()."\n";
          |
          |echo "-----\n";
          |
          |$bar1 = new bar;
          |$bar1->a = 4;
          |$bar1->b = 3;
          |$bar1->c = 12;
          |$bar1->display();
          |echo $bar1->mul()."\n";""".stripMargin
      ) must haveOutput(
        """This is class foo
          |a = 2
          |b = 5
          |10
          |-----
          |This is class bar
          |a = 4
          |b = 3
          |c = 12
          |12
          |""".stripMargin
      )
    }
  }
}
