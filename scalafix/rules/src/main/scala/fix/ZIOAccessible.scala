package fix

import scalafix.v1._
import scala.meta._

case class AccessibleMethod(
  methodDef: Decl.Def,
  methodImpl: Defn.Def,
  companionMethodDef: Decl.Def,
  companionMethodImpl: Defn.Def
)

import ZIOModulePattern._
class ZIOAccessible extends SyntacticRule("ZIOAccessible") {



  override def fix(implicit doc: SyntacticDocument): Patch = {

    doc.tree.collect { case trt @ Defn.Trait(mods, serviceName, _, _, _) =>
      mods
        .collectFirst {
          case Mod.Annot(Init(Type.Name(name), _, _)) if name == "Accessible" =>

            val accessibleMethods = findAccessibleMethods(trt)


            serviceImplPatch(trt, serviceName, accessibleMethods) + companionPatch(
              trt,
              serviceName,
              accessibleMethods
            )

        }
        .getOrElse(Patch.empty)

    }.asPatch

  }

  


}
