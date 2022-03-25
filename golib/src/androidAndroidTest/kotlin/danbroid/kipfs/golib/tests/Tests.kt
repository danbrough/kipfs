package danbroid.kipfs.golib.tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import dagCID
import getMessage
import getMessage2
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Tests {

  companion object {
    val log = danbroid.logging.getLog(Tests::class)
  }


  @Test
  fun test1() {
    log.trace("nothing happening")
  }

}
