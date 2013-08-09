package de.leanovate.jbj.runtime.context

import java.io.PrintStream
import scala.collection.mutable
import de.leanovate.jbj.runtime._
import de.leanovate.jbj.runtime.buildin
import de.leanovate.jbj.ast.{Prog, NodePosition}
import scala.collection.immutable.Stack
import de.leanovate.jbj.JbjEnv
import de.leanovate.jbj.runtime.exception.CompileErrorException
import de.leanovate.jbj.ast.NamespaceName
import de.leanovate.jbj.runtime.value.{Value, StringVal}

case class GlobalContext(jbj: JbjEnv, out: PrintStream, err: PrintStream, settings: Settings) extends Context {
  private val classes = mutable.Map.empty[Seq[String], PClass]

  private val constants = mutable.Map.empty[ConstantKey, Value]

  private val variables = mutable.Map.empty[String, ValueRef]

  private val functions = mutable.Map.empty[Seq[String], PFunction]

  private val staticContexts = mutable.Map.empty[String, StaticContext]

  private val includedFiles = mutable.Set.empty[String]

  private val autoloading = mutable.Set.empty[String]

  def global = this

  def static = staticContext("global")

  def stack: Stack[NodePosition] = Stack.empty[NodePosition]

  def include(file: String)(implicit ctx: Context, position: NodePosition): Option[(Prog, Boolean)] = jbj.parse(file) match {
    case Some(Left(prog)) =>
      Some(prog, includedFiles.add(prog.fileName))
    case Some(Right(t)) => throw new CompileErrorException(t.getMessage)
    case None if file.startsWith("/") => None
    case None =>
      val idx = position.fileName.lastIndexOf('/')
      if (idx < 0)
        None
      else
        jbj.parse(position.fileName.substring(0, idx + 1) + file) match {
          case Some(Left(prog)) => Some(prog, includedFiles.add(prog.fileName))
          case Some(Right(t)) => throw new CompileErrorException(t.getMessage)
          case None => None
        }
  }

  def findClass(name: NamespaceName): Option[PClass] =
    buildin.buildinClasses.get(name.lowercase).map(Some.apply).getOrElse(classes.get(name.lowercase))

  def findClassOrAutoload(name: NamespaceName)(implicit position: NodePosition): Option[PClass] =
    findClass(name).map(Some.apply).getOrElse {
      findFunction(NamespaceName(relative = true, "__autoload")).flatMap {
        case autoload if !autoloading.contains(name.toString.toLowerCase) =>
          autoloading.add(name.toString.toLowerCase)
          autoload.call(this, position, StringVal(name.toString) :: Nil)
          autoloading.remove(name.toString.toLowerCase)
          findClass(name)
        case _ => None
      }
    }

  def defineClass(pClass: PClass) {
    classes.put(pClass.name.lowercase, pClass)
  }

  def findConstant(name: String): Option[Value] =
    buildin.buildinConstants.get(name.toUpperCase).map(Some.apply).getOrElse {
      constants.get(CaseSensitiveConstantKey(name)).map(Some.apply).getOrElse {
        constants.get(CaseInsensitiveConstantKey(name.toLowerCase))
      }
    }

  def defineConstant(name: String, value: Value, caseInsensitive: Boolean) {
    if (caseInsensitive)
      constants.put(CaseInsensitiveConstantKey(name.toLowerCase), value)
    else
      constants.put(CaseSensitiveConstantKey(name), value)
  }

  def findVariable(name: String)(implicit position: NodePosition): Option[ValueRef] = variables.get(name)

  def defineVariable(name: String, valueRef: ValueRef)(implicit position: NodePosition) {
    variables.put(name, valueRef)
  }

  def undefineVariable(name: String) {
    variables.remove(name)
  }

  def findFunction(name: NamespaceName) =
    buildin.buildinFunctions.get(name.lowercase).map(Some.apply).getOrElse(functions.get(name.lowercase))

  def defineFunction(function: PFunction) {
    functions.put(function.name.lowercase, function)
  }

  def staticContext(identifier: String): StaticContext =
    staticContexts.getOrElseUpdate(identifier, new StaticContext(this))
}
