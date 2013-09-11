/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.types.{PClass, PStdClass, PValParam}
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.context.StaticMethodContext
import scala.Some
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

class PropReference(parentRef: Reference, name: String)(implicit ctx: Context) extends Reference {
  override def isConstant = false

  def isDefined = {
    if (parentRef.isDefined) {
      parentRef.byVal.concrete match {
        case obj: ObjectVal =>
          if ((ctx match {
            case MethodContext(_, pMethod, _) =>
              obj.getProperty(name, Some(pMethod.declaringClass.name.toString))
            case StaticMethodContext(pMethod, _) =>
              obj.getProperty(name, Some(pMethod.declaringClass.name.toString))
            case _ =>
              obj.getProperty(name, None)
          }).isDefined) {
            true
          } else {
            obj.pClass.findMethod("__get").isDefined
          }
        case _ => false
      }
    } else {
      false
    }
  }

  def byVal = {
    parentRef.byVal.concrete match {
      case obj: ObjectVal =>
        checkShadowedStatic(obj.pClass, name)
        ctx match {
          case MethodContext(_, pMethod, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse {
              if (pMethod.name == "__get") {
                ctx.log.notice("Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                NullVal
              } else {
                obj.pClass.findMethod("__get").map(_.invoke(ctx, obj, PValParam(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                  ctx.log.notice("Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                  NullVal
                }
              }
            }
          case StaticMethodContext(pMethod, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse {
              obj.pClass.findMethod("__get").map(_.invoke(ctx, obj, PValParam(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                ctx.log.notice("Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                NullVal
              }
            }
          case _ =>
            obj.getProperty(name, None).map(_.asVal).getOrElse {
              obj.pClass.findMethod("__get").map(_.invoke(ctx, obj, PValParam(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                ctx.log.notice("Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                NullVal
              }
            }
        }
      case _ =>
        ctx.log.notice("Trying to get property of non-object")
        NullVal
    }
  }

  def byVar = {
    optParent(withWarn = false) match {
      case Some(obj) =>
        ctx match {
          case MethodContext(_, pMethod, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).map {
              case pVar: PVar => pVar
              case value: PVal =>
                val result = PVar(value)
                obj.setProperty(name, Some(pMethod.declaringClass.name.toString), result)
                result
            }.getOrElse {
              val result = PVar()
              obj.setProperty(name, Some(pMethod.declaringClass.name.toString), result)
              result
            }
          case StaticMethodContext(pMethod, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).map {
              case pVar: PVar => pVar
              case value: PVal =>
                val result = PVar(value)
                obj.setProperty(name, None, result)
                result
            }.getOrElse {
              val result = PVar()
              obj.setProperty(name, None, result)
              result
            }
          case _ =>
            obj.getProperty(name, None).map {
              case pVar: PVar => pVar
              case value: PVal =>
                val result = PVar(value)
                obj.setProperty(name, None, result)
                result
            }.getOrElse {
              val result = PVar()
              obj.setProperty(name, None, result)
              result
            }
        }
      case None =>
        ctx.log.notice("Trying to get property of non-object")
        PVar(NullVal)
    }
  }

  def assign(pAny: PAny)(implicit ctx: Context) = {
    optParent(withWarn = true) match {
      case Some(obj) =>
        checkShadowedStatic(obj.pClass, name)
        ctx match {
          case MethodContext(inst, pMethod, _) =>
            if (obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).isDefined) {
              obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny.asVal)
            } else {
              if (inst.pClass == obj.pClass && pMethod.name == "__set") {
                obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny.asVal)
              } else {
                obj.pClass.findMethod("__set").map(_.invoke(ctx, obj, PValParam(StringVal(name)) :: PValParam(pAny.asVal) :: Nil)).getOrElse {
                  obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny.asVal)
                }
              }
            }
          case StaticMethodContext(pMethod, _) =>
            if (obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).isDefined) {
              obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny.asVal)
            } else {
              obj.pClass.findMethod("__set").map(_.invoke(ctx, obj, PValParam(StringVal(name)) :: PValParam(pAny.asVal) :: Nil)).getOrElse {
                obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny.asVal)
              }
            }
          case _ =>
            if (obj.getProperty(name, None).isDefined) {
              obj.setProperty(name, None, pAny.asVal)
            } else {
              obj.pClass.findMethod("__set").map(_.invoke(ctx, obj, PValParam(StringVal(name)) :: PValParam(pAny.asVal) :: Nil)).getOrElse {
                obj.setProperty(name, None, pAny.asVal)
              }
            }
        }
      case None =>
        ctx.log.warn("Attempt to assign property of non-object")
    }
    pAny
  }

  def unset() {
    optParent(withWarn = false) match {
      case Some(obj) =>
        checkShadowedStatic(obj.pClass, name)
        ctx match {
          case MethodContext(_, pMethod, _) =>
            obj.unsetProperty(name, Some(pMethod.declaringClass.name.toString))
          case StaticMethodContext(pMethod, _) =>
            obj.unsetProperty(name, Some(pMethod.declaringClass.name.toString))
          case _ =>
            obj.unsetProperty(name, None)
        }
      case _ =>
    }
  }

  private def optParent(withWarn: Boolean) =
    if (!parentRef.isDefined) {
      if (withWarn)
        ctx.log.warn("Creating default object from empty value")
      val obj = PStdClass.newInstance(Nil)(ctx)
      parentRef.byVar.value = obj
      Some(obj)
    } else {
      parentRef.byVal.concrete match {
        case obj: ObjectVal =>
          Some(obj)
        case NullVal =>
          if (withWarn)
            ctx.log.warn("Creating default object from empty value")
          val obj = PStdClass.newInstance(Nil)(ctx)
          parentRef.byVar.value = obj
          Some(obj)
        case _ =>
          None
      }
    }

  private def checkShadowedStatic(pClass: PClass, name: String)(implicit ctx: Context) {
    ctx match {
      case MethodContext(_, pMethod, _) =>
        if (ctx.global.staticClassObject(pMethod.declaringClass).getProperty(name, Some(pMethod.declaringClass.name.toString)).isDefined)
          ctx.log.strict("Accessing static property %s::$%s as non static".format(pClass.name.toString, name))
      case _ =>
        val staticClassObject = ctx.global.staticClassObject(pClass)
        if (staticClassObject.getProperty(name, None).isDefined)
          ctx.log.strict("Accessing static property %s::$%s as non static".format(pClass.name.toString, name))
        else if (staticClassObject.getProperty(name, Some(pClass.name.toString)).isDefined)
          throw new FatalErrorJbjException("Cannot access protected property %s::$%s".format(pClass.name.toString, name))
    }
  }
}
