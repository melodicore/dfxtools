/*
 * Copyright 2025 Lauri "datafox" Heino
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.datafox.dfxtools.utils

import io.github.oshai.kotlinlogging.KLogger

/**
 * Tools for logging with [KLogger].
 *
 * @author Lauri "datafox" Heino
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