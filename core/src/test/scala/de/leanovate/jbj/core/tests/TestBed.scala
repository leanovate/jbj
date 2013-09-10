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
        |error_reporting(E_ALL & ~E_STRICT);
        |
        |function f() { return 0; }
        |$a[0][1] = 'good';
        |$a[1][1] = 'bad';
        |
        |echo "\n" . '$i=f() :';
        |echo $a[$i=f()][++$i];
        |unset($i);
        |
        |echo "\n" . '$$x=f() :';
        |$x='i';
        |echo $a[$$x=f()][++$$x];
        |unset($i, $x);
        |
        |echo "\n" . '${\'i\'}=f() :';
        |echo $a[${'i'}=f()][++${'i'}];
        |unset(${'i'});
        |
        |echo "\n" . '$i[0]=f() :';
        |echo $a[$i[0]=f()][++$i[0]];
        |unset($i);
        |
        |echo "\n" . '$i[0][0]=f() :';
        |echo $a[$i[0][0]=f()][++$i[0][0]];
        |unset($i);
        |
        |echo "\n" . '$i->p=f() :';
        |echo $a[$i->p=f()][++$i->p];
        |unset($i);
        |
        |echo "\n" . '$i->p->q=f() :';
        |echo $a[$i->p->q=f()][++$i->p->q];
        |unset($i);
        |
        |echo "\n" . '$i->p[0]=f() :';
        |echo $a[$i->p[0]=f()][++$i->p[0]];
        |unset($i);
        |
        |echo "\n" . '$i->p[0]->p=f() :';
        |echo $a[$i->p[0]->p=f()][++$i->p[0]->p];
        |unset($i);
        |
        |Class C {
        |	static $p;
        |}
        |
        |echo "\n" . 'C::$p=f() :';
        |echo $a[C::$p=f()][++C::$p];
        |
        |echo "\n" . 'C::$p[0]=f() :';
        |C::$p = array();
        |echo $a[C::$p[0]=f()][++C::$p[0]];
        |
        |echo "\n" . 'C::$p->q=f() :';
        |C::$p = new stdclass;
        |echo $a[C::$p->q=f()][++C::$p->q];
        |?>""".stripMargin)
  }
}
