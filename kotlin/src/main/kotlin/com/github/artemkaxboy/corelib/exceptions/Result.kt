@file:Suppress("UNCHECKED_CAST", "RedundantVisibilityModifier")

package com.github.artemkaxboy.corelib.exceptions

import com.github.artemkaxboy.corelib.exceptions.ExceptionUtils.getMessage
import java.io.Serializable

// @doc https://stackoverflow.com/a/59168658/1452052

/**
 * A discriminated union that encapsulates a successful outcome with a value of type [T]
 * or a failure with an arbitrary [Throwable] exception.
 *
 * @see [kotlin.Result]
 */
class Result<out T>(
    val value: Any?
) {

    /**
     * Returns `true` if this instance represents a successful outcome.
     * In this case [isFailure] returns `false`.
     */
    val isSuccess: Boolean get() = !isFailure

    /**
     * Returns `true` if this instance represents a failed outcome.
     * In this case [isSuccess] returns `false`.
     */
    val isFailure: Boolean get() = value is Failure

    /**
     * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or `null`
     * if it is [failure][Result.isFailure].
     *
     * This function is a shorthand for `getOrElse { null }` (see [getOrElse]).
     */
    fun getOrNull(): T? =
        when {
            isFailure -> null
            else -> value as T
        }

    /**
     * Returns the encapsulated [Throwable] exception if this instance represents [failure][isFailure] or `null`
     * if it is [success][isSuccess].
     *
     * This function is a shorthand for `fold(onSuccess = { null }, onFailure = { it })` (see [fold]).
     */
    fun exceptionOrNull(): Throwable? =
        when (value) {
            is Failure -> value.exception
            else -> null
        }

    /**
     * Returns a string `Success(v)` if this instance represents [success][Result.isSuccess]
     * where `v` is a string representation of the value or a string `Failure(x)` if
     * it is [failure][isFailure] where `x` is a string representation of the exception.
     */
    public override fun toString(): String =
        when (value) {
            is Failure -> value.toString() // "Failure($exception)"
            else -> "Success($value)"
        }

    override fun equals(other: Any?): Boolean = other is Result<*> && value == other.value
    override fun hashCode(): Int = value.hashCode()

    /**
     * Companion object for [Result] class that contains its constructor functions
     * [success] and [failure].
     */
    public companion object {
        /**
         * Returns an instance that encapsulates the given [value] as successful value.
         */
        public fun <T> success(value: T): Result<T> =
            Result(value)

        /**
         * Returns an instance that encapsulates [Throwable] exception with given [message] as failure.
         *
         * @param message exception will be wrapped in [Exception] with given message.
         */
        public fun <T> failure(message: String): Result<T> =
            failure(Exception(message))

        /**
         * Returns an instance that encapsulates the given [Throwable] [exception] as failure.
         *
         * @param message exception will be wrapped in [Exception] with given message.
         */
        public fun <T> failure(exception: Throwable, message: String? = null): Result<T> {
            val extendedException = message
                ?.let { Exception(exception.getMessage(message), exception) }
                ?: exception
            return Result(Failure(extendedException))
        }

        /**
         * Returns the result of [block] for the encapsulated value if this instance represents [success][Result.isSuccess]
         * or the encapsulated [Throwable] exception with given [errorMessage] if it is [failure][Result.isFailure].
         */
        inline fun <R : Any> of(errorMessage: String? = null, block: () -> R): Result<R> {
            return of({ errorMessage }, block)
        }

        /**
         * Returns the result of [block] for the encapsulated value if this instance represents [success][Result.isSuccess]
         * or the encapsulated [Throwable] exception with given [errorMessage] if it is [failure][Result.isFailure].
         */
        inline fun <R : Any> of(errorMessage: () -> String?, block: () -> R): Result<R> {
            return try {
                success(block())
            } catch (e: Throwable) {
                failure(e, errorMessage())
            }
        }
    }

    internal class Failure(
        @JvmField
        val exception: Throwable
    ) : Serializable {
        override fun equals(other: Any?): Boolean =
            other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }
}

/**
 * Returns the encapsulated value if this instance represents [success][Result.isSuccess] or the
 * result of [onFailure] function for the encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [onFailure] function.
 */
public inline fun <R, T : R> Result<T>.getOrElse(onFailure: (exception: Throwable) -> R): R {
    return when (val exception = exceptionOrNull()) {
        null -> value as T
        else -> onFailure(exception)
    }
}

/**
 * Returns the encapsulated result of the given [transform] function applied to the encapsulated value
 * if this instance represents [success][Result.isSuccess] or the
 * original encapsulated [Throwable] exception if it is [failure][Result.isFailure].
 *
 * Note, that this function rethrows any [Throwable] exception thrown by [transform] function.
 * See [mapCatching] for an alternative that encapsulates exceptions.
 */
fun <T : Any, I : List<T>, R : Any> Result<I>.map(transform: (T) -> R): Result<List<R>> {

    return getOrElse { return Result.failure(it) }
        .map(transform)
        .let(Result.Companion::success)
}

/**
 * Performs the given [action] on the encapsulated [Throwable] exception if this instance represents [failure][Result.isFailure].
 * Returns the original `Result` unchanged.
 */
public inline fun <T> Result<T>.onFailure(action: (exception: Throwable) -> Unit): Result<T> {
    exceptionOrNull()?.let { action(it) }
    return this
}

/**
 * Performs the given [action] on the encapsulated value if this instance represents [success][Result.isSuccess].
 * Returns the original `Result` unchanged.
 */
public inline fun <T> Result<T>.onSuccess(action: (value: T) -> Unit): Result<T> {
    if (isSuccess) action(value as T)
    return this
}

/*
To use with org.springframework.data.domain.Page

fun <T : Any, I : Page<T>, R : Any> Result<I>.mapPage(block: (T) -> R): Result<Page<R>> {

    return getOrElse { return Result.failure(it) }
        .map(block)
        .let(Result.Companion::success)
}
*/
