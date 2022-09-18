package fix

import scalafix.v1._
import scala.meta._

class ZIOAccessible extends SemanticRule("ZIOAccessible") {

  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect { case trt @ Defn.Trait(mods, serviceName, _, _, _) =>
      mods
        .collectFirst {
          case annot: Mod.Annot
              if annot.init.tpe.asInstanceOf[Type.Name].value == "Accessible" =>
            val accessibleMethods = findAccessibleMethods(trt)

            val update = updateCompanionPatches(serviceName, accessibleMethods)
            if (update.isEmpty) {
              val tmpl = template"{ ..${accessibleMethods.map(_._2)} }"
              Patch.addRight(
                trt,
                s"""
                 |object ${serviceName} $tmpl
                 """.stripMargin
              )
            } else
              update

        }
        .getOrElse(Patch.empty)

    }.asPatch

  }

  private def updateCompanionPatches(
      serviceName: Type.Name,
      accessibleMethods: List[(Decl.Def, Defn.Def)]
  )(implicit doc: SemanticDocument): Patch = doc.tree.collect {
    case cls @ Defn.Object(_, name, orig) if name.value == serviceName.value =>
      // We have a companion object
      val companionMethods = orig.stats.collect { case method: Defn.Def =>
        method
      }
      val newTemplate = orig.copy(stats =
        orig.stats ++ accessibleMethods
          .collect {
            case (decl, defn)
                if !companionMethods.exists(_.name.value == decl.name.value) =>
              defn
          }
      )
      Patch.replaceTree(cls, cls.copy(templ = newTemplate).toString)
  }.asPatch


  /** Find all methods in the trait that are annotated with @Accessible
    */
  private def findAccessibleMethods(trt: Defn.Trait) = trt.collect {
    case method @ Decl.Def(mod, name, _, params, _) =>
      val service = t"${trt.name}"
      val returnType = method.decltpe match {
        case t"""Task[$ret]""" =>
          t"RIO[$service, $ret]"
        case t"""IO[$err, $ret]""" =>
          t"ZIO[$service, $err, $ret]"
        case t"""ZIO[$res, $err, $ret]""" if res.syntax == "Any" =>
          t"ZIO[$service, $err, $ret]"
        case t"""ZIO[$res, $err, $ret]""" =>
          t"ZIO[$service with $res, $err, $ret]"
      }
      val arguments =
        params.map(pprams => pprams.map(i => Term.Name(i.name.value)))
      (
        q"def ${method.name}(...$params) : $returnType",
        q"""
          ..$mod def ${method.name}(...$params) : $returnType =
                ZIO.serviceWithZIO[$service](_.${method.name}(...$arguments))
          """
      )
  }

}
