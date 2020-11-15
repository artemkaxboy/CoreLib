package com.github.artemkaxboy.corelib.exceptions

import com.github.artemkaxboy.corelib.exceptions.ExceptionUtils.getMessage
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.net.HttpURLConnection
import java.net.URL
import java.net.UnknownHostException

internal class ExceptionUtilsTest {

    @Test
    fun `pass if getMessage returns original message`() {
        val expected = "Message 1"
        val exception = Exception(expected)

        Assertions.assertEquals(expected, exception.getMessage())
    }

    @Test
    fun `pass if getMessage returns Exception#toString with null message exception`() {
        val exception = Exception()

        Assertions.assertEquals(exception.toString(), exception.getMessage())
    }

    @Test
    fun `pass if getMessage returns message for UnknownHostException`() {
        val exception = Assertions.assertThrows(UnknownHostException::class.java) {
            val hostname = "http://locaihost"
            val url = URL(hostname)
            val con: HttpURLConnection = url.openConnection() as HttpURLConnection
            con.responseCode
        }

        Assertions.assertNotEquals(exception.toString(), exception.getMessage())
        Assertions.assertTrue(exception.getMessage().startsWith("Unknown host exception:"))
    }

    @Test
    fun `pass if getMessage returns message for NullPointerException`() {
        val exception = Assertions.assertThrows(NullPointerException::class.java) {
            null!!
        }

        Assertions.assertNotEquals(exception.toString(), exception.getMessage())
        Assertions.assertTrue(exception.getMessage().startsWith("Null pointer exception"))
    }

    @Test
    fun `pass if getMessage returns exception message`() {
        val expectedMessage = "Message 1"
        val exception = Exception(expectedMessage)

        Assertions.assertEquals(expectedMessage, exception.getMessage())
    }

    @Test
    fun `pass if getMessage adds prefix`() {
        val expectedMessage = "Message 1"
        val expectedPrefix = "Prefix"

        val exception = Exception(expectedMessage)
        val actualMessage = exception.getMessage(expectedPrefix)

        Assertions.assertTrue(actualMessage.startsWith(expectedPrefix))
        Assertions.assertTrue(actualMessage.endsWith(expectedMessage))
    }
}
