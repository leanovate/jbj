package de.leanovate.jbj.core.tests.zend

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class Namespace7Spec extends SpecificationWithJUnit with TestJbjExecutor {
  "Namespace tests 060-069" should {
    "060: multiple namespaces per file" in {
      // Zend/tests/ns_060.phpt
      script(
        """<?php
          |namespace Foo;
          |use Bar\A as B;
          |class A {}
          |$a = new B;
          |$b = new A;
          |echo get_class($a)."\n";
          |echo get_class($b)."\n";
          |namespace Bar;
          |use Foo\A as B;
          |$a = new B;
          |$b = new A;
          |echo get_class($a)."\n";
          |echo get_class($b)."\n";
          |class A {}
          |""".stripMargin
      ).result must haveOutput(
        """Bar\A
          |Foo\A
          |Foo\A
          |Bar\A
          |""".stripMargin
      )
    }

    "061: use in global scope" in {
      // Zend/tests/ns_061.phpt
      script(
        """<?php
          |class A {}
          |use \A as B;
          |echo get_class(new B)."\n";
          |""".stripMargin
      ).result must haveOutput(
        """A
          |""".stripMargin
      )
    }

    "062: use \\global class" in {
      // Zend/tests/ns_062.phpt
      script(
        """<?php
          |namespace Foo;
          |use \stdClass;
          |use \stdClass as A;
          |echo get_class(new stdClass)."\n";
          |echo get_class(new A)."\n";
          |""".stripMargin
      ).result must haveOutput(
        """stdClass
          |stdClass
          |""".stripMargin
      )
    }

    "063: Old-style constructors in namesapces (not supported!)" in {
      // Zend/tests/ns_063.phpt
      script(
        """<?php
          |namespace Foo;
          |class Bar {
          |	function Bar() {
          |		echo "ok\n";
          |	}
          |}
          |new Bar();
          |echo "ok\n";
          |""".stripMargin
      ).result must haveOutput(
        """ok
          |""".stripMargin
      )
    }

    "Magic methods in overrided stdClass inside namespace" in {
      // Zend/tests/ns_064.phpt
      script(
        """<?php
          |
          |namespace test;
          |
          |class foo {
          |	public $e = array();
          |
          |	public function __construct() {
          |		$this->e[] = $this;
          |	}
          |
          |	public function __set($a, $b) {
          |		var_dump($a, $b);
          |	}
          |	public function __get($a) {
          |		var_dump($a);
          |		return $this;
          |	}
          |}
          |
          |use test\foo as stdClass;
          |
          |$x = new stdClass;
          |$x->a = 1;
          |$x->b->c = 1;
          |$x->d->e[0]->f = 2;
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """string(1) "a"
          |int(1)
          |string(1) "b"
          |string(1) "c"
          |int(1)
          |string(1) "d"
          |string(1) "f"
          |int(2)
          |""".stripMargin
      )
    }

    "065: Multiple names in use statement" in {
      // Zend/tests/ns_065.phpt
      script(
        """<?php
          |use X\Y as test, X\Z as test2;
          |
          |require "ns_065.inc";
          |
          |test\foo();
          |test2\foo();
          |""".stripMargin
      ).result must haveOutput(
        """X\Y\foo
          |X\Z\foo
          |""".stripMargin
      )
    }

    "066: Name ambiguity (import name & internal class name)" in {
      // Zend/tests/ns_066.phpt
      script(
        """<?php
          |include __DIR__ . '/ns_027.inc';
          |use Foo\Bar\Foo as stdClass;
          |
          |new stdClass();
          |""".stripMargin
      ).result must haveOutput(
        """Foo\Bar\Foo
          |""".stripMargin
      )
    }

    "067: Name ambiguity (import name & internal class name)" in {
      // Zend/tests/ns_067.phpt
      script(
        """<?php
          |include __DIR__ . '/ns_022.inc';
          |include __DIR__ . '/ns_027.inc';
          |include __DIR__ . '/ns_067.inc';
          |""".stripMargin
      ).result must haveOutput(
        """Foo\Bar\Foo
          |""".stripMargin
      )
    }

    "068: Code before namespace" in {
      // ../php-src/Zend/tests/ns_068.phpt
      script(
        """<?php
          |echo __NAMESPACE__ . "\n";
          |namespace foo;
          |echo __NAMESPACE__ . "\n";
          |namespace bar;
          |echo __NAMESPACE__ . "\n";
          |?>
          |===DONE===
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Namespace declaration statement has to be the very first statement in the script in /zend/Namespace7Spec.inlinePhp on line 2
          |""".stripMargin
      )
    }
  }
}
