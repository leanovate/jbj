package de.leanovate.jbj.runtime.exception

import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.Context
import de.leanovate.jbj.ast.NodePosition

case class RuntimeJbjException(exception: ObjectVal)(implicit ctx: Context, position: NodePosition)
  extends JbjException(exception.getProperty("message").map(_.asVal.toStr.asString).getOrElse("")) {

}
