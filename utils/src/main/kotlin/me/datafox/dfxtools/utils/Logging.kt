package me.datafox.dfxtools.utils

import io.github.oshai.kotlinlogging.KLogger

/**
 * Tools for logging with [KLogger].
 *
 * @author datafox
 */

object Logging {
    /**
     * Logs an exception with [KLogger.error] and throws it.
     *
     * @param logger [KLogger] to be used.
     * @param message message to be logged and thrown
     * @param throwable lambda that returns a [Throwable] to be thrown. [message] is always given to it as a parameter.
     */
    fun logThrow(logger: KLogger, message: String, throwable: (String) -> Throwable): Nothing {
        val thrown = throwable(message)
        logger.error(thrown) { message }
        throw thrown
    }
}