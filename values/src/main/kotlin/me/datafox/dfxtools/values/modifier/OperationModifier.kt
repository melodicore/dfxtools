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
import me.datafox.dfxtools.values.operation.DualParameterOperation
import me.datafox.dfxtools.values.operation.Operation
import me.datafox.dfxtools.values.operation.SingleParameterOperation
import me.datafox.dfxtools.values.operation.SourceOperation
import java.math.BigDecimal

private val logger = KotlinLogging.logger {}

/** @author Lauri "datafox" Heino */
class OperationModifier(priority: Int = 0, private val operation: Operation, vararg params: Value) :
    AbstractModifier(priority, *params) {
    init {
        if (operation.parameterCount > params.size) {
            logThrow(
                logger,
                "${params.size} parameters given to $this but $operation needs ${operation.parameterCount}",
            ) {
                IllegalArgumentException(it)
            }
        }
        if (operation.parameterCount < params.size) {
            logger.warn {
                "${params.size} parameters given to $this but $operation only needs ${operation.parameterCount}, some parameters will be ignored"
            }
        }
    }

    override fun apply(value: BigDecimal): BigDecimal {
        if (operation is SourceOperation) {
            return operation.apply(value)
        }
        if (operation is SingleParameterOperation) {
            return operation.apply(value, params[0].value)
        }
        if (operation is DualParameterOperation) {
            return operation.apply(value, params[0].value, params[1].value)
        }
        return operation.apply(value, *params.map { it.value }.toTypedArray())
    }

    override fun onInvalidated() {}
}
