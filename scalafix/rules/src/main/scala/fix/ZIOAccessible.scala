package fix

import scalafix.v1._
import scala.meta._

class ZIOAccessible extends SemanticRule("ZIOAccessible") {

  override def fix(implicit doc: SemanticDocument): Patch = {

    doc.tree.collect { case trt @ Defn.Trait(mods, serviceName, _, _, _) =>
      mods
        .collectFirst {
          case Mod.Annot(Init(Type.Name(name), _, _)) if name == "Accessible" =>
            val accessibleMethods = findAccessibleMethods(trt)

            val update = updateCompanionIfExists(serviceName, accessibleMethods)
            if (update.isEmpty) {
              createCompanion(trt, serviceName, accessibleMethods.map(_._2))
            } else update

        }
        .getOrElse(Patch.empty)

    }.asPatch

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
       """.stripMargin
    )
  }

  private def updateCompanionIfExists(
      serviceName: Type.Name,
      accessibleMethods: List[(Decl.Def, Defn.Def)]
  )(implicit doc: SemanticDocument): Patch = doc.tree.collect {
    case cls @ Defn.Object(_, name, orig) if name.value == serviceName.value =>
      // We have a companion object
      val companionMethods = orig.stats.collect {
        case method @ Defn.Def(mod, name, _, params, Some(returnType), _) =>
          q"def ${name}(...$params) : $returnType"
      }

      val newTemplate = orig.copy(stats =
        orig.stats ++ accessibleMethods
          .collect {
            case (decl, defn)
                if !companionMethods.exists(_.syntax == decl.syntax) =>
              defn
          }
      )
      Patch.replaceTree(cls, cls.copy(templ = newTemplate).toString)
  }.asPatch

  /** Find all methods in the trait
    */
  private def findAccessibleMethods(
      trt: Defn.Trait
  ): List[(Decl.Def, Defn.Def)] = trt.collect {
    case method @ Decl.Def(mod, name, _, params, _) =>
      val service = t"${trt.name}"
      val zio = q"ZIO.serviceWithZIO"
      val stream = q"ZStream.serviceWithStream"
      val sink = q"ZSink.serviceWithSink"
      val (returnType, lookup) = method.decltpe match {
        case t"""Task[$ret]""" =>
          (t"RIO[$service, $ret]", zio)
        case t"""IO[$err, $ret]""" =>
          (t"ZIO[$service, $err, $ret]", zio)
        case t"""UIO[$ret]""" =>
          (t"URIO[$service, $ret]", zio)
        case t"""URIO[$res, $ret]""" if res.syntax == "Any" =>
          (t"URIO[$service, $ret]", zio)
        case t"""URIO[$res, $ret]""" =>
          (t"URIO[$service with $res, $ret]", zio)
        case t"""ZIO[$res, $err, $ret]""" if res.syntax == "Any" =>
          (t"ZIO[$service, $err, $ret]", zio)
        case t"""ZIO[$res, $err, $ret]""" =>
          (t"ZIO[$service with $res, $err, $ret]", zio)
        case t"""RIO[$res, $ret]""" if res.syntax == "Any" =>
          (t"RIO[$service, $ret]", zio)
        case t"""RIO[$res, $ret]""" =>
          (t"RIO[$service with $res, $ret]", zio)
        case t"""ZStream[$res, $err, $ret]""" if res.syntax == "Any" =>
          (t"ZStream[$service, $err, $ret]", stream)
        case t"""ZStream[$res, $err, $ret]""" =>
          (t"ZStream[$service with $res, $err, $ret]", stream)
        case t"""ZSink[$res, $err, $in, $l, $ret]""" if res.syntax == "Any" =>
          (t"ZSink[$service, $err, $in, $l, $ret]", sink)
        case t"""ZSink[$res, $err, $in, $l, $ret]""" =>
          (t"ZSink[$service with $res, $err, $in, $l, $ret]", sink)
      }
      
      val arguments =
        params.map(pprams => pprams.map(i => Term.Name(i.name.value)))

      (
        q"def ${name}(...$params) : $returnType",
        q"""
          ..$mod def ${method.name}(...$params) : $returnType =
                $lookup[$service](_.${method.name}(...$arguments))
          """
      )
  }

}
