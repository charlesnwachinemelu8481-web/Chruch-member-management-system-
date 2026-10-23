package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.Member
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("FaithConnect", appName)
  }

  @Test
  fun `create and verify member entity model`() {
    val member = Member(
      id = 100,
      fullName = "Pastor David Vance",
      phone = "+1 (555) 123-4567",
      email = "pastor.david@faithconnect.church",
      address = "77 Grace Blvd, Springfield",
      dateJoined = "2020-01-01",
      departments = "Choir & Music Ministry, Men of Faith Fellowship",
      householdRole = "Head of Household",
      householdName = "The Vance Family",
      status = "Active"
    )
    assertNotNull(member)
    assertEquals("Pastor David Vance", member.fullName)
    assertEquals("Active", member.status)
  }
}
