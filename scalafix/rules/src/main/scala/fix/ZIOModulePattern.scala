package fix

import scalafix.v1._
import scala.meta._

object ZIOModulePattern {

  def patches(
      trt: Defn.Trait,
      serviceName: Type.Name
  )(implicit doc: SyntacticDocument) = {
    val accessibleMethods = findAccessibleMethods(trt)
    if (accessibleMethods.isEmpty)
      Patch.empty
    else
      ZIOServiceImpl.serviceImplPatch(trt, serviceName, accessibleMethods) +
        ZIOCompanion.companionPatch(trt, serviceName, accessibleMethods)
  }

  def findAccessibleMethods(
      trt: Defn.Trait
  ): List[AccessibleMethod] = trt.collect {
    case method @ Decl.Def(
          mods,
          name,
          tparams,
          params,
          mtype @ Type.Apply(rtype, rtparams)
        ) if public(mods) =>
      val service = t"${trt.name}"
      val zio = q"ZIO.serviceWithZIO"
      val stream = q"ZStream.serviceWithStream"
      val pipeline = q"ZPipeline.serviceWithPipeline"
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
        case t"""Stream[$err, $ret]""" =>
          (t"ZStream[$service, $err, $ret]", stream)
        case t"""ZStream[$res, $err, $ret]""" if res.syntax == "Any" =>
          (t"ZStream[$service, $err, $ret]", stream)
        case t"""ZStream[$res, $err, $ret]""" =>
          (t"ZStream[$service with $res, $err, $ret]", stream)
        case t"""ZPipeline[$res, $err, $in, $out]""" if res.syntax == "Any" =>
          (t"ZPipeline[$service, $err, $in, $out]", pipeline)
        case t"""ZSink[$res, $err, $in, $l, $ret]""" if res.syntax == "Any" =>
          (t"ZSink[$service, $err, $in, $l, $ret]", sink)
        case t"""ZSink[$res, $err, $in, $l, $ret]""" =>
          (t"ZSink[$service with $res, $err, $in, $l, $ret]", sink)
        case t"""Sink[$err, $in, $l, $ret]""" =>
          (t"ZSink[$service, $err, $in, $l, $ret]", sink)
      }

      val arguments =
        params
          .map(pprams =>
            pprams.map {
              case Term.Param(_, name, Some(Type.Repeated(t)), _) =>
                Term.Repeated(Term.Name(name.value))
              case Term.Param(_, name, _, _) =>
                Term.Name(name.value)
            }
          )

      AccessibleMethod(
//        method,
        Decl.Def(
          List(),
          method.name,
          if (method.paramClauseGroups.isEmpty)
            List(Member.ParamClauseGroup(Type.ParamClause(List()), List()))
          else
            method.paramClauseGroups,
          Type.Apply(
            rtype,
            Type.ArgClause(
              rtparams.map(_.syntax).map(Type.Name(_))
            )
          )
        ),
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

  private def public(mods: List[Mod]): Boolean = mods.forall {
    case Mod.Private(_)   => false
    case Mod.Protected(_) => false
    case _                => true
  }

}
