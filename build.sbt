name := "Clerkster"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings

resolvers += (
    "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/"
//  "Cismet Repo" at "https://repo.cismet.de/cismet-libs-snapshots-local"
)

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.apache.ant" % "ant" % "1.9.3"

libraryDependencies += "de.cismet.commons" % "cismet-commons" % "2.0-SNAPSHOT" changing()

