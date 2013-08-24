package de.leanovate.jbj.core.ast


trait NodeVisitor[R] {

  import NodeVisitor.Result

  case class NextChild(results: List[R]) extends Result[R] {
    def prepend(prevResults: List[R]) = NextChild(prevResults ++ results)

    def thenChild(child: Node): Result[R] = {
      child.visit(NodeVisitor.this).prepend(results)
    }

    def thenChild(optChild: Option[Node]): Result[R] =
      optChild.map(thenChild).getOrElse(this)

    def thenChildren(children: Seq[Node]): Result[R] =
      children.foldLeft(this.asInstanceOf[Result[R]]) {
        (prev, child) =>
          prev.thenSibling(child)
      }

    def thenSibling(sibling: Node): Result[R] = {
      sibling.visit(NodeVisitor.this).prepend(results)
    }
  }

  object NextChild {
    def apply(results: R*): NextChild = NextChild(List(results: _*))
  }

  case class NextSibling(results: List[R]) extends Result[R] {
    def prepend(prevResults: List[R]) = NextSibling(prevResults ++ results)

    def thenChild(child: Node): Result[R] = this

    def thenChild(optChild: Option[Node]): Result[R] = this

    def thenChildren(children: Seq[Node]): Result[R] = this

    def thenSibling(sibling: Node): Result[R] = {
      sibling.visit(NodeVisitor.this).prepend(results)
    }
  }

  object NextSibling {
    def apply(results: R*): NextSibling = NextSibling(List(results: _*))
  }

  case class Abort(results: List[R]) extends Result[R] {
    def prepend(prevResults: List[R]) = Abort(prevResults ++ results)

    def thenChild(child: Node): Result[R] = this

    def thenChild(optChild: Option[Node]): Result[R] = this

    def thenChildren(children: Seq[Node]): Result[R] = this

    def thenSibling(child: Node): Result[R] = this
  }

  object Abort {
    def apply(results: R*): Abort = Abort(List(results: _*))
  }

  def apply(node: Node): Result[R]
}

object NodeVisitor {

  sealed trait Result[R] {
    def results: List[R]

    def prepend(prevResults: List[R]): Result[R]

    def thenChild(child: Node): Result[R]

    def thenSibling(child: Node): Result[R]

    def thenChild(optChild: Option[Node]): Result[R]

    def thenChildren(children: Seq[Node]): Result[R]
  }

}