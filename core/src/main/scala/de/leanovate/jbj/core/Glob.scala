package de.leanovate.jbj.core

import de.leanovate.jbj.runtime.adapter.{OptionParameterAdapter, StringConverter, GlobalFunctions}

import scala.reflect.runtime.universe._
import de.leanovate.jbj.runtime.annotations.GlobalFunction
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{PValParam, PParam}
import de.leanovate.jbj.runtime.value.{IntegerVal, StringVal}

object Glob {
  @GlobalFunction
  def hurra(name: String, idx: Int)(implicit ctx: Context) {
    println(">>> Urra " + name + " " + idx + " " + ctx)
  }

  def main(args: Array[String]) {
    val jbj = JbjEnv(errorStream = Some(System.err))
    implicit val context = jbj.newGlobalContext(System.out)

    val parameters = List.empty[PParam]
    val adapter = OptionParameterAdapter(StringConverter)
    println(showRaw(reify {
      val param1 = adapter.adapt(parameters).getOrElse {
        return
      }
    }))

    val func = GlobalFunctions.functions(this)

    func(0).call(PValParam(StringVal("Hurra")) :: PValParam(IntegerVal(1234)) :: Nil)
    func(0).call(PValParam(StringVal("Hurra")) ::  Nil)
  }
}
