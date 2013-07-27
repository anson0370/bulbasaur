import sbt._

object LibraryManagement {
  private object Versions {
    val springVersion = "2.5.6.SEC02"
    val slf4jVersion = "1.5.6"
  }

  val scalaTest = "org.scalatest" %% "scalatest" % "1.9.1" % "test"

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.2.0"

  val slf4jApi = "org.slf4j" % "slf4j-api" % Versions.slf4jVersion
  val slf4jLog4j = "org.slf4j" % "slf4j-log4j12" % Versions.slf4jVersion

  val log4j = "log4j" % "log4j" % "1.2.15"

  val springCore = "org.springframework" % "spring-core" % Versions.springVersion
  val springTx = "org.springframework" % "spring-tx" % Versions.springVersion
  val springContext = "org.springframework" % "spring-context" % Versions.springVersion
  val springContextSupport = "org.springframework" % "spring-context-support" % Versions.springVersion
  val springOrm = "org.springframework" % "spring-orm" % Versions.springVersion
  val springJdbc = "org.springframework" % "spring-jdbc" % Versions.springVersion
  val springBeans = "org.springframework" % "spring-beans" % Versions.springVersion
  val springTest = "org.springframework" % "spring-test" % Versions.springVersion % "test"

  val mvel2 = "org.mvel" % "mvel2" % "2.0.19"

  val jsoup = "org.jsoup" % "jsoup" % "1.6.2"

  val junit4 = "junit" % "junit" % "4.6" % "test"
}