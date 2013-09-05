/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.decl

import de.leanovate.jbj.core.ast.{HasNodePosition, Node}
import de.leanovate.jbj.runtime.value.ObjectVal
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.PClass

trait ClassMemberDecl extends Node with HasNodePosition {
  def initializeInterface(pInterface: InterfaceDeclStmt)(implicit ctx:Context) {}

  def initializeClass(pClass: ClassDeclStmt)(implicit ctx: Context) {}

  def initializeStatic(pClass: ClassDeclStmt, staticClassObject:ObjectVal)(implicit ctx:Context) {}

  def initializeInstance(instance: ObjectVal, pClass: PClass)(implicit ctx: Context) {}
}
