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
class MappingOperation(vararg operations: Operation) : Operation {
    val operations: List<Operation> = operations.toList()
    override val parameterCount: Int = operations.sumOf { it.parameterCount + 1 }

    init {
        if (operations.isEmpty()) logThrow(logger, "Operations must not be empty") { IllegalArgumentException(it) }
    }

    override fun apply(source: BigDecimal, vararg params: BigDecimal): BigDecimal {
        if (params.size < parameterCount) {
            logThrow(logger, "Too few parameters given to $this, $parameterCount are required") {
                IllegalArgumentException(it)
            }
        }
        if (params.size > parameterCount) {
            logger.warn { "More than $parameterCount parameters given to $this, some parameters will be ignored" }
        }
        var index = 0
        val markers: MutableList<BigDecimal> = mutableListOf(source)
        operations.forEach {
            var first: BigDecimal? = null
            val list = ArrayList<BigDecimal>(it.parameterCount)
            for (i in 0 until it.parameterCount + 1) {
                val number = params[index + i]
                val actual = if (number is Marker) markers[number.value] else number
                if (first == null) first = actual else list.add(actual)
            }
            markers.add(it.apply(first!!, *list.toTypedArray()))
            index += it.parameterCount + 1
        }
        return markers.last()
    }
}
