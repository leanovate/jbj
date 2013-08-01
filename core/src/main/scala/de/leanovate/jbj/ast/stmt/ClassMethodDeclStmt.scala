package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast.{StaticInitializer, NodePosition, MemberModifier, Stmt}
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value.{NullVal, ObjectVal}
import scala.annotation.tailrec
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.ReturnExecResult

case class ClassMethodDeclStmt(modifieres: Set[MemberModifier.Type], name: String, parameterDecls: List[ParameterDecl],
                               stmts: List[Stmt]) extends Stmt with PMethod {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  def exec(ctx: Context) = {
    SuccessExecResult()
  }

  def call(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, parameters: List[Value]) = {
    val methodCtx = MethodContext(instance, name, callerPosition, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx))
      methodCtx.static.initialized = true
    }

    parameterDecls.zipWithIndex.foreach {
      case (parameterDef, idx) =>
        val value = parameters.drop(idx).headOption.getOrElse(parameterDef.defaultVal(ctx))
        methodCtx.defineVariable(parameterDef.variableName, ValueRef(value.copy))
    }
    Left(execStmts(stmts, methodCtx))
  }

  @tailrec
  private def execStmts(statements: List[Stmt], context: Context): Value = statements match {
    case head :: tail => head.exec(context) match {
      case ReturnExecResult(returnVal) => returnVal
      case _ => execStmts(tail, context)
    }
    case Nil => NullVal
  }
}
