package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{FilePosition, Stmt}
import de.leanovate.jbj.runtime.Context

case class ClassDefStmt(position:FilePosition, className:String, superClassName:Option[String], body: BlockStmt) extends Stmt {
  def exec(ctx: Context) = ???
}
