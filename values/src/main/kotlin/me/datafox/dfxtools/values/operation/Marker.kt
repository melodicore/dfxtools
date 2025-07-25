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

package me.datafox.dfxtools.values.operation

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.utils.Logging.logThrow
import java.math.BigDecimal

private val logger = KotlinLogging.logger {}

/** @author Lauri "datafox" Heino */
class Marker(val value: Int) : BigDecimal(0) {
    init {
        if (value < 0) logThrow(logger, "Marker value cannot be negative") { IllegalArgumentException(it) }
    }

    override fun toShort(): Short = 0

    override fun toByte(): Byte = 0
}
