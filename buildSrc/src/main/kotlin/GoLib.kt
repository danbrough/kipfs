object Common {


/*
  @Suppress("UNCHECKED_CAST")
  fun <T : KotlinTarget> Project.createTarget(
    platform: Platform<T>, targetName: String = platform.name.toString(), conf: T.() -> Unit = {}
  ): T {
    val extn = kotlinExtension as KotlinMultiplatformExtension
    val preset: KotlinTargetPreset<T> =
      extn.presets.getByName(platform.name.toString()) as KotlinTargetPreset<T>
    return extn.targetFromPreset(preset, targetName, conf)
  }
*/


}

object GoLib {
  
  
/*  fun <T : KotlinNativeTarget> Project.registerGoLibBuild(
    target: KonanTarget,
    goDir: File,
    outputDir: File,
    libBaseName: String,
    modules: String = ".",
    name: String = "build${target.name.capitalized()}"
  ): TaskProvider<GoLibBuildTask<T>> =
    tasks.register(name, target, goDir, outputDir, libBaseName, modules)
  
  
  fun KonanTarget.goLibsDir(project: Project): File =
    project.rootProject.file("golib/build/lib/$name")
  */
  
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

//abstract class GoLibBuildTask<T : KotlinNativeTarget> @Inject constructor(
//  private val target: KonanTarget,
//  private val goDir: File,
//  private val outputDir: File,
//  private val outputBaseName: String,
//  private val modules: String = ","
//) : Exec() {
//
//
//  init {
//    group = BasePlugin.BUILD_GROUP
//    // println("PLATFORM $platform  godir: $goDir: libDir: ${libDir.orNull}")
//
//    environment("PLATFORM", target.name)
//
//
//    inputs.files(project.fileTree(goDir) {
//      include("**/*.go")
//      include("**/*.c")
//      include("**/*.h")
//      include("**/*.mod")
//    })
//
//
//    val libFile = outputDir.resolve("lib${outputBaseName}.so")
//    val headerFile = outputDir.resolve("lib${outputBaseName}.so")
//    outputs.files(libFile, headerFile)
//
//
//    workingDir(goDir)
//
//    val commandEnvironment = BuildEnvironment.environment(platform)
//    environment(commandEnvironment)
//
//    commandLine(
//      listOf(
//        BuildEnvironment.goBinary, "build", "-v",//"-x",
//        "-trimpath", "-buildmode=c-shared", "-o", libFile, modules
//      )
//    )
//
//
//    val out = project.serviceOf<StyledTextOutputFactory>().create("golibOutput")
//
//    doFirst {
//      out.style(StyledTextOutput.Style.Info).println("Building golib for $platform")
//      out.style(StyledTextOutput.Style.ProgressStatus).println("environment: $commandEnvironment")
//      out.style(StyledTextOutput.Style.ProgressStatus)
//        .println("commandLine: ${commandLine.joinToString(" ")}")
//    }
//    doLast {
//      if (didWork) out.style(StyledTextOutput.Style.Success)
//        .println("Finished building golib for $platform")
//    }
//  }
//
//
//  fun appendToEnvironment(key: String, value: String, separator: String = " ") {
//    environment(key, environment.getOrDefault(key, null).let {
//      if (it == null) value else "$it$separator$value"
//    })
//  }
//
//}
//
