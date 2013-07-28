package de.leanovate.jbj.ast.stmt.loop

import de.leanovate.jbj.ast.{FilePosition, Stmt, Expr}
import de.leanovate.jbj.runtime._
import java.util.concurrent.atomic.AtomicLong
import de.leanovate.jbj.ast.stmt.BlockStmt
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.value.{ArrayVal}
import de.leanovate.jbj.runtime.BreakExecResult
import de.leanovate.jbj.runtime.SuccessExecResult
import de.leanovate.jbj.runtime.context.BlockContext
import de.leanovate.jbj.runtime.value.ArrayVal.ArrayKey

case class ForeachKeyValueStmt(position: FilePosition, identifier: String, arrayExpr: Expr, keyName: String, valueName: String,
                               stmts: List[Stmt]) extends Stmt {
  def exec(ctx: Context) = {
    val blockCtx = BlockContext(identifier, ctx)

    arrayExpr.eval(ctx).unref match {
      case array: ArrayVal =>
        execValues(array.keyValues, blockCtx)
      case _ =>
    }
    SuccessExecResult()
  }

  @tailrec
  private def execValues(keyValues: List[(ArrayKey, Value)], context: BlockContext): ExecResult = keyValues match {
    case head :: tail =>
      context.defineVariable(keyName, ValueRef(head._1.value))
      context.defineVariable(valueName, ValueRef(head._2))
      execStmts(stmts, context) match {
        case BreakExecResult(depth) if depth > 1 => BreakExecResult(depth - 1)
        case BreakExecResult(_) => SuccessExecResult()
        case result: ReturnExecResult => result
        case _ => execValues(tail, context)
      }
    case Nil => SuccessExecResult()
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: BlockContext): ExecResult = statements match {
    case head :: tail => head.exec(context) match {
      case SuccessExecResult() => execStmts(tail, context)
      case result => result
    }
    case Nil => SuccessExecResult()
  }
}

object ForeachKeyValueStmt {
  private val forCount = new AtomicLong()

  def apply(position: FilePosition, arrayExpr: Expr, keyName: String, valueName: String, forBlock: Stmt): ForeachKeyValueStmt = forBlock match {
    case block: BlockStmt => ForeachKeyValueStmt(position, nextIdentifier(), arrayExpr, keyName, valueName, block.stmts)
    case stmt => ForeachKeyValueStmt(position, nextIdentifier(), arrayExpr, keyName, valueName, stmt :: Nil)
  }

  private def nextIdentifier(): String = "foreachkeyvalue_" + forCount.incrementAndGet()

}
