package de.leanovate.jbj.tests

import scala.util.parsing.input.Reader
import de.leanovate.jbj.parser.JbjTokens.Token
import de.leanovate.jbj.parser.{JbjParser, ParseContext, InitialLexer, TokenReader}
import de.leanovate.jbj.ast.Prog
import de.leanovate.jbj.JbjEnv
import de.leanovate.jbj.runtime.Settings
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

    val jbj = JbjEnv(TestLocator)
    val tokens2 = new TokenReader(exprstr, InitialLexer)
    val parser = new JbjParser(ParseContext("/classes/bla.php", jbj.settings))
    parser.phrase(parser.start)(tokens2) match {
      case parser.Success(tree: Prog, _) =>
        println("Tree")
        val pp = new scala.xml.PrettyPrinter(80, 2)
        println(pp.format(tree.toXml))

        implicit val context = jbj.newGlobalContext(System.out, System.err)

        context.settings.errorReporting = Settings.E_ALL
        CgiEnvironment.httpGet("?ab+cd+ef+123+test")
        tree.exec(context)
      case e: parser.NoSuccess =>
        println(e)
    }

  }

  //A main method for testing
  def main(args: Array[String]) {
    test(
      """<?php
        |error_reporting(E_ALL & ~E_STRICT);
        |
        |function f() { return 0; }
        |$a[0][1] = 'good';
        |$a[1][1] = 'bad';
        |
        |echo "\n" . '$i=f(): ';
        |echo $a[$i=f()][++$i];
        |unset($i);
        |
        |echo "\n" . '$$x=f(): ';
        |$x='i';
        |echo $a[$$x=f()][++$$x];
        |unset($i, $x);
        |
        |echo "\n" . '${\'i\'}=f(): ';
        |echo $a[${'i'}=f()][++${'i'}];
        |unset(${'i'});
        |
        |echo "\n" . '$i[0]=f(): ';
        |echo $a[$i[0]=f()][++$i[0]];
        |unset($i);
        |
        |echo "\n" . '$i[0][0]=f(): ';
        |echo $a[$i[0][0]=f()][++$i[0][0]];
        |unset($i);
        |
        |echo "\n" . '$i->p=f(): ';
        |echo $a[$i->p=f()][++$i->p];
        |unset($i);
        |
        |echo "\n" . '$i->p->q=f(): ';
        |echo $a[$i->p->q=f()][++$i->p->q];
        |unset($i);
        |
        |echo "\n" . '$i->p[0]=f(): ';
        |echo $a[$i->p[0]=f()][++$i->p[0]];
        |unset($i);
        |
        |echo "\n" . '$i->p[0]->p=f(): ';
        |echo $a[$i->p[0]->p=f()][++$i->p[0]->p];
        |unset($i);
        |
        |Class C {
        |	static $p;
        |}
        |
        |echo "\n" . 'C::$p=f(): ';
        |echo $a[C::$p=f()][++C::$p];
        |
        |echo "\n" . 'C::$p[0]=f(): ';
        |C::$p = array();
        |echo $a[C::$p[0]=f()][++C::$p[0]];
        |
        |echo "\n" . 'C::$p->q=f(): ';
        |C::$p = new stdclass;
        |echo $a[C::$p->q=f()][++C::$p->q];
        |?>""".stripMargin)
  }
}
