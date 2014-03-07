package de.leanovate.jbj.transcode.transcoded

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.transcode.TestJbjEnvironment

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
}
