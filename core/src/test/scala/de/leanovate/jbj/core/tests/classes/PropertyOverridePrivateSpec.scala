/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

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

    "Redeclare inherited private property as protected." in {
      // classes/property_override_private_protected.phpt
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
          |A::p
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited private property as protected static." in {
      // classes/property_override_private_protectedStatic.phpt
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
        """A::p
          |A::p
          |B::p (static)
          |""".stripMargin
      )
    }

    "Redeclare inherited private property as public." in {
      // classes/property_override_private_public.phpt
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
          |A::p
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited private property as public static." in {
      // classes/property_override_private_publicStatic.phpt
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
        """A::p
          |A::p
          |B::p (static)
          |""".stripMargin
      )
    }

    "Redeclare inherited private static property as private." in {
      // classes/property_override_privateStatic_private.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private static $p = "A::p (static)";
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
        """A::p (static)
          |A::p (static)
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited private static property as private static." in {
      // classes/property_override_privateStatic_privateStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private static $p = "A::p (static)";
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
        """A::p (static)
          |A::p (static)
          |B::p (static)
          |""".stripMargin
      )
    }

    "Redeclare inherited private static property as protected." in {
      // classes/property_override_privateStatic_protected.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private static $p = "A::p (static)";
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
        """A::p (static)
          |A::p (static)
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited private static property as protected static." in {
      // classes/property_override_privateStatic_protectedStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private static $p = "A::p (static)";
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

    "Redeclare inherited private static property as public." in {
      // classes/property_override_privateStatic_public.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private static $p = "A::p (static)";
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
        """A::p (static)
          |A::p (static)
          |B::p
          |""".stripMargin
      )
    }

    "Redeclare inherited private static property as public static." in {
      // classes/property_override_privateStatic_publicStatic.phpt
      script(
        """<?php
          |  class A
          |  {
          |      private static $p = "A::p (static)";
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
