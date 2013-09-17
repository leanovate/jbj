/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.parser


sealed trait LexerMode {
  def newLexer(): Lexer
}

case class InitialLexerMode(shortOpenTag: Boolean, aspTags: Boolean) extends LexerMode {
  def newLexer() = InitialLexer(this)
}

case class ScriptingLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = ScriptLexer(this)
}

case class DoubleQuotedLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = DoubleQuotesLexer(this)
}

case class HeredocLexerMode(endMarker: String, prevMode: LexerMode) extends LexerMode {
  def newLexer() = new HereDocLexer(this)
}

case class EncapsScriptingLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = new EncapsScriptingLexer(prevMode)
}

case class LookingForPropertyLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = new LookingForPropertyLexer(prevMode)
}

case class LookingForVarnameLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = new LookingForVarnameLexer(prevMode)
}
