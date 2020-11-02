package com.artemkaxboy.kotlin.corelib.exceptions

import java.net.UnknownHostException

object ExceptionUtils {

    fun Throwable.getMessage(prefix: String? = null): String {
        return getDetailedMessage(this).prefixReasonIfNeeded(prefix)
    }

    private fun String.prefixReasonIfNeeded(prefix: String? = null): String {
        return prefix
            .takeUnless { it.isNullOrBlank() }
            ?.let { this.prefixReason(it) }
            ?: this
    }

    private fun String.prefixReason(prefix: String): String = "$prefix: $this"

    private fun getDetailedMessage(throwable: Throwable): String {
        return when (throwable) {

            /* message returns hostname only for UnknownHostException */
            is UnknownHostException ->
                throwable.message?.prefixReason("Unknown host exception")

            /* message returns null for UnknownHostException */
            is NullPointerException ->
                "Null pointer exception"

            else ->
                throwable.message
        } ?: throwable.toString()
    }
}
