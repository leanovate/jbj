package de.leanovate.jbj.converter.converted

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.converter.TestJbjEnvironment

class HelloWorldSpec extends SpecificationWithJUnit with TestJbjEnvironment {
  "Hello world" should {
    "Produce same output as php" in {
      exec(testunits.hello_world).result must haveOutput(
        """This is before
          |Hello worldThis is after
          |""".stripMargin
      )
    }
  }

  "Hello world2" should {
    "Produce same output as php" in {
      exec(testunits.hello_world2).result must haveOutput(
        """Hello world42"""
      )
    }
  }

  "Hello world3" should {
    "Produce same output as php" in {
      exec(testunits.hello_world3).result must haveOutput(
        """Hello
          |World
          |42
          |1
          |4
          |9
          |""".stripMargin
      )
    }
  }

  "Hello world4" should {
    "Producde same output as php" in {
      exec(testunits.hello_world4).result must haveOutput(
        """7
          |19
          |25
          |""".stripMargin
      )
    }
  }
}
