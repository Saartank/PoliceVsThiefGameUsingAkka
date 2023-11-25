ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "PoliceThiefGame"
  )

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % "2.6.19", // Use the latest Akka 2.6 version
  "com.typesafe.akka" %% "akka-http" % "10.2.9",
  "com.typesafe.akka" %% "akka-slf4j" % "2.6.19",
  "com.typesafe.akka" %% "akka-stream" % "2.6.19",
  "org.slf4j" % "slf4j-simple" % "1.7.36",
  "io.circe" %% "circe-core" % "0.14.1",
  "io.circe" %% "circe-generic" % "0.14.1",
  "io.circe" %% "circe-parser" % "0.14.1",
  "ch.megard" %% "akka-http-cors" % "1.2.0",
  "software.amazon.awssdk" % "s3" % "2.17.52",
  "software.amazon.awssdk" % "apache-client" % "2.17.52",
  "org.scalatest" %% "scalatest" % "3.2.10" % Test
)

excludeFilter in assembly := "META-INF/license/*"

assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "application.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}