package de.leanovate.jbj.core.runtime.exception

import de.leanovate.jbj.core.runtime.value.ObjectVal
import de.leanovate.jbj.core.ast.NodePosition
import de.leanovate.jbj.core.runtime.context.Context
import de.leanovate.jbj.api.JbjException

case class RuntimeJbjException(exception: ObjectVal)(implicit ctx: Context, position: NodePosition)
  extends JbjException(exception.getProperty("message", None).map(_.asVal.toStr.asString).getOrElse("")) {

}
