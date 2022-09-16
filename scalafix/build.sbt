lazy val V = _root_.scalafix.sbt.BuildInfo
inThisBuild(
  List(
    organization := "dev.cheleb",
    homepage := Some(url("https://github.com/cheleb/zio-accessible-scalafix")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    developers := List(
      Developer(
        "cheleb",
        "Olivier NOUGUIER",
        "olivier.nouguier@gmain.com",
        url("https://github.com/cheleb")
      )
    ),
    scalaVersion := V.scala213,
    semanticdbEnabled := true,
    semanticdbIncludeInJar := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= List("-Yrangepos")
  )
)

(publish / skip) := true

lazy val accessible = project.settings(
  (publish / skip) := true
)

lazy val rules = project.settings(
  moduleName := "named-literal-arguments",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
)

lazy val input = project.settings(
  (publish / skip) := true,
  libraryDependencies += "dev.zio" %% "zio" % "2.0.2"
).dependsOn(accessible)

lazy val output = project.settings(
  (publish / skip) := true,
   libraryDependencies += "dev.zio" %% "zio" % "2.0.2"
).dependsOn(accessible)

lazy val tests = project
  .settings(
    (publish / skip) := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    scalafixTestkitOutputSourceDirectories :=
      (output / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputSourceDirectories :=
      (input / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputClasspath :=
      (input / Compile / fullClasspath).value
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
