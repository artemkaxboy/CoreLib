package com.github.artemkaxboy.corelib.exceptions

import com.github.artemkaxboy.corelib.exceptions.ExceptionUtils.getMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ResultTest {

    private val successValue = 1
    private val success = Result.success(successValue)

    private val failure = Result.failure<Int>("Failure message")

    @Test
    fun `pass if isSuccess works`() {

        assertThat(success.isSuccess).isTrue

        assertThat(failure.isSuccess).isFalse
    }

    @Test
    fun `pass if getOrNull works`() {

        assertThat(success.getOrNull())
            .isNotNull

        assertThat(failure.getOrNull())
            .isNull()
    }

    @Test
    fun `pass if onSuccess works`() {
        var touched = false

        failure.onSuccess { touched = true }
        assertThat(touched).isFalse

        success.onSuccess { touched = true }
        assertThat(touched).isTrue
    }

    @Test
    fun `pass if isFailure works`() {

        assertThat(success.isFailure).isFalse

        assertThat(failure.isFailure).isTrue
    }

    @Test
    fun `pass if exceptionOrNull works`() {

        assertThat(success.exceptionOrNull()).isNull()

        assertThat(failure.exceptionOrNull()).isNotNull
    }

    @Test
    fun `pass if onFailure works`() {
        var touched = false

        success.onFailure { touched = true }
        assertThat(touched).isFalse

        failure.onFailure { touched = true }
        assertThat(touched).isTrue
    }

    @Test
    fun `pass if success really successful`() {
        val expected = "My success result"
        val success = Result.success(expected)

        assertThat(success.isSuccess).isTrue
        assertThat(success.getOrNull()).isEqualTo(expected)
    }

    @Test
    fun `pass if failure really failure`() {
        val expected = "My failure message"
        val failure = Result.failure<Nothing>(expected)

        assertThat(failure.isFailure).isTrue
        assertThat(failure.exceptionOrNull()?.getMessage()).isNotNull.contains(expected)
    }

    @Test
    fun `pass if failure really failure with exception`() {
        val expectedMessage = "My failure message"
        val expectedExceptionMessage = "Exception message"
        val failure = Result.failure<Unit>(Exception(expectedExceptionMessage), expectedMessage)

        assertThat(failure.isFailure)

        assertThat(failure.exceptionOrNull())
            .isNotNull
            .hasMessageContaining(expectedMessage)
            .hasMessageContaining(expectedExceptionMessage)
    }

    @Test
    fun `pass if getOrElse works`() {

        val expectedFailValue = successValue + 10

        assertThat(success.getOrElse { expectedFailValue }).isEqualTo(successValue)
        assertThat(failure.getOrElse { expectedFailValue }).isEqualTo(expectedFailValue)
    }

    @Test
    fun `pass if map works`() {
        val initial = listOf(1, 2, 3, 4)
        val mapper = { i: Int -> i * i }
        val expected = initial.map(mapper)

        val result = Result.success(initial)
            .map(mapper)
            .getOrNull()

        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `pass if returns passed message on failed`() {
        val expectedMessage = "My fail message"

        val failure1 = Result.of(expectedMessage) { null!! }
            .exceptionOrNull()

        assertThat(failure1)
            .isNotNull
            .hasMessageContaining(expectedMessage)

        val failure2 = Result.of({ expectedMessage }) { null!! }
            .exceptionOrNull()

        assertThat(failure2)
            .isNotNull
            .hasMessageContaining(expectedMessage)
    }

    @Test
    fun `pass if equals work for success`() {
        val success2 = Result.of { successValue }
        val success3 = Result.success(successValue)
        val success4 = Result.success(successValue + 10)

        assertThat(success)
            .isEqualTo(success2)
            .isEqualTo(success3)
            .isNotEqualTo(success4)
    }

    @Test
    fun `pass if equals work for failure`() {
        val exception = failure.exceptionOrNull()

        assertThat(exception).isNotNull
        assertThat(exception!!.message).isNotNull

        val failure2 = Result.failure<String>(exception)
        val failure3 = Result.failure<String>(exception.message!!)

        assertThat(failure)
            .isEqualTo(failure2)
            .isNotEqualTo(failure3)
    }

    @Test
    fun `toString works`() {

        assertThat(success).asString().contains("Success")
        assertThat(success).asString().doesNotContain("Failure")

        assertThat(failure).asString().contains("Failure")
        assertThat(failure).asString().doesNotContain("Success")
    }

    @Test
    fun `pass if hashCode works`() {

        assertThat(success.hashCode())
            .isEqualTo(successValue.hashCode())

        assertThat(failure.hashCode())
            .isEqualTo(failure.exceptionOrNull().hashCode())
    }
}
