/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, ReferableExpr}
import de.leanovate.jbj.core.runtime.value.{PVal, PVar, PAny, NullVal}
import de.leanovate.jbj.core.runtime.exception.FatalErrorJbjException
import de.leanovate.jbj.core.runtime.context.{StaticMethodContext, MethodContext, Context}
import de.leanovate.jbj.core.runtime.Reference

case class StaticClassVarReferableExpr(className: Name, variableName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    val name = className.evalNamespaceName
    ctx.global.findClass(name, autoload = false).map {
      pClass =>
        val name = variableName.evalName
        val staticClassObject = ctx.global.staticClassObject(pClass)
        ctx match {
          case MethodContext(inst, pMethod, _) =>
            staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse(NullVal)
          case StaticMethodContext(pMethod, _) =>
            staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse(NullVal)
          case _ =>
            staticClassObject.getProperty(name, None).map(_.asVal).getOrElse(NullVal)
        }
    }.getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val cname = className.evalNamespaceName
    var name = variableName.evalName
    val pClass = ctx.global.findClass(cname, autoload = false).getOrElse {
      throw new FatalErrorJbjException("Class '%s' not found".format(name.toString))
    }
    val staticClassObject = ctx.global.staticClassObject(pClass)

    override def isDefined = !asVal.isNull

    override def asVal = ctx match {
      case MethodContext(_, pMethod, _) =>
        staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse(NullVal)
      case StaticMethodContext(pMethod, _) =>
        staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map(_.asVal).getOrElse(NullVal)
      case _ =>
        staticClassObject.getProperty(name, None).map(_.asVal).getOrElse(NullVal)
    }

    override def asVar = ctx match {
      case MethodContext(_, pMethod, _) =>
        staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map {
          case pVar: PVar => pVar
          case value: PVal =>
            val result = PVar(value)
            staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), result)
            result
        }.getOrElse {
          val result = PVar()
          staticClassObject.setProperty(name, None, result)
          result
        }
      case StaticMethodContext(pMethod, _) =>
        staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map {
          case pVar: PVar => pVar
          case value: PVal =>
            val result = PVar(value)
            staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), result)
            result
        }.getOrElse {
          val result = PVar()
          staticClassObject.setProperty(name, None, result)
          result
        }
      case _ =>
        staticClassObject.getProperty(name, None).map {
          case pVar: PVar => pVar
          case value: PVal =>
            val result = PVar(value)
            staticClassObject.setProperty(name, None, result)
            result
        }.getOrElse {
          val result = PVar()
          staticClassObject.setProperty(name, None, result)
          result
        }
    }


    override def assign(pAny: PAny)(implicit ctx: Context) = {
      pAny match {
        case pVar: PVar =>
          ctx match {
            case MethodContext(_, pMethod, _) =>
              staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), pVar)
            case StaticMethodContext(pMethod, _) =>
              println("Da")
              staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), pVar)
            case _ =>
              staticClassObject.setProperty(name, None, pVar)
          }
        case pVal: PVal =>
          ctx match {
            case MethodContext(_, pMethod, _) =>
              staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map {
                case pVar: PVar => pVar.value = pVal
                case _ =>
                  staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), pVal)
              }.getOrElse {
                staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), pVal)
              }
            case StaticMethodContext(pMethod, _) =>
              staticClassObject.getProperty(name, Some(pMethod.declaringClass.name.toString)).map {
                case pVar: PVar => pVar.value = pVal
                case _ =>
                  staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), pVal)
              }.getOrElse {
                staticClassObject.setProperty(name, Some(pMethod.declaringClass.name.toString), pVal)
              }
            case _ =>
              staticClassObject.getProperty(name, None).map {
                case pVar: PVar => pVar.value = pVal
                case _ =>
                  staticClassObject.setProperty(name, None, pVal)
              }.getOrElse {
                staticClassObject.setProperty(name, None, pVal)
              }
          }
      }
      pAny
    }

    override def unset() {
      ctx match {
        case MethodContext(_, pMethod, _) =>
          staticClassObject.unsetProperty(name, Some(pMethod.declaringClass.name.toString))
        case StaticMethodContext(pMethod, _) =>
          staticClassObject.unsetProperty(name, Some(pMethod.declaringClass.name.toString))
        case _ =>
          staticClassObject.unsetProperty(name, None)
      }
    }
  }
}
