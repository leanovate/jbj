/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                 (Tests based on PHP)   */

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
    case echoStmt: EchoStmt =>
      NextSibling(
        <echo>
          {echoStmt.parameters.flatMap(_.visit(this).results)}
        </echo>
      )
    case forStmt: ForStmt =>
      NextSibling(
        <for>
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
    case exprStmt: ExprStmt =>
      NextSibling(
        <expr line={exprStmt.position.line.toString} file={exprStmt.position.fileName}>
          {exprStmt.expr.visit(this).results}
        </expr>
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
