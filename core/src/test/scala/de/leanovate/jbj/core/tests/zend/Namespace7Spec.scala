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
  }
}
