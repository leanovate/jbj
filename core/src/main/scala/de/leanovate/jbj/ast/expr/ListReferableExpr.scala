package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.ReferableExpr
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.{NullVal, ArrayVal, PAny}
import de.leanovate.jbj.runtime.context.Context

case class ListReferableExpr(references: List[Option[ReferableExpr]]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = throw new RuntimeException("List can only be used in assignment")

  override def evalRef(implicit ctx: Context) = new Reference {
    val refs = references.map(_.map(_.evalRef)).toSeq

    def asVal = throw new RuntimeException("List can only be used in assignment")

    def asVar = throw new RuntimeException("List can only be used in assignment")

    def assign(pAny: PAny) = {
      pAny.asVal match {
        case array: ArrayVal =>
          // this is a bit sub-optimal, but it seems to be the order the original engine likes
          var values = Range(0, refs.size).reverse.map {
            idx =>
              array.getAt(idx.toLong).getOrElse {
                ctx.log.notice("Undefined offset: %d".format(idx))
                NullVal
              }
          }.toList
          refs.foreach {
            ref =>
              ref.foreach(_.assign(values.last))
              values = values.dropRight(1)
          }
        case _ =>
          refs.foreach(_.foreach(_.assign(NullVal)))
      }
      pAny
    }

    def unset() {
      refs.foreach(_.foreach(_.unset()))
    }
  }

  override def toXml =
    <ListReferableExpr>
      {references.map {
      case Some(reference) => reference.toXml
      case None => <empty/>
    }}
    </ListReferableExpr>
}
