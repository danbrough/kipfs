import java.io.FileInputStream


interface ProjectInitExtension

class ProjectPlugin @javax.inject.Inject constructor() : Plugin<Project> {


  override fun apply(project: Project) {

    project.extensions.create<ProjectInitExtension>("init")
    //initProps()

    project.task("projectVersionName") {
      doLast {
        println(ProjectVersions.getVersionName())
      }
    }

    project.task("projectNextVersionName") {
      doLast {
        println(ProjectVersions.getIncrementedVersionName())
      }
    }

    project.task("projectIncrementVersion") {
      doLast {
        val propsFile = project.file("project.properties")
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

// Apply the plugin
apply<ProjectPlugin>()

configure<ProjectInitExtension> {
  FileInputStream(file("project.properties")).use {
    val props = java.util.Properties()
    props.load(it)
    ProjectVersions.init(project,props)
  }
}