package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.ast.NodePosition
import de.leanovate.jbj.runtime.context.Context

case class RuntimeJbjException(exception: ObjectVal)(implicit ctx: Context, position: NodePosition)
  extends JbjException(exception.getProperty("message").map(_.asVal.toStr.asString).getOrElse("")) {

}
