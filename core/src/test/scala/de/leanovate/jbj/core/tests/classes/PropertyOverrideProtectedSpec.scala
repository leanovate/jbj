/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.core.tests.TestJbjExecutor

class PropertyOverrideProtectedSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "protected property override" should {
    "Redeclare inherited protected property as private (duplicates Zend/tests/errmsg_023.phpt)." in {
      // classes/property_override_protected_private.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected $p = "A::p";
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
        """
          |Fatal error: Access level to B::$p must be protected (as in class A) or weaker in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected property as private static." in {
      // classes/property_override_protected_privateStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected $p = "A::p";
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
        """
          |Fatal error: Cannot redeclare non static A::$p as static B::$p in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected property as protected." in {
      // classes/property_override_protected_protected.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected $p = "A::p";
          |      function showA()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      protected $p = "B::p";
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
          |B::p
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited protected property as protected static." in {
      // classes/property_override_protected_protectedStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected $p = "A::p";
          |      function showA()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      protected static $p = "B::p (static)";
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
        """
          |Fatal error: Cannot redeclare non static A::$p as static B::$p in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected property as public." in {
      // classes/property_override_protected_public.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected $p = "A::p";
          |      function showA()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      public $p = "B::p";
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
          |B::p
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited protected property as public static." in {
      // classes/property_override_protected_publicStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected $p = "A::p";
          |      function showA()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      public static $p = "B::p (static)";
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
        """
          |Fatal error: Cannot redeclare non static A::$p as static B::$p in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected static property as private." in {
      // classes/property_override_protectedStatic_private.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected static $p = "A::p (static)";
          |      static function showA()
          |      {
          |          echo self::$p . "\n";
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
          |  A::showA();
          |
          |  $b = new B;
          |  $b->showA();
          |  $b->showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot redeclare static A::$p as non static B::$p in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected static property as private static." in {
      // classes/property_override_protectedStatic_privateStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected static $p = "A::p (static)";
          |      static function showA()
          |      {
          |          echo self::$p . "\n";
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
          |  A::showA();
          |
          |  B::showA();
          |  B::showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Access level to B::$p must be protected (as in class A) or weaker in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected static property as protected." in {
      // classes/property_override_protectedStatic_protected.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected static $p = "A::p (static)";
          |      static function showA()
          |      {
          |          echo self::$p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      protected $p = "B::p";
          |      function showB()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |
          |  A::showA();
          |
          |  $b = new B;
          |  $b->showA();
          |  $b->showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot redeclare static A::$p as non static B::$p in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected static property as protected static." in {
      // classes/property_override_protectedStatic_protectedStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected static $p = "A::p (static)";
          |      static function showA()
          |      {
          |          echo self::$p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      protected static $p = "B::p (static)";
          |      static function showB()
          |      {
          |          echo self::$p . "\n";
          |      }
          |  }
          |
          |
          |  A::showA();
          |
          |  B::showA();
          |  B::showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """A::p (static)
          |A::p (static)
          |B::p (static)
          |""".stripMargin
      )
    }

    "Redeclare inherited protected static property as public." in {
      // classes/property_override_protectedStatic_public.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected static $p = "A::p (static)";
          |      static function showA()
          |      {
          |          echo self::$p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      public $p = "B::p";
          |      function showB()
          |      {
          |          echo $this->p . "\n";
          |      }
          |  }
          |
          |
          |  A::showA();
          |
          |  $b = new B;
          |  $b->showA();
          |  $b->showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """
          |Fatal error: Cannot redeclare static A::$p as non static B::$p in /classes/PropertyOverrideProtectedSpec.inlinePhp on line 13
          |""".stripMargin
      )
    }

    "Redeclare inherited protected static property as public static." in {
      // classes/property_override_protectedStatic_publicStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      protected static $p = "A::p (static)";
          |      static function showA()
          |      {
          |          echo self::$p . "\n";
          |      }
          |  }
          |
          |  class B extends A
          |  {
          |      public static $p = "B::p (static)";
          |      static function showB()
          |      {
          |          echo self::$p . "\n";
          |      }
          |  }
          |
          |
          |  A::showA();
          |
          |  B::showA();
          |  B::showB();
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """A::p (static)
          |A::p (static)
          |B::p (static)
          |""".stripMargin
      )
    }
  }
}
