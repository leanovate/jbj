package de.leanovate.jbj.runtime.value

import org.specs2.mutable.SpecificationWithJUnit
import de.leanovate.jbj.runtime.context.{Context, GlobalContext}
import de.leanovate.jbj.JbjEnv

class NumericPatternSpec extends SpecificationWithJUnit {
  "Numeric pattern" should {
    "int paatern" in {
      numericMatch("123") must beSome("123")
      numericMatch("-123") must beSome("-123")
      numericMatch("123abc") must beSome("123")
      numericMatch("-123abc") must beSome("-123")
      numericMatch("ab123") must beNone
    }

    "float pattern" in {
      numericMatch("123") must beSome("123")
      numericMatch("-123") must beSome("-123")
      numericMatch("-123e3") must beSome("-123e3")
      numericMatch("123.0") must beSome("123.0")
      numericMatch("123.0e6") must beSome("123.0e6")
      numericMatch("123.0123") must beSome("123.0123")
      numericMatch("123.0123E4") must beSome("123.0123E4")
      numericMatch(".0123") must beSome(".0123")
      numericMatch(".0123e4") must beSome(".0123e4")
      numericMatch("123abcd") must beSome("123")
      numericMatch("-123abcd") must beSome("-123")
      numericMatch("-123e3abcd") must beSome("-123e3")
      numericMatch("123.0abcd") must beSome("123.0")
      numericMatch("123.0e6abcd") must beSome("123.0e6")
      numericMatch("123.0123abcd") must beSome("123.0123")
      numericMatch("123.0123E4abcd") must beSome("123.0123E4")
      numericMatch(".0123abcd") must beSome(".0123")
      numericMatch(".0123e4abcd") must beSome(".0123e4")
      numericMatch("ab123") must beNone
    }

    "string conversion" in {
      val env = JbjEnv()
      implicit val ctx = env.newGlobalContext(null, null)

      convert("679") must beSome(679.0)
      convert("679abc") must beSome(679.0)
      convert(" 679") must beSome(679.0)
      convert("679  ") must beSome(679.0)
      convert("- 67835") must beNone
    }
  }

  private def numericMatch(str: String) = {
    str match {
      case NumericVal.numericPattern(num, _, _) if !num.isEmpty => Some(num)
      case _ => None
    }
  }

  private def convert(str: String)(implicit ctx: Context) = {
    StringVal(str) match {
      case NumericVal(v) => Some(v)
      case _ => None
    }
  }
}
