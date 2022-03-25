object OS {
  @JvmStatic
  val name: String = System.getProperty("os.name").toLowerCase()

  @JvmStatic
  val arch: String = System.getProperty("os.arch").toLowerCase()

  @JvmStatic
  val fullName = name + arch.capitalize()


  override fun toString() = "OS:$fullName"

}