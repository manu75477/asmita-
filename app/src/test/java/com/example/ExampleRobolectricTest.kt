package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.EncryptionHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    assertEquals("Birthday Wish", appName)
  }

  @Test
  fun `verify pin hashing and encryption`() {
    val hashed = EncryptionHelper.hashPin("2026")
    assertTrue(EncryptionHelper.verifyPin("2026", hashed))

    val secret = "Happy Birthday Asmita Yadav!"
    val encrypted = EncryptionHelper.encryptData(secret)
    val decrypted = EncryptionHelper.decryptData(encrypted)
    assertEquals(secret, decrypted)
  }

  @Test
  fun `verify admin id and password authentication`() {
    val passwordHash = EncryptionHelper.hashString("Admin@2026")
    assertTrue(EncryptionHelper.verifyCredentials("admin", "Admin@2026", "admin", passwordHash))
    org.junit.Assert.assertFalse(EncryptionHelper.verifyCredentials("admin", "WrongPassword", "admin", passwordHash))
    org.junit.Assert.assertFalse(EncryptionHelper.verifyCredentials("fake_user", "Admin@2026", "admin", passwordHash))
  }
}
