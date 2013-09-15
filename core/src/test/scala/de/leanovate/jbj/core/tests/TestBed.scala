/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.core.parser.JbjTokens.Token
import de.leanovate.jbj.core.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.core.ast.Prog
import de.leanovate.jbj.core.JbjEnv
import de.leanovate.jbj.api.JbjSettings
import de.leanovate.jbj.runtime.env.CgiEnvironment

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
        println(AstAsXmlNodeVisitor.dump(tree))

        implicit val context = jbj.newGlobalContext(System.out)

        CgiEnvironment.httpRequest(
          TestRequestInfo.post("/bla", "multipart/form-data; boundary=---------------------------20896060251896012921717172737",
            """-----------------------------20896060251896012921717172737
              |Content-Disposition: form-data; name="submitter"
              |
              |testname
              |-----------------------------20896060251896012921717172737
              |Content-Disposition: form-data; name="pics"; filename="bug37276.txt"
              |Content-Type: text/plain
              |
              |bug37276
              |
              |-----------------------------20896060251896012921717172737--
              | """.stripMargin.replace("\n", "\r\n"), Seq.empty))

        context.settings.setErrorReporting(JbjSettings.E_ALL)
        try {
          tree.exec(context)
        } finally {
          context.cleanup()
        }
      case e: parser.NoSuccess =>
        println(e)
    }
  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """<?php
        |
        |echo "\nDirectly changing array values.\n";
        |$a = array("original.1","original.2","original.3");
        |foreach ($a as $k=>$v) {
        |	$a[$k]="changed.$k";
        |	var_dump($v);
        |}
        |var_dump($a);
        |
        |echo "\nModifying the foreach \$value.\n";
        |$a = array("original.1","original.2","original.3");
        |foreach ($a as $k=>$v) {
        |	$v="changed.$k";
        |}
        |var_dump($a);
        |
        |
        |echo "\nModifying the foreach &\$value.\n";
        |$a = array("original.1","original.2","original.3");
        |foreach ($a as $k=>&$v) {
        |	$v="changed.$k";
        |}
        |var_dump($a);
        |
        |echo "\nPushing elements onto an unreferenced array.\n";
        |$a = array("original.1","original.2","original.3");
        |$counter=0;
        |foreach ($a as $v) {
        |	array_push($a, "new.$counter");
        |
        |	//avoid infinite loop if test is failing
        |    if ($counter++>10) {
        |    	echo "Loop detected\n";
        |    	break;
        |    }
        |}
        |var_dump($a);
        |
        |echo "\nPushing elements onto an unreferenced array, using &\$value.\n";
        |$a = array("original.1","original.2","original.3");
        |$counter=0;
        |foreach ($a as &$v) {
        |	array_push($a, "new.$counter");
        |
        |	//avoid infinite loop if test is failing
        |    if ($counter++>10) {
        |    	echo "Loop detected\n";
        |    	break;
        |    }
        |}
        |var_dump($a);
        |
        |echo "\nPopping elements off an unreferenced array.\n";
        |$a = array("original.1","original.2","original.3");
        |foreach ($a as $v) {
        |	array_pop($a);
        |	var_dump($v);
        |}
        |var_dump($a);
        |
        |echo "\nPopping elements off an unreferenced array, using &\$value.\n";
        |$a = array("original.1","original.2","original.3");
        |foreach ($a as &$v) {
        |	array_pop($a);
        |	var_dump($v);
        |}
        |var_dump($a);
        |
        |?>""".stripMargin)
  }
}
