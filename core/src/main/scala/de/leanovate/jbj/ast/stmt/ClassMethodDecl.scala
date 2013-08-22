package de.leanovate.jbj.ast.stmt

import de.leanovate.jbj.ast._
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.runtime.context.{Context, MethodContext, StaticMethodContext}
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

case class ClassMethodDecl(modifieres: Set[MemberModifier.Type], name: String, returnByRef: Boolean, parameterDecls: List[ParameterDecl],
                               stmts: List[Stmt]) extends ClassMemberDecl with PMethod with BlockLike with FunctionLike {
  private lazy val staticInitializers = StaticInitializer.collect(stmts:_*)

  override lazy val isStatic = modifieres.contains(MemberModifier.STATIC)

  override def invoke(ctx: Context, instance: ObjectVal, parameters: List[Expr]) = {
    implicit val methodCtx = MethodContext(instance, name, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, ctx, parameters)
    perform(methodCtx, returnByRef, stmts)
  }

  override def invokeStatic(ctx: Context, pClass: PClass, parameters: List[Expr]) = {
    implicit val methodCtx = StaticMethodContext(pClass, name, ctx)

    if (!methodCtx.static.initialized) {
      staticInitializers.foreach(_.initializeStatic(methodCtx.static))
      methodCtx.static.initialized = true
    }

    setParameters(methodCtx, ctx, parameters)

    if (!isStatic)
      ctx.log.strict("Non-static method %s::%s() should not be called statically".format(pClass.name.toString, name))

    perform(methodCtx, returnByRef, stmts)
  }

  override def checkRules(pClass: PClass)(implicit ctx: Context) {
    name match {
      case "__call" =>
        if (parameterDecls.size != 2)
          throw new FatalErrorJbjException("Method %s::__call() must take exactly 2 arguments".format(pClass.name.toString))
      case "__get" =>
        if (parameterDecls.size != 1)
          throw new FatalErrorJbjException("Method %s::__get() must take exactly 1 argument".format(pClass.name.toString))
      case "__set" =>
        if (parameterDecls.size != 2)
          throw new FatalErrorJbjException("Method %s::__set() must take exactly 2 arguments".format(pClass.name.toString))
      case _ =>
    }
  }

  override def dump(out: PrintStream, ident: String) {
    out.println(ident + getClass.getSimpleName + " " + name.toString + " " + position)
    stmts.foreach {
      stmt =>
        stmt.dump(out, ident + "  ")
    }
  }

  override def visit[R](visitor: NodeVisitor[R]) = visitor(this).thenChildren(parameterDecls).thenChildren(stmts)
}
