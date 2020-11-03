package com.artemkaxboy.kotlin.corelib.exceptions

import com.artemkaxboy.kotlin.corelib.exceptions.ExceptionUtils.getMessage
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse

internal class ResultTest {

    @Test
    fun `pass if isSuccess works`() {

        Result.of { Unit }
            .isSuccess()
            .also { assertTrue(it) }

        Result.of { null!! }
            .isSuccess()
            .also { assertFalse(it) }
    }

    @Test
    fun `pass if getOrNull works`() {

        Result.of { Unit }
            .getOrNull()
            .also { assertNotNull(it) }

        Result.of { null!! }
            .getOrNull()
            .also { assertNull(it) }
    }

    @Test
    fun `pass if onSuccess works`() {
        var touched = false

        Result.of { null!! }
            .onSuccess { touched = true }
        assertFalse(touched)

        Result.of { Unit }
            .onSuccess { touched = true }
        assertTrue(touched)
    }

    @Test
    fun `pass if isFailure works`() {

        Result.of { Unit }
            .isFailure()
            .also { assertFalse(it) }

        Result.of { null!! }
            .isFailure()
            .also { assertTrue(it) }
    }

    @Test
    fun `pass if exceptionOrNull works`() {

        Result.of { Unit }
            .exceptionOrNull()
            .also { assertNull(it) }

        Result.of { null!! }
            .exceptionOrNull()
            .also { assertNotNull(it) }
    }

    @Test
    fun `pass if onFailure works`() {
        var touched = false

        Result.of { Unit }
            .onFailure { touched = true }
        assertFalse(touched)

        Result.of { null!! }
            .onFailure { touched = true }
        assertTrue(touched)
    }

    @Test
    fun `pass if success really successful`() {
        val expected = "My success result"
        val success = Result.success(expected)

        assertTrue(success.isSuccess())
        assertEquals(expected, success.getOrNull())
    }

    @Test
    fun `pass if failure really failure`() {
        val expected = "My failure message"
        val failure = Result.failure(expected)

        assertTrue(failure.isFailure())
        assertEquals(expected, failure.exceptionOrNull()?.getMessage())
    }

    @Test
    fun `pass if failure really failure with exception`() {
        val expectedMessage = "My failure message"
        val expectedExceptionMessage = "Exception message"
        val failure = Result.failure(Exception(expectedExceptionMessage), expectedMessage)

        assertTrue(failure.isFailure())

        val actualException = failure.exceptionOrNull()
        assertNotNull(actualException)

        actualException!!.getMessage()
            .also { assertTrue(it.contains(expectedMessage)) }
            .also { assertTrue(it.contains(expectedExceptionMessage)) }
    }

    @Test
    fun `pass if getOrElse works`() {

        Result.of { 1 }
            .getOrElse { 2 }
            .also { assertEquals(1, it) }

        Result.of { null!! }
            .getOrElse { 2 }
            .also { assertEquals(2, it) }
    }

    @Test
    fun `pass if map works`() {
        val initial = listOf(1, 2)
        val mapper = { i: Int -> i * i }
        val expected = initial.map(mapper)

        Result.success(listOf(1, 2))
            .map(mapper)
            .getOrNull()
            .also { assertEquals(expected, it) }
    }

    @Test
    fun `pass if cannot return null result`() {

        Result.of { null }
            .exceptionOrNull()
            .also { assertNotNull(it) }
            .also { assertTrue(it!!.getMessage().contains("null")) }
    }

    @Test
    fun `pass if returns passed message on failed`() {
        val expectedMessage = "My fail message"

        Result.of(expectedMessage) { null }
            .exceptionOrNull()
            .also { assertNotNull(it) }
            .also { assertTrue(it!!.getMessage().contains(expectedMessage)) }
    }
}
