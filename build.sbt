enablePlugins(FRCPlugin)

name := "864Robot"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += "Funky-Repo" at "http://team846.github.io/repo"
resolvers += "WPILib-Maven" at "http://team846.github.io/wpilib-maven"
resolvers += "opencv-maven" at "http://first.wpi.edu/FRC/roborio/maven/development"

organization := "com.lynbrookrobotics"
teamNumber := 846

val potassiumVersion = "0.1.0-21914cf9"
libraryDependencies += "com.lynbrookrobotics" %% "potassium-core" % potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-commons" % potassiumVersion
libraryDependencies += "com.lynbrookrobotics" %% "potassium-frc" % potassiumVersion

libraryDependencies += "edu.wpi.first" % "wpilib" % "2018.1.1"
libraryDependencies += "edu.wpi.first" % "wpiutil" % "2018.1.1"
libraryDependencies += "edu.wpi.first" % "cscore" % "2018.1.1"
libraryDependencies += "com.ctre" % "phoenix" % "5.1.3.1"
libraryDependencies += "org.opencv" % "opencv-java" % "3.2.0"

unmanagedJars in Compile += file("/Users/yerin/wpilib/java/current/lib/ntcore.jar")