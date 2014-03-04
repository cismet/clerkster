name := "Clerkster"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache
)     

play.Project.playJavaSettings

//resolvers += "Cismet Repo" at "https://repo.cismet.de/cismet-libs-snapshots-local"

resolvers += "Local Maven Repository" at "file:///"+Path.userHome.absolutePath+"/.m2/"

libraryDependencies += "commons-io" % "commons-io" % "2.4"

libraryDependencies += "org.apache.ant" % "ant" % "1.9.3"

libraryDependencies += "de.cismet.commons" % "cismet-commons" % "2.0-SNAPSHOT"

libraryDependencies += "org.slf4j" % "log4j-over-slf4j" % "1.7.6"

ivyXML :=
  <dependencies>
    <exclude org="log4j" name="log4j" />
    <exclude org="commons-logging" name="commons-logging" />
    <exclude org="org.slf4j" name="slf4j-log4j12" />
  </dependencies>


