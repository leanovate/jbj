/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

trait NodeVisitor[R] {

  import NodeVisitor._

  def visit: PartialFunction[Node, Action[R]]

  def result: R

  def apply(node: Node): Action[R] = visit.applyOrElse(node, {
    _: Node => abort
  })

  def acceptsNextChild: Action[R] = AcceptsNextChild(this)

  def acceptsNextSibling: Action[R] = AcceptsNextSibling(this)

  def abort: Action[R] = Abort(this)
}

object NodeVisitor {

  sealed trait Action[R] {
    def result: R

    def thenChild(child: Node): Action[R]

    def thenSibling(child: Node): Action[R]

    def thenChild(optChild: Option[Node]): Action[R]

    def thenChildren(children: Seq[Node]): Action[R]
  }

  case class AcceptsNextChild[R](visitor: NodeVisitor[R]) extends Action[R] {
    def result = visitor.result

    def thenChild(child: Node): Action[R] = child.accept(visitor)

    def thenChild(optChild: Option[Node]): Action[R] = optChild.map(thenChild).getOrElse(this)

    def thenChildren(children: Seq[Node]): Action[R] = children.foldLeft(this.asInstanceOf[Action[R]]) {
      (prev, child) =>
        prev.thenSibling(child)
    }

    def thenSibling(sibling: Node): Action[R] = sibling.accept(visitor)
  }

  case class AcceptsNextSibling[R](visitor: NodeVisitor[R]) extends Action[R] {
    def result = visitor.result

    def thenChild(child: Node): Action[R] = this

    def thenChild(optChild: Option[Node]): Action[R] = this

    def thenChildren(children: Seq[Node]): Action[R] = this

    def thenSibling(sibling: Node): Action[R] = sibling.accept(visitor)
  }

  case class Abort[R](visitor: NodeVisitor[R]) extends Action[R] {
    def result = visitor.result

    def thenChild(child: Node): Action[R] = this

    def thenChild(optChild: Option[Node]): Action[R] = this

    def thenChildren(children: Seq[Node]): Action[R] = this

    def thenSibling(child: Node): Action[R] = this
  }

}