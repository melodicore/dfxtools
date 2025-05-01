package me.datafox.dfxtools.utils

import io.github.oshai.kotlinlogging.KLogger

/**
 * @author datafox
 */

fun logThrow(logger: KLogger, message: String, throwable: (String) -> Throwable): Nothing {
    val thrown = throwable(message)
    logger.error(thrown) { message }
    throw thrown
}