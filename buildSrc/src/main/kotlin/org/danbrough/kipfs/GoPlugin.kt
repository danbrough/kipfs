package org.danbrough.kipfs

import org.gradle.api.Plugin
import org.gradle.api.Project

const val GO_EXTENSION_NAME = "goLib"

open class GoExtension(val project: Project){

}

class GoPlugin : Plugin<Project> {
  override fun apply(target: Project) {
   // println("applying $this to $target")
    target.extensions.create(GO_EXTENSION_NAME,GoExtension::class.java,target)
  }
}