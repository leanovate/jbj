package de.leanovate.jbj.core.runtime.context

import java.io.PrintStream
import scala.collection.mutable
import de.leanovate.jbj.core.runtime._
import de.leanovate.jbj.core.ast.{NoNodePosition, Prog, NodePosition, NamespaceName}
import scala.collection.immutable.Stack
import de.leanovate.jbj.core.runtime.exception.CompileErrorException
import de.leanovate.jbj.core.runtime.value.{ArrayVal, PVar, PVal, StringVal}
import de.leanovate.jbj.core.ast.expr.value.ScalarExpr
import java.util.concurrent.atomic.AtomicLong
import de.leanovate.jbj.core.JbjEnv
import de.leanovate.jbj.api.JbjSettings
import de.leanovate.jbj.core.runtime.output.OutputBuffer

case class GlobalContext(jbj: JbjEnv, out: OutputBuffer, err: Option[PrintStream], settings: JbjSettings)
  extends Context {
  private var _inShutdown = false

  private val classes = mutable.Map.empty[Seq[String], PClass]

  private val constants = mutable.Map.empty[ConstantKey, PVal]

  private val functions = mutable.Map.empty[Seq[String], PFunction]

  private val staticContexts = mutable.Map.empty[String, GenericStaticContext]

  private val includedFiles = mutable.Set.empty[String]

  private val autoloading = mutable.Set.empty[String]

  def name = ""

  def global = this

  def static = staticContext("global")

  def stack: Stack[NodePosition] = Stack.empty[NodePosition]

  val instanceCounter = new AtomicLong(0)

  val GLOBALS = ArrayVal()

  GLOBALS.setAt("GLOBALS", GLOBALS)(this)

  def include(file: String)(implicit ctx: Context): Option[(Prog, Boolean)] = jbj.parse(file) match {
    case Some(Left(prog)) =>
      Some(prog, includedFiles.add(prog.fileName))
    case Some(Right(t)) => throw new CompileErrorException(t.getMessage)
    case None if file.startsWith("/") => None
    case None =>
      val idx = ctx.currentPosition.fileName.lastIndexOf('/')
      if (idx < 0)
        None
      else
        jbj.parse(ctx.currentPosition.fileName.substring(0, idx + 1) + file) match {
          case Some(Left(prog)) => Some(prog, includedFiles.add(prog.fileName))
          case Some(Right(t)) => throw new CompileErrorException(t.getMessage)
          case None => None
        }
  }

  def findClass(name: NamespaceName): Option[PClass] =
    jbj.predefinedClasses.get(name.lowercase).map(Some.apply).getOrElse(classes.get(name.lowercase))

  def findClassOrAutoload(name: NamespaceName)(implicit position: NodePosition): Option[PClass] =
    findClass(name).map(Some.apply).getOrElse {
      implicit val ctx = this

      findFunction(NamespaceName(relative = true, "__autoload")).flatMap {
        case autoload if !autoloading.contains(name.toString.toLowerCase) =>
          autoloading.add(name.toString.toLowerCase)
          autoload.call(ScalarExpr(StringVal(name.toString)) :: Nil)
          autoloading.remove(name.toString.toLowerCase)
          findClass(name)
        case _ => None
      }
    }

  def defineClass(pClass: PClass) {
    classes.put(pClass.name.lowercase, pClass)
  }

  def findConstant(name: String): Option[PVal] =
    jbj.preedfinedConstants.get(name.toUpperCase).map(Some.apply).getOrElse {
      constants.get(CaseSensitiveConstantKey(name)).map(Some.apply).getOrElse {
        constants.get(CaseInsensitiveConstantKey(name.toLowerCase))
      }
    }

  def defineConstant(name: String, value: PVal, caseInsensitive: Boolean) {
    if (caseInsensitive)
      constants.put(CaseInsensitiveConstantKey(name.toLowerCase), value)
    else
      constants.put(CaseSensitiveConstantKey(name), value)
  }

  override def findVariable(name: String) = GLOBALS.getAt(name)(this).map {
    case variable: PVar => variable
    case pVal: PVal =>
      val variable = PVar(pVal)
      GLOBALS.setAt(name, variable)(this)
      variable
  }

  override def defineVariable(name: String, variable: PVar) {
    GLOBALS.setAt(name, variable)(this)
  }

  override def undefineVariable(name: String) {
    GLOBALS.unsetAt(name)(this)
  }

  def findFunction(name: NamespaceName) =
    jbj.predefinedFunctions.get(name.lowercase).map(Some.apply).getOrElse(functions.get(name.lowercase))

  def defineFunction(function: PFunction) {
    functions.put(function.name.lowercase, function)
  }

  def staticContext(identifier: String): GenericStaticContext =
    staticContexts.getOrElseUpdate(identifier, new GenericStaticContext)

  def staticContext(pClass: PClass): GenericStaticContext = {
    val identifier = "Class_" + pClass.toString
    staticContext(identifier)
  }

  def inShutdown = _inShutdown

  var isOutputBufferingCallback = false

  def cleanup() {
    currentPosition = NoNodePosition
    _inShutdown = true
    staticContexts.values.foreach(_.cleanup()(this))
    GLOBALS.cleanup()(this)

    out.closeAll()
  }
}
