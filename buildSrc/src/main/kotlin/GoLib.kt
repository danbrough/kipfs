import BuildEnvironment.buildEnvironment
import BuildEnvironment.platformName
import BuildEnvironment.sharedLibExtn
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.TaskProvider
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family
import org.jetbrains.kotlin.konan.target.KonanTarget
import java.io.File
import javax.inject.Inject


object GoLib {
  
  fun <T : KotlinNativeTarget> Project.registerGoLibBuild(
    target: KonanTarget,
    goDir: File,
    outputDir: File,
    libBaseName: String,
    modules: String = ".",
    name: String = "build${target.platformName.capitalize()}"
  ): TaskProvider<GoLibBuildTask<T>> =
    tasks.register(name, target, goDir, outputDir, libBaseName, modules)
  
  
  fun KonanTarget.goLibsDir(project: Project): File =
    project.rootProject.file("golib/build/golib/$platformName")
}


/*
tasks.register("styleTest") {
  doLast {
    val out = project.serviceOf<StyledTextOutputFactory>().create("testOutput")
    StyledTextOutput.Style.values().forEach {
      out.style(it).println("This line has the style: $it")
      
      if (hasProperty("message")) {
        out.style(it).println("The message is: ${property("message")}")
      }
    }
  }
}*/

abstract class GoLibBuildTask<T : KotlinNativeTarget> @Inject constructor(
  private val target: KonanTarget,
  private val goDir: File,
  private val outputDir: File,
  private val outputBaseName: String,
  private val modules: String = ","
) : Exec() {
  
  
  init {
    group = BasePlugin.BUILD_GROUP
    // println("PLATFORM $platform  godir: $goDir: libDir: ${libDir.orNull}")
    
    environment("PLATFORM", target.name)
    
    
    inputs.files(project.fileTree(goDir) {
      include("**/*.go")
      include("**/*.c")
      include("**/*.h")
      include("**/*.mod")
    })
    
    
    val libFile =
      outputDir.resolve("lib${outputBaseName}.${target.sharedLibExtn}")
    val headerFile = outputDir.resolve("lib${outputBaseName}.h")
    outputs.files(libFile, headerFile)
    
    
    workingDir(goDir)
    
    val commandEnvironment = target.buildEnvironment()
    environment(commandEnvironment)
    
    commandLine(
      listOf(
        BuildEnvironment.goBinary, "build", "-v",//"-x",
        "-trimpath", "-buildmode=c-shared", "-o", libFile, modules
      )
    )
    
    
    val out = project.serviceOf<StyledTextOutputFactory>().create("golibOutput")
    
    doFirst {
      out.style(StyledTextOutput.Style.Info).println("Building golib for $target")
      out.style(StyledTextOutput.Style.ProgressStatus).println("environment: $commandEnvironment")
      out.style(StyledTextOutput.Style.ProgressStatus)
        .println("commandLine: ${commandLine.joinToString(" ")}")
    }
    doLast {
      if (didWork) out.style(StyledTextOutput.Style.Success)
        .println("Finished building golib for ${target.platformName}")
    }
  }
  
  
  fun appendToEnvironment(key: String, value: String, separator: String = " ") {
    environment(key, environment.getOrDefault(key, null).let {
      if (it == null) value else "$it$separator$value"
    })
  }
  
}


