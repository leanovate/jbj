package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{NamespaceName, Stmt}

case class CatchBlock(exceptionName: NamespaceName, variableName: String, stmts: List[Stmt]) {

}
