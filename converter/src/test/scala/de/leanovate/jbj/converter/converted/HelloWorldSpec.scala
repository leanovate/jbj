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
}
