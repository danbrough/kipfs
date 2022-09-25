import BuildEnvironment.nativeTargets
import BuildEnvironment.platformNameCapitalized
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("multiplatform") apply false
  id("org.jetbrains.dokka")
  id("com.android.library") apply false
  id("org.jetbrains.kotlin.android") apply false
  //id("org.jetbrains.kotlin.jvm") apply false
  //id("com.android.application") apply false
//  id("org.jetbrains.kotlin.android")
  id("org.danbrough.kotlinxtras.binaries")
  id("org.danbrough.kotlinxtras.sonatype")
  `maven-publish`
  signing
}

ProjectProperties.init(project)

group = ProjectProperties.projectGroup
version = ProjectProperties.buildVersionName

println("GROUP: $group")


allprojects {
  repositories {
    maven("https://s01.oss.sonatype.org/content/groups/staging/")
    
    mavenCentral()
    google()
    // maven("https://s01.oss.sonatype.org/content/repositories/releases/")
  }
  
  
  tasks.withType<AbstractTestTask>() {
    testLogging {
      events = setOf(
        TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED
      )
      exceptionFormat = TestExceptionFormat.FULL
      showStandardStreams = true
      showStackTraces = true
    }
    outputs.upToDateWhen {
      false
    }
  }
  
  tasks.withType(KotlinCompile::class) {
    kotlinOptions {
      jvmTarget = ProjectProperties.KOTLIN_JVM_VERSION
    }
  }
  
  tasks.withType<JavaCompile>().all {
    sourceCompatibility = JavaVersion.VERSION_11.toString()
    targetCompatibility = JavaVersion.VERSION_11.toString()
  }
  
  
}


tasks.register<Delete>("deleteDocs") {
  setDelete(file("docs/api"))
}

tasks.register<Copy>("copyDocs") {
  dependsOn("deleteDocs")
  from(buildDir.resolve("dokka"))
  destinationDir = file("docs/api")
}

tasks.dokkaHtmlMultiModule.configure {
  outputDirectory.set(buildDir.resolve("dokka"))
  finalizedBy("copyDocs")
}

val javadocJar by tasks.registering(Jar::class) {
  archiveClassifier.set("javadoc")
  from(tasks.dokkaHtml)
}

//nexusPublishing {
//  repositories {
//    sonatype {
//      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
//      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
//    }
//  }
//}

sonatype {
}

val sonatypeExtension = extensions.findByType(org.danbrough.kotlinxtras.sonatype.SonatypeExtension::class)
println("SonaType extension user: ${sonatypeExtension?.username}")

allprojects {
  
  group = ProjectProperties.projectGroup
  version = ProjectProperties.buildVersionName
  
  afterEvaluate {
    extensions.findByType(PublishingExtension::class) ?: return@afterEvaluate
    apply<SigningPlugin>()
    
    
    publishing {
      
      publications.all {
        if (this !is MavenPublication) return@all
        
        if (project.hasProperty("publishDocs")) artifact(javadocJar)
        
        pom {
          
          name.set("KIPFS")
          description.set("Kotlin IPFS client api and embedded node")
          url.set("https://github.com/danbrough/kipfs/")
          
          licenses {
            license {
              name.set("Apache-2.0")
              url.set("https://opensource.org/licenses/Apache-2.0")
            }
          }
          
          scm {
            connection.set("scm:git:git@github.com:danbrough/kipfs.git")
            developerConnection.set("scm:git:git@github.com:danbrough/kipfs.git")
            url.set("https://github.com/danbrough/kipfs/")
          }
          
          issueManagement {
            system.set("GitHub")
            url.set("https://github.com/danbrough/kipfs/issues")
          }
          
          developers {
            developer {
              id.set("danbrough")
              name.set("Dan Brough")
              email.set("dan@danbrough.org")
              organizationUrl.set("https://danbrough.org")
            }
          }
        }
        
      }
    }
    
    signing {
      sign(publishing.publications)
    }
  }
}

afterEvaluate {
  tasks.create("publishMac") {
    nativeTargets.filter { it.family.isAppleFamily }
      .map {
        getTasksByName("publish${it.platformNameCapitalized}PublicationToSonatypeRepository", true)
      }.flatMap { it.toList() }.map { it.path }.distinct().also {
        dependsOn(it)
      }
  }
}

