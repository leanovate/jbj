package de.leanovate.jbj.tests.classes

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.tests.TestJbjExecutor

class SingletonSpec extends SpecificationWithJUnit with TestJbjExecutor {
  "singleton" should {
    "ZE2 singleton" in {
      // classes/singleton_001.phpt
      script(
        """<?php
          |
          |class Counter {
          |	private $counter = 0;
          |
          |	function increment_and_print() {
          |		echo ++$this->counter;
          |		echo "\n";
          |    }
          |}
          |
          |
          |class SingletonCounter {
          |	private static $m_instance = NULL;
          |
          |	static function Instance() {
          |		if (self::$m_instance == NULL) {
          |			self::$m_instance = new Counter();
          |		}
          |		return self::$m_instance;
          |	}
          |}
          |
          |SingletonCounter::Instance()->increment_and_print();
          |SingletonCounter::Instance()->increment_and_print();
          |SingletonCounter::Instance()->increment_and_print();
          |
          |?>
          |""".stripMargin
      ).result must haveOutput(
        """1
          |2
          |3
          |""".stripMargin
      )
    }
  }
}
