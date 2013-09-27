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

  def lastPath = if (path.isEmpty) "" else path(path.length - 1)

  def absolute(implicit ctx: Context) = {
    if (relative && !path.startsWith(ctx.global.currentNamespace.path)) {
      NamespaceName(relative = false, ctx.global.currentNamespace.path ++ path: _ *)
    } else
      this
  }

  def checkCandidates[A](f: NamespaceName => Option[A])(implicit ctx: Context): Option[A] = {
    if (relative) {
      var result = Option.empty[A]

      if (!ctx.global.currentNamespace.path.isEmpty) {
        result = f(NamespaceName(relative = false, ctx.global.currentNamespace.path ++ path: _*))
      }
      if (!path.isEmpty && ctx.global.namespaceAliases.contains(path(0))) {
        result = f(NamespaceName(relative = false, ctx.global.namespaceAliases(path(0)).path ++ path.drop(1): _ *))
      }

      if (!result.isDefined)
        result = f(NamespaceName(relative = false, path: _*))
      result
    } else {
      f(this)
    }
  }

  override def toString =
    path.mkString("\\")
}

object NamespaceName {
  def apply(name: String): NamespaceName = NamespaceName(relative = name.startsWith("\\"), name.split( """\\"""): _*)

  def unapply(namespaceName: NamespaceName) = Some(namespaceName.lowercase.mkString("\\"))
}