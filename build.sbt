version := "0.1"
scalaVersion := "2.12.8"

lazy val deps = new {

  val specs2V = "4.8.1"
  val zioV    = "1.0.0-RC17"

  val specs2 = Seq(
    "specs2-core",
    "specs2-matcher"
  ).map("org.specs2" %% _ % specs2V % "test")

  val cats           = "org.typelevel" %% "cats-core"        % "2.1.0"
  val catsEffect     = "org.typelevel" %% "cats-effect"      % "2.0.0"
  val zioInteropCats = "dev.zio"       %% "zio-interop-cats" % "2.0.0.0-RC10"

  val common = specs2 ++ Seq(cats)

  val zio = Seq(
    "zio"
  ).map("dev.zio" %% _ % zioV)

}

lazy val root =
  project
    .in(file("."))
    .settings(
      name := "data-pipeline",
      libraryDependencies ++= deps.specs2,
      fork := true,
      fork in Test := true
    )
    .aggregate(onlineAds, system)

lazy val onlineAds =
  project
    .in(file("online-ads"))
    .settings(
      name := "online-ads",
      libraryDependencies ++= deps.common ++ deps.zio ++ Seq(deps.zioInteropCats, deps.catsEffect),
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
      fork := true,
      fork in Test := true,
      scalacOptions ++= compilerOpts
    )
    .dependsOn(system)

lazy val system =
  project
    .in(file("system"))
    .settings(
      name := "system",
      libraryDependencies ++= deps.common ++ Seq(deps.catsEffect),
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
      fork := true,
      fork in Test := true,
      scalacOptions ++= compilerOpts
    )

lazy val compilerOpts =
  Seq("-Ypartial-unification")
