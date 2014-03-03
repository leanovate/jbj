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

  def pushScripting() = Some(ScriptingLexerMode(this))
}

case class ScriptingLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = ScriptLexer(this)

  def pop() = Some(prevMode)
}

case class DoubleQuotedLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = DoubleQuotesLexer(this)

  def pushLookingForProperty() = Some(LookingForPropertyLexerMode(this))

  def pushEncapsScripting() = Some(EncapsScriptingLexerMode(this))

  def pushVarOffset() = Some(VarOffsetLexerMode(this))

  def pop() = Some(prevMode)
}

case class HeredocLexerMode(endMarker: String, prevMode: LexerMode) extends LexerMode {
  def newLexer() = new HereDocLexer(this)

  def pushLookingForProperty() = Some(LookingForPropertyLexerMode(this))

  def pushEncapsScripting() = Some(EncapsScriptingLexerMode(this))

  def pushVarOffset() = Some(VarOffsetLexerMode(this))

  def pop() = Some(prevMode)
}

case class NowdocLexerMode(endMarker: String, prevMode: LexerMode) extends LexerMode {
  def newLexer() = new NowDocLexer(this)

  def pop() = Some(prevMode)
}

case class EncapsScriptingLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = new EncapsScriptingLexer(this)

  def pop() = Some(prevMode)
}

case class LookingForPropertyLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = new LookingForPropertyLexer(this)

  def pop() = Some(prevMode)
}

case class LookingForVarnameLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = new LookingForVarnameLexer(prevMode)

  def pop() = Some(prevMode)
}

case class VarOffsetLexerMode(prevMode: LexerMode) extends LexerMode {
  def newLexer() = new VarOffsetLexer(this)

  def pop() = Some(prevMode)
}