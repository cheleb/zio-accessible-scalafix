package fix

import scalafix.v1._
import scala.meta._

class ZIOAccessible extends SyntacticRule("ZIOAccessible") {

  case class AccessibleMethod(
    methodDef: Decl.Def,
    methodImpl: Defn.Def,
    companionMethodDef: Decl.Def,
    companionMethodImp: Defn.Def
  )

  override def fix(implicit doc: SyntacticDocument): Patch = {

    doc.tree.collect { case trt @ Defn.Trait(mods, serviceName, _, _, _) =>
      mods
        .collectFirst {
          case Mod.Annot(Init(Type.Name(name), _, _)) if name == "Accessible" =>

            val accessibleMethods = findAccessibleMethods(trt)

            val updateCompanion = updateCompanionIfExists(serviceName, accessibleMethods)


            val updateSertCompanion = if (updateCompanion.isEmpty) {
              createCompanion(trt, serviceName, accessibleMethods.map(_.companionMethodImp))
            } else updateCompanion

            val updateImpl = updateServiceImplIfExists(serviceName, trt, accessibleMethods)
            
            val updateSertImpl = if (updateImpl.isEmpty) {
              createServiceImpl(trt, serviceName, accessibleMethods.map(_.methodImpl))
            } else updateImpl

            updateSertImpl + updateSertCompanion

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
       |""".stripMargin
    )
  }

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

  private def updateCompanionIfExists(
      serviceName: Type.Name,
      accessibleMethods: List[AccessibleMethod]
  )(implicit doc: SyntacticDocument): Patch = doc.tree.collect {
    case obj @ Defn.Object(_, name, orig) if name.value == serviceName.value =>
      // We have a companion object
      val companionMethods = orig.stats.collect {
        case method @ Defn.Def(mods, name, tparams, params, Some(returnType), _) =>
          q"..$mods def ${name}[..$tparams](...$params) : $returnType"
      }

      val newTemplate = orig.copy(stats =
        orig.stats ++ accessibleMethods
          .collect {
            case AccessibleMethod(_, _, decl, defn)
                if !companionMethods.exists(_.syntax == decl.syntax) =>
              defn
          }
      )
      Patch.replaceTree(obj, obj.copy(templ = newTemplate).toString)
  }.asPatch


  private def updateServiceImplIfExists(
      serviceName: Type.Name,
      trt: Defn.Trait,
      accessibleMethods: List[AccessibleMethod]
  )(implicit doc: SyntacticDocument): Patch = doc.tree.collect {
    case impl @ Defn.Class(mods, name, _, _, orig) if impl.templ.inits.exists(_.tpe.syntax == trt.name.syntax) =>
      val serviceImplMethods = impl.templ.stats.collect {
        case method @ Defn.Def(mods, name, tparams, params, Some(returnType), _) =>
          q"..$mods def ${name}[..$tparams](...$params) : $returnType"
      }

      val newTemplate = orig.copy(stats =
        orig.stats ++ accessibleMethods
          .collect {
            case AccessibleMethod(decl, defn, _, _) if !serviceImplMethods.exists(_.syntax == decl.syntax) =>
              defn
          }
      )
      Patch.replaceTree(impl, impl.copy(templ = newTemplate).toString)
  }.asPatch

  private def public(mods: List[Mod]): Boolean = mods.forall {
    case Mod.Private(_) => false
    case Mod.Protected(_) => false
    case _ => true
  }

  /** Find all methods in the trait
    */
  private def findAccessibleMethods(
      trt: Defn.Trait
  ): List[AccessibleMethod] = trt.collect {
    case method @ Decl.Def(mods, name, tparams, params, mtype) if public(mods) =>
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

      AccessibleMethod( method,
        q"""
          ..$mods def ${method.name}[..$tparams](...$params) : $mtype = ???
          """,
        q"..$mods def ${name}[..$tparams](...$params) : $returnType",
        q"""
          ..$mods def ${method.name}[..$tparams](...$params) : $returnType =
                $lookup[$service](_.${method.name}(...$arguments))
          """
      )
  }

}
