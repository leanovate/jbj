package de.leanovate.jbj.ast.expr

import de.leanovate.jbj.ast.{ReferableExpr, Expr}
import de.leanovate.jbj.runtime.value.ArrayVal
import de.leanovate.jbj.runtime.context.Context
import scala.xml.NodeSeq

case class ArrayKeyValue(key: Option[Expr], value: Expr, isRef: Boolean) {
  def toXml = Seq(
  {
    key.map {
      key =>
        <key>
          {key.toXml}
        </key>
    }.orNull
  }, if (isRef) {
    <ref>
      {value.toXml}
    </ref>
  } else {
    <value>
      {value.toXml}
    </value>
  })
}

case class ArrayCreateExpr(keyValueExprs: List[ArrayKeyValue]) extends Expr {
  override def eval(implicit ctx: Context) = {
    ArrayVal(keyValueExprs.map {
      case ArrayKeyValue(keyExpr, valueExpr: ReferableExpr, true) => (keyExpr.map(_.evalOld), valueExpr.evalRef.asVar)
      case ArrayKeyValue(keyExpr, valueExpr: Expr, _) => (keyExpr.map(_.evalOld), valueExpr.evalOld)
    }: _*)
  }

  override def toXml =
    <ArrayKeyValue>
      {keyValueExprs.map(_.toXml)}
    </ArrayKeyValue>
}
