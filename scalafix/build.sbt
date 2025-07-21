val scala3Version = "3.2.1"
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
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    pgpPublicRing := file("/tmp/public.asc"),
    pgpSecretRing := file("/tmp/secret.asc"),
    pgpPassphrase := sys.env.get("PGP_PASSWORD").map(_.toArray),
    scalaVersion := V.scala213,
    crossScalaVersions := List(V.scala213, V.scala212),
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
  moduleName := "zio-module-pattern",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion
)

lazy val input = project.settings(
  (publish / skip) := true,
  libraryDependencies += "dev.zio" %% "zio-streams" % "2.1.20"
).dependsOn(accessible)

lazy val output = project.settings(
  (publish / skip) := true,
   libraryDependencies += "dev.zio" %% "zio-streams" % "2.1.20"
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
