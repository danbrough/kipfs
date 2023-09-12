import org.gradle.kotlin.dsl.extra

pluginManagement {

  repositories {
    maven(settings.extra.properties["xtras.dir.maven"] ?: rootDir.resolve("maven"))
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

