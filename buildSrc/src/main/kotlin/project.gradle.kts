import java.io.FileInputStream

interface ProjectInitExtension {
  val message: Property<String>

}

class ProjectPlugin @javax.inject.Inject constructor() : Plugin<Project> {


  override fun apply(project: Project) {
/*
    configure<ProjectInitExtension> {
      ProjectProperties.init(project)
    }

*/

    //println("PROJECT APPLY: $this $project")

    project.extensions.create<ProjectInitExtension>("projectProperties")

    ProjectProperties.init(project)
    //initProps()

    project.task("versionName") {
      doLast {
        println(ProjectProperties.getVersionName())
      }
    }

    project.task("versionNameNext") {
      doLast {
        println(ProjectProperties.getIncrementedVersionName())
      }
    }

    project.task("versionIncrement") {
      doLast {
        val propsFile = project.file("gradle.properties")
        val fis = FileInputStream(propsFile)
        val prop = java.util.Properties()
        prop.load(fis)
        fis.close()
        val version = prop.getProperty("buildVersion", "0").toInt()
        println("version $version")
        prop.setProperty("buildVersion", "${version + 1}")
        val fos = java.io.PrintWriter(java.io.FileWriter(propsFile))
        prop.store(fos, "")
        fos.println()
        fos.close()
      }
    }
  }
}


/*

configure<ProjectInitExtension> {
  println("2222222222222222222222222222configuring project init extension")
  message.set("Hi")
}

*/

