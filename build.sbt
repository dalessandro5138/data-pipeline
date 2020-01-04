version := "0.1"
scalaVersion := "2.12.8"

lazy val deps = new {

  val specs2V = "4.8.1"

  val specs2 = Seq(
    "org.specs2" %% "specs2-core",
    "org.specs2" %% "specs2-matcher"
  ).map(_ % specs2V)

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
      libraryDependencies ++= deps.specs2,
      fork := true,
      fork in Test := true
    )
    .dependsOn(system)

lazy val system =
  project
    .in(file("system"))
    .settings(
      name := "system",
      libraryDependencies ++= deps.specs2,
      fork := true,
      fork in Test := true
    )
