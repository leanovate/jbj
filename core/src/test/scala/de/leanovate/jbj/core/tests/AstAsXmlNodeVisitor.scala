/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

package de.leanovate.jbj.core.tests

import de.leanovate.jbj.core.ast.{Prog, Node, NodeVisitor}
import scala.xml.{Text, PCData, NodeSeq}
import de.leanovate.jbj.core.ast.expr._
import de.leanovate.jbj.core.ast.stmt.BlockStmt
import de.leanovate.jbj.core.ast.expr.VariableRefExpr
import de.leanovate.jbj.core.ast.stmt.loop.ForStmt
import de.leanovate.jbj.core.ast.expr.AssignRefExpr
import de.leanovate.jbj.core.ast.decl.ParameterDecl
import de.leanovate.jbj.core.ast.decl.ClassDeclStmt
import de.leanovate.jbj.core.ast.name.{DynamicName, StaticName, StaticNamespaceName}
import de.leanovate.jbj.core.ast.decl.InterfaceDeclStmt
import de.leanovate.jbj.core.ast.stmt.InlineStmt
import de.leanovate.jbj.core.ast.expr.NewRefExpr
import de.leanovate.jbj.core.ast.stmt.ExprStmt
import de.leanovate.jbj.core.ast.decl.ClassMethodDecl
import de.leanovate.jbj.core.ast.stmt.EchoStmt

object AstAsXmlNodeVisitor extends NodeVisitor[NodeSeq] {
  def visit = {
    case assign: AssignRefExpr =>
      acceptsNextSibling(
        <assign>
          <to>
            {assign.reference.accept(this).results}
          </to>
          <value>
            {assign.expr.accept(this).results}
          </value>
        </assign>
      )
    case block: BlockStmt =>
      acceptsNextSibling(
        <block>
          {block.stmts.flatMap(_.accept(this).results)}
        </block>
      )
    case callFunction: CallByNameRefExpr =>
      acceptsNextSibling(
        <call>
          <name>
            {callFunction.functionName.toString}
          </name>
          <parameters>
            {callFunction.parameters.flatMap(_.accept(this).results)}
          </parameters>
        </call>
      )
    case callFunction: CallByExprRefExpr =>
      acceptsNextSibling(
        <call>
          <callable>
            {callFunction.callable.accept(this).results}
          </callable>
          <parameters>
            {callFunction.parameters.flatMap(_.accept(this).results)}
          </parameters>
        </call>
      )
    case callMethod: CallMethodRefExpr =>
      acceptsNextSibling(
        <call-method>
          <instance>
            {callMethod.instanceExpr.accept(this).results}
          </instance>
          <name>
            {callMethod.methodName.accept(this).results}
          </name>
          <parameters>
            {callMethod.parameters.flatMap(_.accept(this).results)}
          </parameters>
        </call-method>
      )
    case classDecl: ClassDeclStmt =>
      acceptsNextSibling(
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
        }}{classDecl.decls.flatMap(_.accept(this).results)}
        </class>
      )
    case classMethodDecl: ClassMethodDecl =>
      acceptsNextSibling(
        <method line={classMethodDecl.position.line.toString} file={classMethodDecl.position.fileName}>
          <name>
            {classMethodDecl.name}
          </name>{classMethodDecl.parameterDecls.flatMap(_.accept(this).results)}{classMethodDecl.stmts.toSeq.flatMap(_.flatMap(_.accept(this).results))}
        </method>
      )
    case dynamicName: DynamicName =>
      acceptsNextSibling(
        <dynamic-name>
          {dynamicName.expr.accept(this).results}
        </dynamic-name>
      )
    case echoStmt: EchoStmt =>
      acceptsNextSibling(
        <echo line={echoStmt.position.line.toString} file={echoStmt.position.fileName}>
          {echoStmt.parameters.flatMap(_.accept(this).results)}
        </echo>
      )
    case forStmt: ForStmt =>
      acceptsNextSibling(
        <for line={forStmt.position.line.toString} file={forStmt.position.fileName}>
          <start>
            {forStmt.befores.flatMap(_.accept(this).results)}
          </start>
          <cond>
            {forStmt.conditions.flatMap(_.accept(this).results)}
          </cond>
          <end>
            {forStmt.afters.flatMap(_.accept(this).results)}
          </end>
          <stmts>
            {forStmt.stmts.flatMap(_.accept(this).results)}
          </stmts>
        </for>
      )
    case inline: InlineStmt =>
      acceptsNextSibling(
        <inline line={inline.position.line.toString} file={inline.position.fileName}>
          {PCData(inline.text)}
        </inline>
      )
    case interfaceDecl: InterfaceDeclStmt =>
      acceptsNextSibling(
        <class line={interfaceDecl.position.line.toString} file={interfaceDecl.position.fileName}>
          <name>
            {interfaceDecl.name}
          </name>{interfaceDecl.superInterfaces.map {
          ext =>
            <extends>
              {ext}
            </extends>
        }}{interfaceDecl.decls.flatMap(_.accept(this).results)}
        </class>
      )
    case newRefExpr: NewRefExpr =>
      acceptsNextSibling(
        <new>
          {newRefExpr.className.accept(this).results}
        </new>
      )
    case exprStmt: ExprStmt =>
      acceptsNextSibling(
        <expr line={exprStmt.position.line.toString} file={exprStmt.position.fileName}>
          {exprStmt.expr.accept(this).results}
        </expr>
      )
    case parameterDecl: ParameterDecl =>
      acceptsNextSibling(
        <parameter>
          <name>
            {parameterDecl.name}
          </name>{parameterDecl.typeHint.map {
          typeHint =>
            <typeHine>
              {typeHint}
            </typeHine>
        }.getOrElse(NodeSeq.Empty)}{parameterDecl.defaultExpr.map {
          defaultExpr =>
            <default>
              {defaultExpr.accept(this).results}
            </default>
        }.getOrElse(NodeSeq.Empty)}
        </parameter>
      )
    case prog: Prog =>
      acceptsNextSibling(
        <script>
          {prog.stmts.flatMap(_.accept(this).results)}
        </script>
      )
    case staticName: StaticName =>
      acceptsNextSibling(
        Text(staticName.name)
      )
    case staticNamespaceName: StaticNamespaceName =>
      acceptsNextSibling(
        Text(staticNamespaceName.namespaceName.toString)
      )
    case variable: VariableRefExpr =>
      acceptsNextSibling(
        <variable>
          {variable.variableName.accept(this).results}
        </variable>
      )
    case node =>
      acceptsNextChild(<node/>.copy(label = node.getClass.getSimpleName))
  }

  def dump(node: Node): String = {
    val pp = new scala.xml.PrettyPrinter(80, 2)
    val nodesBuilder = NodeSeq.newBuilder
    node.accept(this).results.foreach {
      nodes =>
        nodesBuilder ++= nodes
    }
    pp.formatNodes(nodesBuilder.result())
  }
}
