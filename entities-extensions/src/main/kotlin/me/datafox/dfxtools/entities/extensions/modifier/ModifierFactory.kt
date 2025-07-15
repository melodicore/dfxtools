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

package me.datafox.dfxtools.entities.extensions.modifier

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.Component
import me.datafox.dfxtools.values.modifier.ChainedModifier
import me.datafox.dfxtools.values.modifier.MappingModifier
import me.datafox.dfxtools.values.modifier.Modifier
import me.datafox.dfxtools.values.modifier.OperationModifier

@Polymorphic
interface ModifierFactory {
    fun build(component: Component): Modifier

    @Serializable
    @SerialName("operation")
    data class Operation(
        val priority: Int,
        val operation: me.datafox.dfxtools.values.operation.Operation,
        val params: List<ModifierParameter>,
    ) : ModifierFactory {
        override fun build(component: Component): Modifier =
            OperationModifier(priority, operation, *params.map { it.get(component) }.toTypedArray())
    }

    @Serializable
    @SerialName("chained")
    data class Chained(
        val priority: Int,
        val operations:
            List<Pair<me.datafox.dfxtools.values.operation.Operation, List<ModifierParameter>>>,
    ) : ModifierFactory {
        override fun build(component: Component): Modifier =
            ChainedModifier(
                priority,
                *operations
                    .map { (op, params) -> op to params.map { it.get(component) } }
                    .toTypedArray(),
            )
    }

    @Serializable
    @SerialName("mapping")
    data class Mapping(
        val priority: Int,
        val operations:
            List<Pair<me.datafox.dfxtools.values.operation.Operation, List<ModifierParameter>>>,
    ) : ModifierFactory {
        override fun build(component: Component): Modifier =
            MappingModifier(
                priority,
                *operations
                    .map { (op, params) -> op to params.map { it.get(component) } }
                    .toTypedArray(),
            )
    }
}
