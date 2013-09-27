/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context

case class NamespaceName(relative: Boolean, path: String*) {
  lazy val lowercase = path.map(_.toLowerCase)

  def absolute(implicit ctx: Context) = {
    if (relative) {
      NamespaceName(relative = false, ctx.global.currentNamespace.path ++ path: _ *)
    } else
      this
  }

  override def toString =
    path.mkString("\\")
}

object NamespaceName {
  def apply(name: String): NamespaceName = NamespaceName(relative = name.startsWith("\\"), name.split( """\\"""): _*)

  def unapply(namespaceName: NamespaceName) = Some(namespaceName.lowercase.mkString("\\"))
}