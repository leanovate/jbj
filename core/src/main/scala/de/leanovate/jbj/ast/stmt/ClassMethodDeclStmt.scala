package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, NodePosition, MemberModifier, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value.{NullVal, ObjectVal}
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.ReturnExecResult
import java.io.PrintStream

case class ClassMethodDeclStmt(modifieres: Set[MemberModifier.Type], name: String, parameterDecls: List[ParameterDecl],
                               stmts: List[Stmt]) extends Stmt with PMethod {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = {
    SuccessExecResult
  }

  def call(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, parameters: List[Value]) = {
    implicit val methodCtx = MethodContext(instance, name, callerPosition, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx))
      methodCtx.static.initialized = true
    }

    parameterDecls.zipWithIndex.foreach {
      case (parameterDef, idx) =>
        val value = parameters.drop(idx).headOption.getOrElse(parameterDef.defaultVal(ctx))
        methodCtx.defineVariable(parameterDef.variableName, ValueRef(value.copy))
    }
    Left(execStmts(stmts))
  }

  @tailrec
  private def execStmts(statements: List[Stmt])(implicit context: Context): Value = statements match {
    case head :: tail => head.exec match {
      case ReturnExecResult(returnVal) => returnVal
      case _ => execStmts(tail)
    }
    case Nil => NullVal
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + name.toString + " " + position)
    stmts.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }
}
