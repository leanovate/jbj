package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.context.StaticMethodContext
import de.leanovate.jbj.runtime.ReturnExecResult

case class ClassMethodDeclStmt(modifieres: Set[MemberModifier.Type], name: String, parameterDecls: List[ParameterDecl],
                               stmts: List[Stmt]) extends Stmt with PMethod with BlockLike with FunctionLike {
  private lazy val staticInitializers = stmts.filter(_.isInstanceOf[StaticInitializer]).map(_.asInstanceOf[StaticInitializer])

  override def exec(implicit ctx: Context) = {
    SuccessExecResult
  }

  override lazy val isStatic = modifieres.contains(MemberModifier.STATIC)

  override def invoke(ctx: Context, callerPosition: NodePosition, instance: ObjectVal, parameters: List[Expr]) = {
    implicit val methodCtx = MethodContext(instance, name, callerPosition, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, ctx, callerPosition, parameters)
    execStmts(stmts) match {
      case ReturnExecResult(returnVal) => returnVal
      case _ => NullVal
    }
  }

  override def invokeStatic(ctx: Context, callerPosition: NodePosition, pClass: PClass, parameters: List[Expr]) = {
    implicit val methodCtx = StaticMethodContext(pClass, name, callerPosition, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, ctx, callerPosition, parameters)

    if (!isStatic)
      ctx.log.strict(callerPosition, "Non-static method %s::%s() should not be called statically".format(pClass.name.toString, name))

    execStmts(stmts) match {
      case ReturnExecResult(returnVal) => returnVal
      case _ => NullVal
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + name.toString + " " + position)
    stmts.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }
}
