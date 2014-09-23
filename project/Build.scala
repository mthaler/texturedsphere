import sbt._
import Keys._
import sbtassembly.Plugin.AssemblyKeys._
import net.virtualvoid.sbt.graph.Plugin.graphSettings
import sbt.Package.ManifestAttributes
import sbtassembly.Plugin._
import sbtassembly.AssemblyUtils._

object Version {

  val akka = "2.3.5"

  val scala = "2.11.1"
}

object Dependencies {
  val gluegen_rt = "org.jogamp.gluegen" % "gluegen-rt-main" % "2.2.1"

  val jogl_all = "org.jogamp.jogl" % "jogl-all-main" % "2.2.1" 
}

object BuildSettings {
  import sbtassembly.Plugin.assemblySettings
  val buildOrganization = "heavens-above"
  val buildVersion      = "1.0.0"
  val buildScalaVersion = Version.scala

  /**
   * Default build settings
   */
  val buildSettings = Defaults.defaultSettings ++ graphSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion,
    fork := true,
    javacOptions ++= Seq("-Xlint:unchecked"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")
  )

  /**
   * Build settings for projects that require a fat .jar
   */
  lazy val jarBuildSettings = buildSettings ++ assemblySettings

  /**
   * Build settings for projects that require a fat jar, but no scala library
   */
  lazy val javaJarBuildSettings = jarBuildSettings ++ Seq(autoScalaLibrary := false)
}

object Build extends sbt.Build {
  import Dependencies._
  import BuildSettings._

  lazy val jogltest = Project(
    id = "texturedsphere",
    base = file("."),
    settings = jarBuildSettings ++ Seq(
      jarName := "texturedsphere.jar",
      libraryDependencies ++= Seq(gluegen_rt, jogl_all),
      mainClass in assembly := Some("texturedsphere.TexturedSphere"),
      mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
	{
	  case s if s.endsWith(".so") => custom
	  case s if s.endsWith(".dll") => custom
	  case s if s.startsWith("jogamp/nativetag") => custom
	  case x => old(x)
	}
      }
    )
  )
  
  val custom: MergeStrategy = new MergeStrategy {
    val name = "custom"
    def apply(tempDir: File, path: String, files: Seq[File]): Either[String, Seq[(File, String)]] =
      Right(files flatMap { f =>
	if(!f.exists) Seq.empty
	else if(f.isDirectory && (f ** "*.class").get.nonEmpty) Seq(f -> path)
	else sourceOfFileForMerge(tempDir, f) match {
	  case (_, _, _, false) => Seq(f -> path)
	  case (jar, base, p, true) =>
	    val filenames = files.map {
	      case f =>
		val (source, _, _, _) = sourceOfFileForMerge(tempDir, f) 
		source.getName
	    }
	    if (filenames.isEmpty) {
	      Seq(f -> path)
	    } else {
	      val sourceJarName = jar.getName
	      val index0 = sourceJarName.indexOf("natives") + 8
	      val index1 = sourceJarName.lastIndexOf(".")
	      val os = sourceJarName.substring(index0, index1)
	      val dest = new File(f.getParent, "natives/" + os + "/" + path)
	      IO.move(f, dest)
	      val result = Seq(dest -> ("natives/" + os + "/" + path))
	      if (dest.isDirectory) ((dest ** (-DirectoryFilter))) x relativeTo(base)
	      else result
	   }
	}
    })
  }
}