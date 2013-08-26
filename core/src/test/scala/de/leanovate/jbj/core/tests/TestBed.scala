package de.leanovate.jbj.core.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.core.parser.JbjTokens.Token
import de.leanovate.jbj.core.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.JbjEnv
import de.leanovate.jbj.api.JbjSettings

object TestBed {
  //Simplify testing
  def test(exprstr: String) = {
    var tokens: Reader[Token] = new TokenReader(exprstr, InitialLexer)

    println("Tokens")
    var count = 0
    while (!tokens.atEnd && count < 1000) {
      println(tokens.first)
      tokens = tokens.rest
      count += 1
    }

    val jbj = JbjEnv(TestLocator, errorStream = Some(System.err))
    val tokens2 = new TokenReader(exprstr, InitialLexer)
    val parser = new JbjParser(ParseContext("/classes/bla.php", jbj.settings))
    parser.phrase(parser.start)(tokens2) match {
      case parser.Success(tree: Prog, _) =>
        println("Tree")
        val pp = new scala.xml.PrettyPrinter(80, 2)
        println(pp.format(tree.toXml))

        implicit val context = jbj.newGlobalContext(System.out)

        context.settings.setErrorReporting(JbjSettings.E_ALL)
        tree.exec(context)
        context.cleanup()
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """"<?php
        |
        |define("MAX_64Bit", 9223372036854775807);
        |define("MAX_32Bit", 2147483647);
        |define("MIN_64Bit", -9223372036854775807 - 1);
        |define("MIN_32Bit", -2147483647 - 1);
        |
        |$longVals = array(
        |    MAX_64Bit, MIN_64Bit, MAX_32Bit, MIN_32Bit, MAX_64Bit - MAX_32Bit, MIN_64Bit - MIN_32Bit,
        |    MAX_32Bit + 1, MIN_32Bit - 1, MAX_32Bit * 2, (MAX_32Bit * 2) + 1, (MAX_32Bit * 2) - 1,
        |    MAX_64Bit -1, MAX_64Bit + 1, MIN_64Bit + 1, MIN_64Bit - 1
        |);
        |
        |$otherVals = array(0, 1, -1, 7, 9, 65, -44, MAX_32Bit, MAX_64Bit);
        |
        |error_reporting(E_ERROR);
        |
        |foreach ($longVals as $longVal) {
        |   foreach($otherVals as $otherVal) {
        |	   echo "--- testing: $longVal << $otherVal ---\n";
        |      var_dump($longVal<<$otherVal);
        |   }
        |}
        |
        |foreach ($otherVals as $otherVal) {
        |   foreach($longVals as $longVal) {
        |	   echo "--- testing: $otherVal << $longVal ---\n";
        |      var_dump($otherVal<<$longVal);
        |   }
        |}
        |
        |?>""".stripMargin)
  }
}
