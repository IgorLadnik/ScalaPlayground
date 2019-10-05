import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.5"
  lazy val kafka = "org.apache.kafka" %% "kafka" % "1.1.0"
  lazy val avro = "org.apache.avro"  %  "avro"  %  "1.7.7"
  lazy val avroSerializer = "io.confluent" % "kafka-avro-serializer" % "3.2.1"
  lazy val logBack = "ch.qos.logback" %  "logback-classic" % "1.1.7"
}
