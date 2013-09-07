package de.leanovate.jbj.core

import de.leanovate.jbj.runtime.adapter.{OptionParameterAdapter, StringConverter, GlobalFunctions}

import scala.reflect.runtime.universe._
import de.leanovate.jbj.runtime.annotations.{ErrorAction, GlobalFunction}
import de.leanovate.jbj.runtime.context.Context
import de.leanovate.jbj.runtime.{PFunction, PValParam, PParam}
import de.leanovate.jbj.runtime.value.{BooleanVal, IntegerVal, StringVal}

object Glob {
  @GlobalFunction(ErrorAction.WARN)
  def hurra(name: String, idx: Int)(implicit ctx: Context) {
    println(">>> Urra " + name + " " + idx + " " + ctx)
  }

  def hu(p: String*) {

  }
  val func : Seq[PFunction]= GlobalFunctions.functions(this)

  def main(args: Array[String]) {
    val jbj = JbjEnv(errorStream = Some(System.err))
    implicit val context = jbj.newGlobalContext(System.out)

    val parameters = List.empty[PParam]
    val adapter = OptionParameterAdapter(StringConverter)
    val li = List()
    println(showRaw(reify {
      hu(li: _*)
    }))


    println(func(0).call(PValParam(StringVal("Hurra")) :: PValParam(IntegerVal(1234)) :: Nil))
    println(func(0).call(PValParam(StringVal("Hurra")) :: Nil))
  }
}
