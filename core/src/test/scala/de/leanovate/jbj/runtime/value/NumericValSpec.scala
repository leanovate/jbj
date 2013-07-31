package de.leanovate.jbj.runtime.value

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FreeSpec
import org.scalatest.matchers.MustMatchers

@RunWith(classOf[JUnitRunner])
class NumericValSpec extends FreeSpec with MustMatchers {
  "Numeric pattern" - {
    "int paatern" in {
      numericMatch("123") must be(Some("123"))
      numericMatch("-123") must be(Some("-123"))
      numericMatch("123abc") must be(Some("123"))
      numericMatch("-123abc") must be(Some("-123"))
      numericMatch("ab123") must be(None)
    }

    "float pattern" in {
      numericMatch("123") must be(Some("123"))
      numericMatch("-123") must be(Some("-123"))
      numericMatch("-123e3") must be(Some("-123e3"))
      numericMatch("123.0") must be(Some("123.0"))
      numericMatch("123.0e6") must be(Some("123.0e6"))
      numericMatch("123.0123") must be(Some("123.0123"))
      numericMatch("123.0123E4") must be(Some("123.0123E4"))
      numericMatch(".0123") must be(Some(".0123"))
      numericMatch(".0123e4") must be(Some(".0123e4"))
      numericMatch("123abcd") must be(Some("123"))
      numericMatch("-123abcd") must be(Some("-123"))
      numericMatch("-123e3abcd") must be(Some("-123e3"))
      numericMatch("123.0abcd") must be(Some("123.0"))
      numericMatch("123.0e6abcd") must be(Some("123.0e6"))
      numericMatch("123.0123abcd") must be(Some("123.0123"))
      numericMatch("123.0123E4abcd") must be(Some("123.0123E4"))
      numericMatch(".0123abcd") must be(Some(".0123"))
      numericMatch(".0123e4abcd") must be(Some(".0123e4"))
      numericMatch("ab123") must be(None)
    }

    "string conversion" in {
      convert("679") must be(Some(679.0))
      convert("679abc") must be(Some(679.0))
      convert(" 679") must be(Some(679.0))
      convert("679  ") must be(Some(679.0))
      convert("- 67835") must be(None)
    }
  }

  private def numericMatch(str: String) = {
    str match {
      case NumericVal.numericPattern(num, _, _) if !num.isEmpty => Some(num)
      case _ => None
    }
  }

  private def convert(str:String) = {
    StringVal(str) match {
      case NumericVal(v) => Some(v)
      case _ => None
    }
  }
}
