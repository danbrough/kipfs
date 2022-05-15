import java.io.FileInputStream


interface ProjectInitExtension

class ProjectPlugin @javax.inject.Inject constructor() : Plugin<Project> {


  override fun apply(project: Project) {

    project.extensions.create<ProjectInitExtension>("init")
    //initProps()

    project.task("versionName") {
      doLast {
        println(ProjectVersions.getVersionName())
      }
    }

    project.task("versionNameNext") {
      doLast {
        println(ProjectVersions.getIncrementedVersionName())
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

apply<ProjectPlugin>()

configure<ProjectInitExtension> {
  ProjectVersions.init(project)
}