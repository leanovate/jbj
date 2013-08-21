package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.ReferableExpr
import de.leanovate.jbj.runtime.Reference
import de.leanovate.jbj.runtime.value.{NullVal, ArrayVal, PAny}
import de.leanovate.jbj.runtime.context.Context

case class ListReferableExpr(references: List[Option[ReferableExpr]]) extends ReferableExpr {
  override def eval(implicit ctx: Context) = throw new RuntimeException("List can only be used in assignment")

  override def evalRef(implicit ctx: Context) = new Reference {
    def asVal = throw new RuntimeException("List can only be used in assignment")

    def asVar = throw new RuntimeException("List can only be used in assignment")

    def assign(pAny: PAny) = {
      pAny.asVal match {
        case array: ArrayVal =>
          references.zipWithIndex.reverse.foreach {
            case (Some(reference), idx) =>
              val elem = array.getAt(idx.toLong).getOrElse {
                ctx.log.notice("Undefined offset: %d".format(idx))
                NullVal
              }
              reference.evalRef.assign(elem)
            case _ =>
          }
        case _ =>
          unset()
      }
      pAny
    }

    def unset() {
      references.foreach {
        reference => reference.foreach(_.evalRef.unset())
      }
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
