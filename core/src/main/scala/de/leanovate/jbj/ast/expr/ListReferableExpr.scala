package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{NodePosition, ReferableExpr}
import de.leanovate.jbj.runtime.{Reference, Context}
import de.leanovate.jbj.runtime.value.{NullVal, ArrayVal, PAny}

case class ListReferableExpr(references: List[ReferableExpr]) extends ReferableExpr {
  override def position_=(pos: NodePosition) {
    super.position_=(pos)

    references.foreach(_.position = pos)
  }

  override def eval(implicit ctx: Context) = throw new RuntimeException("List can only be used in assignment")

  override def evalRef(implicit ctx: Context) = new Reference {
    def asVal = throw new RuntimeException("List can only be used in assignment")

    def asVar = throw new RuntimeException("List can only be used in assignment")

    def assign(pAny: PAny) = {
      pAny.asVal match {
        case array: ArrayVal =>
          references.zipWithIndex.reverse.foreach {
            case (reference, idx) =>
              val elem = array.getAt(idx.toLong).getOrElse {
                ctx.log.notice(position, "Undefined offset: %d".format(idx))
                NullVal
              }
              reference.evalRef.assign(elem)
          }
        case _ =>
          unset()
      }
      pAny
    }

    def unset() {
      references.foreach {
        reference => reference.evalRef.unset()
      }
    }
  }
}
