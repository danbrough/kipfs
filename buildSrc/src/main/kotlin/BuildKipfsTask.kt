import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

abstract class BuildKipfsTask : DefaultTask() {
  @get:Input
  abstract val greeting: Property<String>

  @TaskAction
  fun greet() {
    println("greet(): The greeting is: ${greeting.get()}")
  }
}