package danbroid.kipfs.golib.tests

import KIPFSLibJNI
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Tests {

  companion object {
    val log = danbroid.logging.getLog(Tests::class)
  }


  @Test
  fun test1() {
    log.trace("creating shell from JNI..")
    val refnum = KIPFSLibJNI.createShellJNI("/ip4/192.168.1.4/tcp/5001")
    log.trace("created shell")

    KIPFSLibJNI.disposeGoObject(refnum)
  }

}
