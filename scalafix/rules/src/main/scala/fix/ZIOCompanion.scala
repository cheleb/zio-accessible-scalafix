package fix

import scalafix.v1._
import scala.meta._

object ZIOCompanion {

  def companionPatch(
      trt: Defn.Trait,
      serviceName: Type.Name,
      accessibleMethods: List[AccessibleMethod]
  )(implicit doc: SyntacticDocument) = findCompanion(serviceName) match {
    case None =>
      createCompanion(
        trt,
        serviceName,
        accessibleMethods.map(_.companionMethodImpl)
      )
    case Some(obj) => updateCompanion(obj, accessibleMethods)

  }

  private def findCompanion(
      serviceName: Type.Name
  )(implicit doc: SyntacticDocument) = doc.tree.collect {
    case obj @ Defn.Object(_, name, _) if name.value == serviceName.value =>
      obj
  }.headOption

  private def updateCompanion(
      obj: Defn.Object,
      accessibleMethods: List[AccessibleMethod]
  )(implicit doc: SyntacticDocument): Patch = {
    val companionMethods = obj.templ.stats
      .collect {
        case method @ Defn.Def(
              mods,
              name,
              tparams,
              params,
              Some(returnType),
              _
            ) =>
          q"..$mods def ${name}[..$tparams](...$params) : $returnType"
      }
      .map(_.structure)

    val newMethods = accessibleMethods.filterNot(m =>
      companionMethods.contains(m.companionMethodDef.structure)
    )
    if (newMethods.isEmpty) Patch.empty
    else {
      val newTemplate = obj.templ.copy(stats =
        obj.templ.stats ++ newMethods.map(_.companionMethodImpl)
      )
      Patch.replaceTree(obj, obj.copy(templ = newTemplate).toString)
    }
  }

  private def createCompanion(
      trt: Defn.Trait,
      serviceName: Type.Name,
      accessibleMethods: List[Defn.Def]
  ) = {
    val tmpl = template"{ ..${accessibleMethods} }"
    Patch.addRight(
      trt,
      s"""
       |object ${serviceName} $tmpl
       |""".stripMargin
    )
  }

}
