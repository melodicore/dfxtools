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

package me.datafox.dfxtools.values.modifier

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.values.Value
import me.datafox.dfxtools.values.operation.Marker
import me.datafox.dfxtools.values.operation.Operation
import java.math.BigDecimal

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
class MappingModifier(
    priority: Int = 0,
    vararg operations: Pair<Operation, List<Value>>
) : AbstractModifier(priority, *operations.flatMap { it.second }.toTypedArray()) {
    val operations: List<Pair<Operation, List<Value>>> = operations.toList()

    init {
        operations.forEach { (op, vals) ->
            if(op.parameterCount + 1 != vals.size) {
                logThrow(logger, "Parameter count mismatch") { IllegalArgumentException(it) }
            }
        }
    }

    override fun apply(value: BigDecimal): BigDecimal {
        if(operations.isEmpty()) return value
        val markers: MutableList<BigDecimal> = mutableListOf(value)
        operations.forEach { (op, vals) ->
            val list = vals.map { it.value }.map { if(it is Marker) markers[it.value] else it }
            markers.add(op.apply(list.first(), *list.subList(1, op.parameterCount + 1).toTypedArray()))
        }
        return markers.last()
    }

    class Builder internal constructor(private val priority: Int) {
        private val operations: MutableList<Pair<Operation, List<Value>>> = mutableListOf()

        fun add(operation: Operation, block: MutableList<Value>.() -> Unit) {
            val list: MutableList<Value> = mutableListOf()
            list.block()
            if(operation.parameterCount + 1 != list.size) {
                logThrow(logger, "Parameter count mismatch") { IllegalArgumentException(it) }
            }
            list.map { it.value }.forEach { if(it is Marker && it.value > operations.size) logThrow(logger, "Reference to operation that is not yet executed at this point") { s -> IllegalArgumentException(s) } }
            operations.add(operation to list)
        }

        internal fun build(): MappingModifier = MappingModifier(priority, *operations.toTypedArray())
    }

    companion object {
        @JvmOverloads
        @JvmStatic
        fun builder(priority: Int = 0, block: Builder.() -> Unit): MappingModifier {
            val builder = Builder(priority)
            builder.block()
            return builder.build()
        }
    }
}