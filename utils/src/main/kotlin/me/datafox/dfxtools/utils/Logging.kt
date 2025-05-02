package me.datafox.dfxtools.utils

import io.github.oshai.kotlinlogging.KLogger

/**
 * @author datafox
 */

object Logging {
    fun logThrow(logger: KLogger, message: String, throwable: (String) -> Throwable): Nothing {
        val thrown = throwable(message)
        logger.error(thrown) { message }
        throw thrown
    }
}