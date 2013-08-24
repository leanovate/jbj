package de.leanovate.jbj.core.ast.expr

import de.leanovate.jbj.core.ast.{Name, ReferableExpr}
import de.leanovate.jbj.core.runtime.Reference
import de.leanovate.jbj.core.runtime.value._
import java.io.PrintStream
import de.leanovate.jbj.core.runtime.value.StringVal
import de.leanovate.jbj.core.runtime.context.{Context, MethodContext}
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import de.leanovate.jbj.core.runtime.buildin.StdClass

case class PropertyReferableExpr(reference: ReferableExpr, propertyName: Name) extends ReferableExpr {
  override def eval(implicit ctx: Context) = {
    reference.eval.asVal match {
      case obj: ObjectVal =>
        val name = propertyName.evalName
        ctx match {
          case MethodContext(inst, pClass, methodName, _) =>
            obj.getProperty(name, Some(pClass.name.toString)).map(_.asVal).getOrElse {
              if (inst.pClass == obj.pClass && methodName == "__get") {
                ctx.log.notice("Undefined property: %s::%s".format(obj.pClass.name.toString, name))
                NullVal
              } else {
                obj.pClass.findMethod("__get").map(_.invoke(ctx, obj, obj.pClass, ScalarExpr(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                  ctx.log.notice("Undefined property: %s::%s".format(obj.pClass.name.toString, name))
                  NullVal
                }
              }
            }
          case _ =>
            obj.getProperty(name, None).map(_.asVal).getOrElse {
              obj.pClass.findMethod("__get").map(_.invoke(ctx, obj, obj.pClass, ScalarExpr(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                ctx.log.notice("Undefined property: %s::%s".format(obj.pClass.name.toString, name))
                NullVal
              }
            }
        }
      case _ =>
        ctx.log.notice("Trying to get property of non-object")
        NullVal
    }
  }

  override def evalRef(implicit ctx: Context) = new Reference {
    val parentRef = reference.evalRef
    val name = propertyName.evalName

    def isDefined = {
      if (reference.isDefined) {
        reference.eval.asVal match {
          case obj: ObjectVal =>
            val name = propertyName.evalName
            if ((ctx match {
              case MethodContext(_, pClass, _, _) =>
                obj.getProperty(name, Some(pClass.name.toString))
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

    def asVal = {
      parentRef.asVal match {
        case obj: ObjectVal =>
          val name = propertyName.evalName
          ctx match {
            case MethodContext(_, pClass, methodName, _) =>
              obj.getProperty(name, Some(pClass.name.toString)).map(_.asVal).getOrElse {
                if (methodName == "__get") {
                  NullVal
                } else {
                  obj.pClass.findMethod("__get").map(_.invoke(ctx, obj, obj.pClass, ScalarExpr(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                    NullVal
                  }
                }
              }
            case _ =>
              obj.getProperty(name, None).map(_.asVal).getOrElse {
                obj.pClass.findMethod("__get").map(_.invoke(ctx, obj, obj.pClass, ScalarExpr(StringVal(name)) :: Nil)).map(_.asVal).getOrElse {
                  NullVal
                }
              }
          }
        case _ =>
          NullVal
      }
    }

    def asVar = {
      optParent(false) match {
        case Some(obj) =>
          ctx match {
            case MethodContext(_, pClass, _, _) =>
              obj.getProperty(name, Some(pClass.name.toString)).map {
                case pVar: PVar => pVar
                case value: PVal =>
                  val result = PVar(value)
                  obj.setProperty(name, Some(pClass.name.toString), result)
                  result
              }.getOrElse {
                val result = PVar()
                obj.setProperty(name, Some(pClass.name.toString), result)
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

    def assign(pAny: PAny) = {
      optParent(true) match {
        case Some(obj) =>
          ctx match {
            case MethodContext(inst, pClass, methodName, _) =>
              if (obj.getProperty(name, Some(pClass.name.toString)).isDefined) {
                obj.setProperty(propertyName.evalName, Some(pClass.name.toString), pAny.asVal)
              } else {
                if (inst.pClass == obj.pClass && methodName == "__set") {
                  obj.setProperty(name, Some(pClass.name.toString), pAny.asVal)
                } else {
                  obj.pClass.findMethod("__set").map(_.invoke(ctx, obj, obj.pClass, ScalarExpr(StringVal(name)) :: ScalarExpr(pAny.asVal) :: Nil)).getOrElse {
                    obj.setProperty(name, Some(pClass.name.toString), pAny.asVal)
                  }
                }
              }
            case _ =>
              if (obj.getProperty(name, None).isDefined) {
                obj.setProperty(propertyName.evalName, None, pAny.asVal)
              } else {
                obj.pClass.findMethod("__set").map(_.invoke(ctx, obj, obj.pClass, ScalarExpr(StringVal(name)) :: ScalarExpr(pAny.asVal) :: Nil)).getOrElse {
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
      if (optParent(false).isDefined) {
        ctx match {
          case MethodContext(_, pClass, _, _) =>
            optParent(false).get.unsetProperty(name, Some(pClass.name.toString))
          case _ =>
            optParent(false).get.unsetProperty(name, None)
        }
      }
    }

    private def optParent(withWarn: Boolean) =
      parentRef.asVal match {
        case obj: ObjectVal =>
          Some(obj)
        case NullVal =>
          if (withWarn)
            ctx.log.warn("Creating default object from empty value")
          val obj = StdClass.newInstance(Nil)(ctx)
          parentRef.asVar.asVar.value = obj
          Some(obj)
        case _ =>
          None
      }
  }

  override def dump(out: PrintStream, ident: String) {
    super.dump(out, ident)
    reference.dump(out, ident + "  ")
    propertyName.dump(out, ident + "  ")
  }
}
