package de.leanovate.jbj.tests.classes

import de.leanovate.jbj.tests.TestJbjExecutor
import org.specs2.mutable.SpecificationWithJUnit

class FactorySpec extends SpecificationWithJUnit with TestJbjExecutor {
  "Fectory" should {
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
