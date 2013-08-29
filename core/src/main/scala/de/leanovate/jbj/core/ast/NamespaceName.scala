/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast

import de.leanovate.jbj.core.runtime.context.Context

case class NamespaceName(relative: Boolean, path: String*) {
  lazy val lowercase = path.map(_.toLowerCase)

  def absolute(implicit ctx: Context) = this

  override def toString =
    path.mkString("\\")
}

object NamespaceName {
  def apply(name: String): NamespaceName = NamespaceName(relative = name.startsWith("\\"), name.split( """\\"""): _*)

  def unapply(namespaceName:NamespaceName) = Some(namespaceName.lowercase.mkString("\\"))
}