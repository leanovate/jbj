package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Stmt, Expr}
import de.leanovate.jbj.ast.stmt.BlockStmt

case class ElseIfBlock(condition: Expr, thenBlock: Stmt)