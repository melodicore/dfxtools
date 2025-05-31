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

package me.datafox.dfxtools.nodes.node

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.utils.Logging.logThrow
import kotlin.reflect.cast

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
data class NodeInputInfo<T : Any>(
    val type: NodeType<T>,
    val allowedVariants: Set<NodeType.Variant<T>>,
    val validator: (T.() -> String?)? = null
) {
    init {
        if(allowedVariants.isEmpty()) {
            logThrow(logger, "Variants must not be empty") { IllegalArgumentException(it) }
        }
        if(!type.variants.values.containsAll(allowedVariants)) {
            logThrow(logger, "All allowed variants must be variants of the type") { IllegalArgumentException(it) }
        }
    }

    fun cast(value: Any): T = type.type.cast(value)
}