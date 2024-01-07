import org.gradle.kotlin.dsl.extra

pluginManagement {

  repositories {
    val xtrasMavenDir = settings.extra.properties.let { properties ->
      properties.getOrDefault("xtras.dir.maven", null)?.toString()
        ?: properties.getOrDefault("xtras.dir", null)
          ?.toString()?.let { File(it).resolve("maven").absolutePath }
        ?: error("Gradle property xtras.dir is not set.")
    }

    maven(xtrasMavenDir)
    maven("https://s01.oss.sonatype.org/content/groups/staging")
    mavenCentral()
    gradlePluginPortal()
    google()
  }
}



dependencyResolutionManagement {
  versionCatalogs {
    create("libs") {
      from(files("../gradle/libs.versions.toml"))
    }
  }
}

