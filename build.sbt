lazy val common = Def.settings(
  libraryDependencies += "org.eclipse.jgit" % "org.eclipse.jgit" % "6.9.0.202403050737-r",
  run / javaOptions := Seq("-Xmx2G"),
  run / fork := true,
  run / baseDirectory := file("."),
)

lazy val p1 = project.settings(common)
lazy val p2 = project.settings(common)
