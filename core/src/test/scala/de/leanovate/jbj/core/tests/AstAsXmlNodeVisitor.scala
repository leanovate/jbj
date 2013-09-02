/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import de.leanovate.jbj.core.ast.{Prog, Node, NodeVisitor}
import scala.xml.{Text, PCData, NodeSeq}
import de.leanovate.jbj.core.ast.stmt.{EchoStmt, BlockStmt, ExprStmt, InlineStmt}
import de.leanovate.jbj.core.ast.expr.{VariableReferableExpr, AssignReferableExpr, CallFunctionReferableExpr}
import de.leanovate.jbj.core.ast.name.{StaticNamespaceName, StaticName}
import de.leanovate.jbj.core.ast.stmt.loop.ForStmt
import de.leanovate.jbj.core.ast.decl.{ParameterDecl, ClassMethodDecl, InterfaceDeclStmt, ClassDeclStmt}

object AstAsXmlNodeVisitor extends NodeVisitor[NodeSeq] {
  def apply(node: Node) = node match {
    case assign: AssignReferableExpr =>
      NextSibling(
        <assign>
          <to>
            {assign.reference.visit(this).results}
          </to>
          <value>
            {assign.expr.visit(this).results}
          </value>
        </assign>
      )
    case block: BlockStmt =>
      NextSibling(
        <block>
          {block.stmts.flatMap(_.visit(this).results)}
        </block>
      )
    case callFunction: CallFunctionReferableExpr =>
      NextSibling(
        <call-function>
          <name>
            {callFunction.functionName.visit(this).results}
          </name>
          <parameters>
            {callFunction.parameters.flatMap(_.visit(this).results)}
          </parameters>
        </call-function>
      )
    case classDecl: ClassDeclStmt =>
      NextSibling(
        <class line={classDecl.position.line.toString} file={classDecl.position.fileName}>
          <name>
            {classDecl.name}
          </name>{classDecl.superClassName.map {
          superClassName =>
            <extends>
              {superClassName}
            </extends>
        }.getOrElse(NodeSeq.Empty)}{classDecl.implements.map {
          implements =>
            <implements>
              {implements}
            </implements>
        }}{classDecl.decls.flatMap(_.visit(this).results)}
        </class>
      )
    case classMethodDecl: ClassMethodDecl =>
      NextSibling(
        <method line={classMethodDecl.position.line.toString} file={classMethodDecl.position.fileName}>
          <name>
            {classMethodDecl.name}
          </name>{classMethodDecl.parameterDecls.flatMap(_.visit(this).results)}{classMethodDecl.stmts.toSeq.flatMap(_.flatMap(_.visit(this).results))}
        </method>
      )
    case echoStmt: EchoStmt =>
      NextSibling(
        <echo line={echoStmt.position.line.toString} file={echoStmt.position.fileName}>
          {echoStmt.parameters.flatMap(_.visit(this).results)}
        </echo>
      )
    case forStmt: ForStmt =>
      NextSibling(
        <for line={forStmt.position.line.toString} file={forStmt.position.fileName}>
          <start>
            {forStmt.befores.flatMap(_.visit(this).results)}
          </start>
          <cond>
            {forStmt.conditions.flatMap(_.visit(this).results)}
          </cond>
          <end>
            {forStmt.afters.flatMap(_.visit(this).results)}
          </end>
          <stmts>
            {forStmt.stmts.flatMap(_.visit(this).results)}
          </stmts>
        </for>
      )
    case inline: InlineStmt =>
      NextSibling(
        <inline line={inline.position.line.toString} file={inline.position.fileName}>
          {PCData(inline.text)}
        </inline>
      )
    case interfaceDecl: InterfaceDeclStmt =>
      NextSibling(
        <class line={interfaceDecl.position.line.toString} file={interfaceDecl.position.fileName}>
          <name>
            {interfaceDecl.name}
          </name>{interfaceDecl.superInterfaces.map {
          ext =>
            <extends>
              {ext}
            </extends>
        }}{interfaceDecl.decls.flatMap(_.visit(this).results)}
        </class>
      )
    case exprStmt: ExprStmt =>
      NextSibling(
        <expr line={exprStmt.position.line.toString} file={exprStmt.position.fileName}>
          {exprStmt.expr.visit(this).results}
        </expr>
      )
    case parameterDecl: ParameterDecl =>
      NextSibling(
        <parameter>
          <name>
            {parameterDecl.variableName}
          </name>{parameterDecl.typeHint.map {
          typeHint =>
            <typeHine>
              {typeHint}
            </typeHine>
        }.getOrElse(NodeSeq.Empty)}{parameterDecl.default.map {
          defaultExpr =>
            <default>
              {defaultExpr.visit(this).results}
            </default>
        }.getOrElse(NodeSeq.Empty)}
        </parameter>
      )
    case prog: Prog =>
      NextSibling(
        <script>
          {prog.stmts.flatMap(_.visit(this).results)}
        </script>
      )
    case staticName: StaticName =>
      NextSibling(
        Text(staticName.name)
      )
    case staticNamespaceName: StaticNamespaceName =>
      NextSibling(
        Text(staticNamespaceName.namespaceName.toString)
      )
    case variable: VariableReferableExpr =>
      NextSibling(
        <variable>
          {variable.variableName.visit(this).results}
        </variable>
      )
    case _ =>
      NextChild(<node/>.copy(label = node.getClass.getSimpleName))
  }

  def dump(node: Node): String = {
    val pp = new scala.xml.PrettyPrinter(80, 2)
    val nodesBuilder = NodeSeq.newBuilder
    node.visit(this).results.foreach {
      nodes =>
        nodesBuilder ++= nodes
    }
    pp.formatNodes(nodesBuilder.result())
  }
}
