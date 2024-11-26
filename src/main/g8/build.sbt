lazy val akkaHttpVersion = "10.6.3"
lazy val akkaVersion    = "2.9.7"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

javacOptions ++= Seq("--release", "21")

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      organization    := "$organization$",
      scalaVersion    := "$scala_version$"
    )),
    name := "butterflyy",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"                % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json"     % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-actor-typed"         % akkaVersion,
      "com.typesafe.akka" %% "akka-stream"              % akkaVersion,
      "com.typesafe.akka" %% "akka-pki"                 % akkaVersion,
      "ch.qos.logback"    % "logback-classic"           % "1.2.11",

      "com.typesafe.akka" %% "akka-http-testkit"        % akkaHttpVersion % Test,
      "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion     % Test,
      "org.scalatest"     %% "scalatest"                % "3.2.12"        % Test,

      "com.typesafe.slick" %% "slick" % "3.5.0-RC1",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.5.0-RC1",
      "org.flywaydb" % "flyway-core" % "10.21.0",
      "org.flywaydb" % "flyway-database-postgresql" % "10.21.0" % "runtime",

      "org.postgresql" % "postgresql" % "42.7.4",

      "com.typesafe" % "config" % "1.4.3",

      "org.slf4j" % "slf4j-nop" % "2.0.16",
      "com.h2database" % "h2" % "2.3.232",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test
    )


  )
// https://mvnrepository.com/artifact/org.flywaydb/flyway-database-postgresql
