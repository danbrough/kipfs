import org.gradle.api.Project
import java.io.FileInputStream
import java.util.Properties
import java.io.File

const val KIPFS_ADDRESS = "KIPFS_ADDRESS"

private fun MutableMap<String, String>.loadProps(file: File) {
  if (file.exists()) {
    FileInputStream(file).use { input ->
      Properties().apply {
        load(input)
        forEach { key, value ->
          this@loadProps[key.toString()] = value.toString()
        }
      }
    }
  }
}


fun kipfsEnvironment(project: Project): Map<String, String> =
  mutableMapOf<String, String>().apply {
    project.properties.filter { it.key.startsWith("KIPFS_") }.forEach {
      set(it.key,it.value?.toString() ?: "")
    }

    //override default values in local.properties
    loadProps(project.rootProject.file("local.properties"))

  }