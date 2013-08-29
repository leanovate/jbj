/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import de.leanovate.jbj.core.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class InheritanceSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Inheritance" should {
    "Classes inheritance test" in {
      // classes/inheritance
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
      ).result must haveOutput(
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

    "ZE2 Constructor precedence" in {
      // classes/inheritance_002.phpt
      script(
        """<?php
          |class Base_php4 {
          |  function Base_php4() {
          |    var_dump('Base constructor');
          |  }
          |}
          |
          |class Child_php4 extends Base_php4 {
          |  function Child_php4() {
          |    var_dump('Child constructor');
          |    parent::Base_php4();
          |  }
          |}
          |
          |class Base_php5 {
          |  function __construct() {
          |    var_dump('Base constructor');
          |  }
          |  }
          |
          |class Child_php5 extends Base_php5 {
          |  function __construct() {
          |    var_dump('Child constructor');
          |    parent::__construct();
          |  }
          |  }
          |
          |class Child_mx1 extends Base_php4 {
          |  function __construct() {
          |    var_dump('Child constructor');
          |    parent::Base_php4();
          |  }
          |}
          |
          |class Child_mx2 extends Base_php5 {
          |  function Child_mx2() {
          |    var_dump('Child constructor');
          |    parent::__construct();
          |  }
          |}
          |
          |echo "### PHP 4 style\n";
          |$c4= new Child_php4();
          |
          |echo "### PHP 5 style\n";
          |$c5= new Child_php5();
          |
          |echo "### Mixed style 1\n";
          |$cm= new Child_mx1();
          |
          |echo "### Mixed style 2\n";
          |$cm= new Child_mx2();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """### PHP 4 style
          |string(17) "Child constructor"
          |string(16) "Base constructor"
          |### PHP 5 style
          |string(17) "Child constructor"
          |string(16) "Base constructor"
          |### Mixed style 1
          |string(17) "Child constructor"
          |string(16) "Base constructor"
          |### Mixed style 2
          |string(17) "Child constructor"
          |string(16) "Base constructor"
          |""".stripMargin
      )
    }
  }
}
