name := "Clerkster"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.apache.ant" % "ant" % "1.9.3"

resolvers += (
    "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
)

