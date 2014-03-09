package de.leanovate.jbj.transcode.builders

import scala.text.Document
import scala.text.Document._
import de.leanovate.jbj.runtime.value.{DoubleVal, IntegerVal, StringVal, PVal}

object LiteralBuilder {
  def buildSingleLine(str: String): Document =
    text("\"\"\"") :: text(str) :: text("\"\"\"")

  def buildMultiLine(str: String): Document = {
    var result = text("\"\"\"")
    var start = 0
    var next = str.indexOf('\n')

    while (next >= 0) {
      result = result :: text(str.substring(start, next)) :/: text("|")
      start = next + 1
      next = str.indexOf(start, '\n')
    }
    nest(3, result :: text(str.substring(start)) :: text("\"\"\".stripMargin"))
  }

  def build(str: String): Document = {
    if (str.isEmpty) {
      text("\"\"")
    } else if (str.contains('\n')) {
      buildMultiLine(str)
    } else {
      buildSingleLine(str)
    }
  }

  def build(i: Int): Document = text(i.toString)

  def build(i: Long): Document = text(i.toString) :: text("L")

  def build(d: Double): Document = text(d.toString)

  def build(pVal: PVal): Document = pVal.concrete match {
    case StringVal(str) => text("p(") :: build(str) :: text(")")
    case IntegerVal(i) => text("p(") :: build(i) :: text(")")
    case DoubleVal(d) => text("p(") :: build(d) :: text(")")
  }
}
