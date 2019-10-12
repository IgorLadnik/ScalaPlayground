import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val kafka = "org.apache.kafka" %% "kafka" % "1.1.0"
  lazy val avro = "org.apache.avro"  %  "avro"  %  "1.7.7"
  lazy val avroSerializer = "io.confluent" % "kafka-avro-serializer" % "3.2.1"
  lazy val logBack = "ch.qos.logback" %  "logback-classic" % "1.1.7"
  lazy val parser = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"
  lazy val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.6.0-M2"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.10"
  lazy val akkaStream = "com.typesafe.akka" %% "akka-stream" % "2.5.23"
}
