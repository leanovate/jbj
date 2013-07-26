package de.leanovate.jbj.ast.stmt.cond

import de.leanovate.jbj.ast.{Stmt, Expr}

case class ElseIfBlock(condition: Expr, thenBlock: Stmt)