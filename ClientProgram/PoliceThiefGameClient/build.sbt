ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "PoliceThiefGameClient"
  )


libraryDependencies ++= Seq(
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "com.softwaremill.sttp.client3" %% "core" % "3.3.11",
  "ch.qos.logback" % "logback-core" % "1.3.0-alpha10",
  "ch.qos.logback" % "logback-classic" % "1.3.0-alpha10",
  "org.slf4j" % "slf4j-api" % "2.0.0-alpha5",
  "com.typesafe" % "config" % "1.4.1",
  "org.yaml" % "snakeyaml" % "1.29",
  "software.amazon.awssdk" % "s3" % "2.17.52",
  "software.amazon.awssdk" % "apache-client" % "2.17.52",
)

excludeFilter in assembly := "META-INF/license/*"

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}