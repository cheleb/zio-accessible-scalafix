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
            val methods = trt.collect {
              case method @ Decl.Def(mod, name, _, params, _) =>
                val service = t"${trt.name}"
                val returnType = method.decltpe match {
                  case t"""ZIO[$res, $err, $ret]""" if res.syntax == "Any" =>
                    t"ZIO[$service, $err, $ret]"
                  case t"""ZIO[$res, $err, $ret]""" =>
                    t"ZIO[$service with $res, $err, $ret]"
                }
                val arguments =
                  params.map(pprams => pprams.map(i => Term.Name(i.name.value)))
                q"""
                    ..$mod def ${method.name}(...$params) : $returnType =
                          ZIO.serviceWithZIO[$service](_.${method.name}(...$arguments))
                    """
            }

            val list = doc.tree.collect {
              case cls @ Defn.Object(_, name, orig)
                  if name.value == serviceName.value =>
                // We have a companion object
                val newTemplate = orig.copy(stats = orig.stats ++ methods)
                Patch.replaceTree(cls, cls.copy(templ = newTemplate).toString)
            }

            if (list.isEmpty) {
              val tmpl = template"{ ..$methods }"
              Patch.addRight(
                trt,
                s"""
                    |object ${serviceName} $tmpl
                    """.stripMargin
              )

            } else {
              list.asPatch
            }

        }
        .getOrElse(Patch.empty)

    }.asPatch

  }

}
