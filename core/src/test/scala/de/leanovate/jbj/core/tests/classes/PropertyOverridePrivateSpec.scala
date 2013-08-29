package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class PropertyOverridePrivateSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "private property override" should {
    "Redeclare inherited private property as private." in {
      // classes/property_override_private_private.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private $p = "A::p";
          |      function showA()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      private $p = "B::p";
          |      function showB()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |
          |  $a = new A;
          |  $a->showA();
          |
          |  $b = new B;
          |  $b->showA();
          |  $b->showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """A::p
          |A::p
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited private property as private static." in {
      // classes/property_override_private_privateStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private $p = "A::p";
          |      function showA()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      private static $p = "B::p (static)";
          |      static function showB()
          |      {
          |          echo self::$p . "\n";
          |      }
          |  }
          |
          |
          |  $a = new A;
          |  $a->showA();
          |
          |  $b = new B;
          |  $b->showA();
          |  B::showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """A::p
          |A::p
          |B::p (static)
          |""".stripMargin
      )
    }
  }
}
