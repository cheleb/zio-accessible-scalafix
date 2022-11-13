package fix

import scalafix.v1._
import scala.meta._

object ZIOServiceImpl {
  def serviceImplPatch(
      trt: Defn.Trait,
      serviceName: Type.Name,
      accessibleMethods: List[AccessibleMethod]
  )(implicit doc: SyntacticDocument) = findServiceImpl(trt, serviceName) match {
    case None =>
      createServiceImpl(
        trt,
        serviceName,
        accessibleMethods.map(_.methodImpl)
      )
    case Some(impl) => updateServiceImpl(impl, accessibleMethods)

  }

  private def updateServiceImpl(
      impl: Defn.Class,
      accessibleMethods: List[AccessibleMethod]
  ) = {
    val serviceImplMethods = impl.templ.stats.collect {
      case Defn.Def(
            mods,
            name,
            tparams,
            params,
            Some(returnType),
            _
          ) =>
        q"..$mods def ${name}[..$tparams](...$params) : $returnType"
    }.map(_.structure)
    val newMethods =
      accessibleMethods.filterNot(m => serviceImplMethods.contains(m.methodDef.structure))
    if (newMethods.isEmpty) Patch.empty
    else {
      val newTemplate = impl.templ.copy(stats =
        impl.templ.stats ++ newMethods.map(_.methodImpl)
      )
      Patch.replaceTree(impl, impl.copy(templ = newTemplate).toString)

    }
  }

  private def findServiceImpl(
      trt: Defn.Trait,
      serviceName: Type.Name
  )(implicit doc: SyntacticDocument) = doc.tree.collect {
    case impl @ Defn.Class(_, _, _, _, _)
        if impl.templ.inits.exists(_.tpe.structure == trt.name.structure) =>
      impl
  }.headOption

  private def createServiceImpl(
      trt: Defn.Trait,
      serviceName: Type.Name,
      accessibleMethods: List[Defn.Def]
  ) = {
    val tmpl = template"{ ..${accessibleMethods} }"
    Patch.addRight(
      trt,
      s"""
       |class ${serviceName}Impl extends ${serviceName} $tmpl
       |""".stripMargin
    )
  }
}
