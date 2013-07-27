import sbt._
import Keys._

object DefaultBuild extends Build {
  lazy val root = Project(
    id = "Root",
    base = file(".")
  ) aggregate(core, ext)

  lazy val core = Project(
    id = "Core",
    base = file("core")
  ) settings(
    libraryDependencies ++= Seq(
      LibraryManagement.akkaActor,
      LibraryManagement.slf4jApi,
      LibraryManagement.slf4jLog4j,
      LibraryManagement.log4j,

      LibraryManagement.springContext,
      LibraryManagement.mvel2,

      LibraryManagement.jsoup % "test",
      LibraryManagement.junit4,
      LibraryManagement.scalaTest
    )
  )

  lazy val ext = Project(
    id = "Ext",
    base = file("ext")
  ) settings(
    libraryDependencies ++= Seq(
      LibraryManagement.springOrm,
      LibraryManagement.springJdbc
    )
  ) dependsOn(core)
}
