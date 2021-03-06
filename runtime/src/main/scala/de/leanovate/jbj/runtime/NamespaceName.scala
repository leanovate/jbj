/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context

case class NamespaceName(relative: Boolean, prefixed: Boolean, path: String*) {
  lazy val lowercase = path.map(_.toLowerCase)

  def lastPath = if (path.isEmpty) "" else path(path.length - 1)

  def absolute(implicit ctx: Context) = {
    if (relative) {
      if (!path.isEmpty && ctx.global.namespaceAliases.contains(path(0))) {
        NamespaceName(relative = false, prefixed = false, ctx.global.namespaceAliases(path(0)).path ++ path.drop(1): _ *)
      } else if (!path.startsWith(ctx.global.currentNamespace.path) &&
        !ctx.global.currentNamespace.path.startsWith(path)) {
        NamespaceName(relative = false, prefixed = false, ctx.global.currentNamespace.path ++ path: _ *)
      } else
        this
    } else if (prefixed) {
      NamespaceName(relative = false, prefixed = false, ctx.global.currentNamespace.path ++ path: _ *)
    } else {
      this
    }
  }

  def absolutePrefix(implicit ctx: Context) = {
    if (relative) {
      if (!path.isEmpty && ctx.global.namespaceAliases.contains(path(0))) {
        NamespaceName(relative = false, prefixed = false, ctx.global.namespaceAliases(path(0)).path ++ path.drop(1): _ *)
      } else {
        NamespaceName(relative = false, prefixed = false, ctx.global.currentNamespace.path ++ path: _ *)
      }
    } else if (prefixed) {
      NamespaceName(relative = false, prefixed = false, ctx.global.currentNamespace.path ++ path: _ *)
    } else {
      this
    }
  }

  override def toString =
    path.mkString("\\")
}

object NamespaceName {
  def apply(name: String): NamespaceName = if (name.startsWith("\\")) {
    NamespaceName(relative = false, prefixed = false, name.drop(1).split( """\\"""): _*)
  } else {
    NamespaceName(relative = true, prefixed = false, name.split( """\\"""): _*)
  }

  def unapply(namespaceName: NamespaceName) = Some(namespaceName.lowercase.mkString("\\"))
}