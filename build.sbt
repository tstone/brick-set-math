
name := "brick-set-tools"

scalaVersion :=  "2.11.8"

resolvers +=  Opts.resolver.sonatypeReleases

libraryDependencies ++= Seq(
  "com.beachape"              %% "enumeratum"     % "1.3.7",
  "org.scala-lang.modules"    %% "scala-xml"      % "1.0.5",
  "org.specs2"                %% "specs2-core"    % "3.7" % "test"
)
