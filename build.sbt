
name := "brick-set-tools"

scalaVersion :=  "2.11.8"

resolvers +=  Opts.resolver.sonatypeReleases

libraryDependencies ++= Seq(
  "com.beachape"              %% "enumeratum"     % "1.3.7",
  "org.scala-lang.modules"    %% "scala-xml"      % "1.0.5",
  "com.typesafe.akka"         %% "akka-stream"    % "2.4.8",
  "com.typesafe.akka"         %% "akka-testkit"   % "2.4.8" % "test",
  "org.specs2"                %% "specs2-core"    % "3.7" % "test",
  "org.scalacheck"            %% "scalacheck"     % "1.13.0" % "test",
  "com.typesafe.akka"         %% "akka-testkit"   % "2.4.8" % "test"
)
