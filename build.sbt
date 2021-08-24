name := "payment-ingestion"

version := "0.1"

scalaVersion := "2.13.6"
val kafkaVersion = "2.8.0"

resolvers ++= Seq (
  Opts.resolver.mavenLocalFile,
  "Confluent" at "https://packages.confluent.io/maven"
)

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.1",

  "org.apache.kafka" % "kafka-clients" % kafkaVersion,
  "org.apache.kafka" % "connect-json" % kafkaVersion,
  "org.apache.kafka" % "connect-runtime" % kafkaVersion,
  "org.apache.kafka" % "kafka-streams" % kafkaVersion,
  "org.apache.kafka" %% "kafka-streams-scala" % kafkaVersion,
  "org.apache.kafka" % "connect-runtime" % kafkaVersion,

  "com.fasterxml.jackson.core" % "jackson-databind" % "2.12.4",
  "io.confluent" % "kafka-json-serializer" % "5.0.1",
  "javax.ws.rs" % "javax.ws.rs-api" % "2.1.1" artifacts Artifact("javax.ws.rs-api", "jar", "jar")
)