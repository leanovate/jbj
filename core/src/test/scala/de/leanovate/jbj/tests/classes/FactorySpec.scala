package de.leanovate.jbj.tests.classes

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import de.leanovate.jbj.tests.TestJbjExecutor
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class FactorySpec extends FreeSpec with TestJbjExecutor with MustMatchers {
  "Fectory" - {
    "ZE2 factory objects" in {
      // classes/factory_001
      script(
        """<?php
          |
          |class Circle {
          |	function draw() {
          |		echo "Circle\n";
          |	}
          |}
          |
          |class Square {
          |	function draw() {
          |		print "Square\n";
          |	}
          |}
          |
          |function ShapeFactoryMethod($shape) {
          |	switch ($shape) {
          |		case "Circle":
          |			return new Circle();
          |		case "Square":
          |			return new Square();
          |	}
          |}
          |
          |ShapeFactoryMethod("Circle")->draw();
          |ShapeFactoryMethod("Square")->draw();
          |
          |?>""".stripMargin
      ).result must haveOutput(
        """Circle
          |Square
          |""".stripMargin
      )
    }
  }
}
