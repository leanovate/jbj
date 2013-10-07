/*    _ _     _                                        *\
**   (_) |__ (_)  License: MIT  (2013)                 **
**   | |  _ \| |    http://opensource.org/licenses/MIT **
**   | | |_) | |                                       **
**  _/ |____// |  Author: Bodo Junglas                 **
\* |__/    |__/                                        */

package de.leanovate.jbj.runtime.context

import de.leanovate.jbj.runtime.value.{NullVal, PVar, PAny}
import de.leanovate.jbj.runtime.types.{PParamDef, PParam}

trait FunctionLikeContext extends Context {
  var functionArguments: Seq[PAny] = Seq.empty

  def functionSignature: String

  def callerContext: Context

  def setParameters(callerContext: Context, parameterDecls: List[PParamDef], parameters: List[PParam]) {
    val parameterIt = parameters.iterator
    val arguments = Seq.newBuilder[PAny]
    parameterDecls.zipWithIndex.foreach {
      case (parameterDecl, index) =>
        if (parameterIt.hasNext) {
          val param = parameterIt.next()
          if (parameterDecl.byRef) {
            val pVar = param.byRef match {
              case pVar: PVar => pVar
              case pAny =>
                callerContext.log.strict("Only variables should be passed by reference")
                pAny.asVar
            }
            checkAndDefine(parameterDecl, index, pVar)
            arguments += pVar
          } else {
            val pVal = param.byVal
            checkAndDefine(parameterDecl, index, PVar(pVal))
            arguments += pVal
          }
        } else {
          parameterDecl.default match {
            case Some(defaultGen) =>
              val pVal = defaultGen(this)
              checkAndDefine(parameterDecl, index, PVar(pVal))
              arguments += pVal
            case None =>
              checkEmpty(parameterDecl, index)
              arguments += NullVal
          }
        }
    }
    parameterIt.foreach {
      param =>
        val pVal = param.byVal
        arguments += pVal
    }
    functionArguments = arguments.result()
  }

  private def checkEmpty(parameterDecl: PParamDef, index: Int) {
    parameterDecl.typeHint.foreach(_.checkEmpty(index)(this))
  }

  private def checkAndDefine(parameterDecl: PParamDef, index: Int, pVar: PVar) {
    parameterDecl.typeHint.foreach(_.check(pVar, index)(this))
    defineVariable(parameterDecl.name, pVar)
  }
}
