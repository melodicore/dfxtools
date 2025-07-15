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
import me.datafox.dfxtools.values.operation.Operation
import me.datafox.dfxtools.values.operation.SourceOperation
import java.math.BigDecimal

private val logger = KotlinLogging.logger {}

/** @author Lauri "datafox" Heino */
class ChainedModifier(priority: Int = 0, vararg operations: Pair<Operation, List<Value>>) :
    AbstractModifier(priority, *operations.flatMap { it.second }.toTypedArray()) {
    val operations: List<Pair<Operation, List<Value>>> = operations.toList()

    init {
        operations.forEach { (op, vals) ->
            if (op.parameterCount != vals.size) {
                logThrow(logger, "Parameter count mismatch") { IllegalArgumentException(it) }
            }
        }
    }

    override fun apply(value: BigDecimal): BigDecimal {
        if (operations.isEmpty()) return value
        var number = value
        operations.forEach { (op, vals) ->
            number = op.apply(number, *vals.map { it.value }.toTypedArray())
        }
        return number
    }

    override fun onInvalidated() { }

    class Builder internal constructor(private val priority: Int) {
        private val operations: MutableList<Pair<Operation, List<Value>>> = mutableListOf()

        fun add(block: () -> SourceOperation) {
            operations.add(block() to emptyList())
        }

        fun add(operation: Operation, block: MutableList<Value>.() -> Unit) {
            val list: MutableList<Value> = mutableListOf()
            list.block()
            if (operation.parameterCount != list.size) {
                logThrow(logger, "Parameter count mismatch") { IllegalArgumentException(it) }
            }
            operations.add(operation to list)
        }

        internal fun build(): ChainedModifier =
            ChainedModifier(priority, *operations.toTypedArray())
    }

    companion object {
        @JvmOverloads
        @JvmStatic
        fun builder(priority: Int = 0, block: Builder.() -> Unit): ChainedModifier {
            val builder = Builder(priority)
            builder.block()
            return builder.build()
        }
    }
}
