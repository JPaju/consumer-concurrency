Global / onChangedBuildSource := ReloadOnSourceChanges
watchBeforeCommand            := Watch.clearScreen

name         := "consumer-concurrency"
version      := "0.1.0-SNAPSHOT"
scalaVersion := "3.2.0"

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-explain",
  "-Xfatal-warnings",
  "-Ycheck-all-patmat",
  "-Ycheck-reentrant",
  "-Ykind-projector",
  "-Ysafe-init"
) ++ Seq("-source", "future")

val zioVersion      = "2.0.2"
val zioKafkaVersion = "2.0.0"
libraryDependencies ++= Seq(
  "dev.zio" %% "zio"         % zioVersion,
  "dev.zio" %% "zio-streams" % zioVersion,
  "dev.zio" %% "zio-kafka"   % zioKafkaVersion
)
