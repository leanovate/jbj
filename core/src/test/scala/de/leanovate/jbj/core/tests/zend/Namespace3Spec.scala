/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace3Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace test 020-029" should {
    "020: Accesing internal namespace function" in {
      // Zend/tests/ns_020.phpt
      script(
        """<?php
          |namespace X;
          |use X as Y;
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |foo();
          |\X\foo();
          |Y\foo();
          |\X\foo();
          |""".stripMargin
      ).result must haveOutput(
        """X\foo
          |X\foo
          |X\foo
          |X\foo
          |""".stripMargin
      )
    }

    "021: Name search priority (first look into namespace)" in {
      // Zend/tests/ns_021.phpt
      script(
        """<?php
          |namespace test;
          |
          |class Test {
          |	static function foo() {
          |		echo __CLASS__,"::",__FUNCTION__,"\n";
          |	}
          |}
          |
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |
          |foo();
          |\test\foo();
          |\test\test::foo();
          |""".stripMargin
      ).result must haveOutput(
        """test\foo
          |test\foo
          |test\Test::foo
          |""".stripMargin
      )
    }

    "022: Name search priority (first look into import, then into current namespace and then for class)" in {
      // Zend/tests/ns_022.phpt
      script(
        """<?php
          |namespace a\b\c;
          |
          |use a\b\c as test;
          |
          |require "ns_022.inc";
          |
          |function foo() {
          |	echo __FUNCTION__,"\n";
          |}
          |
          |test\foo();
          |\test::foo();
          |""".stripMargin
      ).result must haveOutput(
        """a\b\c\foo
          |Test::foo
          |""".stripMargin
      )
    }

    "023: __NAMESPACE__ constant" in {
      // Zend/tests/ns_023.phpt
      script(
        """<?php
          |namespace test\foo;
          |
          |var_dump(__NAMESPACE__);
          |""".stripMargin
      ).result must haveOutput(
        """string(8) "test\foo"
          |""".stripMargin
      )
    }

    "024: __NAMESPACE__ constant out of namespace" in {
      // Zend/tests/ns_024.phpt
      script(
        """<?php
          |var_dump(__NAMESPACE__);
          |""".stripMargin
      ).result must haveOutput(
        """string(0) ""
          |""".stripMargin
      )
    }

    "025: Name ambiguity (class name & part of namespace name)" in {
      // Zend/tests/ns_025.phpt
      script(
        """<?php
          |namespace Foo\Bar;
          |
          |class Foo {
          |  function __construct() {
          |  	echo __CLASS__,"\n";
          |  }
          |  static function Bar() {
          |  	echo __CLASS__,"\n";
          |  }
          |}
          |
          |$x = new Foo;
          |Foo::Bar();
          |$x = new \Foo\Bar\Foo;
          |\Foo\Bar\Foo::Bar();
          |""".stripMargin
      ).result must haveOutput(
        """Foo\Bar\Foo
          |Foo\Bar\Foo
          |Foo\Bar\Foo
          |Foo\Bar\Foo
          |""".stripMargin
      )
    }

    "026: Name ambiguity (class name & namespace name)" in {
      // Zend/tests/ns_026.phpt
      script(
        """<?php
          |namespace Foo;
          |
          |class Foo {
          |  function __construct() {
          |  	echo "Method - ".__CLASS__."::".__FUNCTION__."\n";
          |  }
          |  static function Bar() {
          |  	echo "Method - ".__CLASS__."::".__FUNCTION__."\n";
          |  }
          |}
          |
          |function Bar() {
          |  echo "Func   - ".__FUNCTION__."\n";
          |}
          |
          |$x = new Foo;
          |\Foo\Bar();
          |$x = new \Foo\Foo;
          |\Foo\Foo::Bar();
          |\Foo\Bar();
          |Foo\Bar();
          |""".stripMargin
      ).result must haveOutput(
        """Method - Foo\Foo::__construct
          |Func   - Foo\Bar
          |Method - Foo\Foo::__construct
          |Method - Foo\Foo::Bar
          |Func   - Foo\Bar
          |
          |Fatal error: Call to undefined function Foo\Foo\Bar() in /zend/Namespace3Spec.inlinePhp on line 22
          |""".stripMargin
      )
    }

    "027: Name ambiguity (class name & part of extertnal namespace name)" in {
      // Zend/tests/ns_027.phpt
      script(
        """<?php
          |require "ns_027.inc";
          |
          |class Foo {
          |  function __construct() {
          |  	echo __CLASS__,"\n";
          |  }
          |  static function Bar() {
          |  	echo __CLASS__,"\n";
          |  }
          |}
          |
          |$x = new Foo;
          |Foo::Bar();
          |$x = new Foo\Bar\Foo;
          |Foo\Bar\Foo::Bar();
          |""".stripMargin
      ).result must haveOutput(
        """Foo
          |Foo
          |Foo\Bar\Foo
          |Foo\Bar\Foo
          |""".stripMargin
      )
    }

    "028: Name ambiguity (class name & external namespace name)" in {
      // Zend/tests/ns_028.phpt
      script(
        """<?php
          |require "ns_028.inc";
          |
          |class Foo {
          |  function __construct() {
          |  	echo "Method - ".__CLASS__."::".__FUNCTION__."\n";
          |  }
          |  static function Bar() {
          |  	echo "Method - ".__CLASS__."::".__FUNCTION__."\n";
          |  }
          |}
          |
          |$x = new Foo;
          |Foo\Bar();
          |$x = new Foo\Foo;
          |Foo\Foo::Bar();
          |\Foo\Bar();
          |""".stripMargin
      ).result must haveOutput(
        """Method - Foo::__construct
          |Func   - Foo\Bar
          |Method - Foo\Foo::__construct
          |Method - Foo\Foo::Bar
          |Func   - Foo\Bar
          |""".stripMargin
      )
    }

    "029: Name ambiguity (class name & import name)" in {
      // Zend/tests/ns_029.phpt
      script(
        """<?php
          |use A\B as Foo;
          |
          |class Foo {
          |}
          |
          |new Foo();
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot declare class Foo because the name is already in use in /zend/Namespace3Spec.inlinePhp on line 4
          |""".stripMargin
      )
    }
  }
}
