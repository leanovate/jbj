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

class AstAsXmlNodeVisitor extends NodeVisitor[NodeSeq] {
  val nodes = NodeSeq.newBuilder

  def result = nodes.result()

  def visit = {
    case assign: AssignRefExpr =>
      nodes +=
        <assign>
          <to>
            {assign.reference.foldWith(new AstAsXmlNodeVisitor)}
          </to>
          <value>
            {assign.expr.foldWith(new AstAsXmlNodeVisitor)}
          </value>
        </assign>
      acceptsNextSibling
    case block: BlockStmt =>
      nodes +=
        <block>
          {block.stmts.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
        </block>
      acceptsNextSibling
    case callFunction: CallByNameRefExpr =>
      nodes +=
        <call>
          <name>
            {callFunction.functionName.toString}
          </name>
          <parameters>
            {callFunction.parameters.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
          </parameters>
        </call>
      acceptsNextSibling
    case callFunction: CallByExprRefExpr =>
      nodes +=
        <call>
          <callable>
            {callFunction.callable.accept(new AstAsXmlNodeVisitor)}
          </callable>
          <parameters>
            {callFunction.parameters.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
          </parameters>
        </call>
      acceptsNextSibling
    case callMethod: CallMethodRefExpr =>
      nodes +=
        <call-method>
          <instance>
            {callMethod.instanceExpr.accept(new AstAsXmlNodeVisitor)}
          </instance>
          <name>
            {callMethod.methodName.accept(new AstAsXmlNodeVisitor)}
          </name>
          <parameters>
            {callMethod.parameters.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
          </parameters>
        </call-method>
      acceptsNextSibling
    case classDecl: ClassDeclStmt =>
      nodes +=
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
        }}{classDecl.decls.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
        </class>
      acceptsNextSibling
    case classMethodDecl: ClassMethodDecl =>
      nodes +=
        <method line={classMethodDecl.position.line.toString} file={classMethodDecl.position.fileName}>
          <name>
            {classMethodDecl.name}
          </name>{classMethodDecl.parameterDecls.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}{classMethodDecl.stmts.toSeq.flatMap(_.flatMap(_.foldWith(new AstAsXmlNodeVisitor)))}
        </method>
      acceptsNextSibling
    case dynamicName: DynamicName =>
      nodes +=
        <dynamic-name>
          {dynamicName.expr.accept(new AstAsXmlNodeVisitor)}
        </dynamic-name>
      acceptsNextSibling
    case echoStmt: EchoStmt =>
      nodes +=
        <echo line={echoStmt.position.line.toString} file={echoStmt.position.fileName}>
          {echoStmt.parameters.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
        </echo>
      acceptsNextSibling
    case forStmt: ForStmt =>
      nodes +=
        <for line={forStmt.position.line.toString} file={forStmt.position.fileName}>
          <start>
            {forStmt.befores.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
          </start>
          <cond>
            {forStmt.conditions.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
          </cond>
          <end>
            {forStmt.afters.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
          </end>
          <stmts>
            {forStmt.stmts.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
          </stmts>
        </for>
      acceptsNextSibling
    case inline: InlineStmt =>
      nodes +=
        <inline line={inline.position.line.toString} file={inline.position.fileName}>
          {PCData(inline.text)}
        </inline>
      acceptsNextSibling
    case interfaceDecl: InterfaceDeclStmt =>
      nodes +=
        <class line={interfaceDecl.position.line.toString} file={interfaceDecl.position.fileName}>
          <name>
            {interfaceDecl.name}
          </name>{interfaceDecl.superInterfaces.map {
          ext =>
            <extends>
              {ext}
            </extends>
        }}{interfaceDecl.decls.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
        </class>
      acceptsNextSibling
    case newRefExpr: NewRefExpr =>
      nodes +=
        <new>
          {newRefExpr.className.accept(new AstAsXmlNodeVisitor)}
        </new>
      acceptsNextSibling
    case exprStmt: ExprStmt =>
      nodes +=
        <expr line={exprStmt.position.line.toString} file={exprStmt.position.fileName}>
          {exprStmt.expr.accept(new AstAsXmlNodeVisitor)}
        </expr>
      acceptsNextSibling
    case parameterDecl: ParameterDecl =>
      nodes +=
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
              {defaultExpr.accept(new AstAsXmlNodeVisitor)}
            </default>
        }.getOrElse(NodeSeq.Empty)}
        </parameter>
      acceptsNextSibling
    case prog: Prog =>
      nodes +=
        <script>
          {prog.stmts.flatMap(_.foldWith(new AstAsXmlNodeVisitor))}
        </script>
      acceptsNextSibling
    case staticName: StaticName =>
      nodes +=
        Text(staticName.name)
      acceptsNextSibling
    case staticNamespaceName: StaticNamespaceName =>
      nodes +=
        Text(staticNamespaceName.namespaceName.toString)
      acceptsNextSibling
    case variable: VariableRefExpr =>
      nodes +=
        <variable>
          {variable.variableName.accept(new AstAsXmlNodeVisitor)}
        </variable>
      acceptsNextSibling
    case node =>
      nodes += <node/>.copy(label = node.getClass.getSimpleName)
      acceptsNextSibling
  }
}

object AstAsXmlNodeVisitor {
  def dump(node: Node): String = {
    val pp = new scala.xml.PrettyPrinter(80, 2)
    val nodesBuilder = NodeSeq.newBuilder
    node.foldWith(new AstAsXmlNodeVisitor).foreach {
      nodes =>
        nodesBuilder ++= nodes
    }
    pp.formatNodes(nodesBuilder.result())
  }
}
