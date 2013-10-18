/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime

import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.value._
import de.leanovate.jbj.runtime.types.{PAnyParam, PClass, PStdClass}
import de.leanovate.jbj.runtime.context.MethodContext
import de.leanovate.jbj.runtime.context.StaticMethodContext
import scala.Some
import de.leanovate.jbj.runtime.exception.FatalErrorJbjException

class PropReference(parentRef: Reference, name: String)(implicit ctx: Context) extends Reference {
  private var _getResult: Option[PVal] = None

  override def isConstant = false

  override def isDefined = {
    if (parentRef.isDefined) {
      parentRef.byVal.concrete match {
        case obj: ObjectVal =>
          if ((ctx match {
            case MethodContext(_, pMethod, _) =>
              obj.getProperty(name, Some(pMethod.declaringClass.name.toString))
            case StaticMethodContext(pMethod, _, _) =>
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

  override def byVal = {
    parentRef.byVal.concrete match {
      case obj: ObjectVal =>
        checkShadowedStatic(obj.pClass, name)
        ctx match {
          case MethodContext(_, pMethod, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse {
              if (obj.hasPrivateProperty(name))
                throw new FatalErrorJbjException("Cannot access private property %s::$%s".format(obj.pClass.name.toString, name))
              if (pMethod.name == "__get") {
                ctx.log.notice("Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                NullVal
              } else {
                obj.pClass.findMethod("__get").map(_.invoke(obj, PAnyParam(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                  ctx.log.notice("Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                  NullVal
                }
              }
            }
          case StaticMethodContext(pMethod, _, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse {
              obj.pClass.findMethod("__get").map(_.invoke(obj, PAnyParam(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                ctx.log.notice("Undefined property: %s::$%s".format(obj.pClass.name.toString, name))
                NullVal
              }
            }
          case _ =>
            obj.getProperty(name, None).map(_.asVal).getOrElse {
              if (obj.hasPrivateProperty(name))
                throw new FatalErrorJbjException("Cannot access private property %s::$%s".format(obj.pClass.name.toString, name))
              obj.pClass.findMethod("__get").map {
                getMethod =>
                  _getResult.getOrElse {
                    val result = getMethod.invoke(obj, PAnyParam(StringVal(name)) :: Nil).asVal
                    _getResult = Some(result)
                    result
                  }
              }.getOrElse {
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

  override def byVar = {
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
          case StaticMethodContext(pMethod, _, _) =>
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

  override def assign(pAny: PAny)(implicit ctx: Context) = {
    optParent(withWarn = !pAny.isInstanceOf[PVar]) match {
      case Some(obj) =>
        checkShadowedStatic(obj.pClass, name)
        ctx match {
          case MethodContext(inst, pMethod, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)) match {
              case Some(pVar: PVar) if pAny.isInstanceOf[PVal] =>
                pVar.value = pAny.asInstanceOf[PVal]
              case Some(_) =>
                obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny)
              case None =>
                if (inst.pClass == obj.pClass && pMethod.name == "__set") {
                  obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny)
                } else {
                  obj.pClass.findMethod("__set").map(_.invoke(obj, PAnyParam(StringVal(name)) :: PAnyParam(pAny.asVal) :: Nil)).getOrElse {
                    obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny)
                  }
                }
            }
          case StaticMethodContext(pMethod, _, _) =>
            obj.getProperty(name, Some(pMethod.declaringClass.name.toString)) match {
              case Some(pVar: PVar) if pAny.isInstanceOf[PVal] =>
                pVar.value = pAny.asInstanceOf[PVal]
              case Some(_) =>
                obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny)
              case None =>
                obj.pClass.findMethod("__set").map(_.invoke(obj, PAnyParam(StringVal(name)) :: PAnyParam(pAny.asVal) :: Nil)).getOrElse {
                  obj.setProperty(name, Some(pMethod.declaringClass.name.toString), pAny)
                }
            }
          case _ =>
            obj.getProperty(name, None) match {
              case Some(pVar: PVar) if pAny.isInstanceOf[PVal] =>
                pVar.value = pAny.asInstanceOf[PVal]
              case Some(_) =>
                obj.setProperty(name, None, pAny)
              case None =>
                if (obj.hasPrivateProperty(name))
                  throw new FatalErrorJbjException("Cannot access private property %s::$%s".format(obj.pClass.name.toString, name))
                obj.pClass.findMethod("__set").map(_.invoke(obj, PAnyParam(StringVal(name)) :: PAnyParam(pAny.asVal) :: Nil)).getOrElse {
                  obj.setProperty(name, None, pAny)
                }
            }
        }
      case None =>
        ctx.log.warn("Attempt to assign property of non-object")
    }
    pAny
  }

  override def unset() {
    optParent(withWarn = false) match {
      case Some(obj) =>
        checkShadowedStatic(obj.pClass, name)
        ctx match {
          case MethodContext(_, pMethod, _) =>
            obj.unsetProperty(name, Some(pMethod.declaringClass.name.toString))
          case StaticMethodContext(pMethod, _, _) =>
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
