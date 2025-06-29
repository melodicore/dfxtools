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

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.Component
import me.datafox.dfxtools.entities.ComponentInitializer
import me.datafox.dfxtools.entities.reference.DataReference
import me.datafox.dfxtools.values.ModifiableValue
import me.datafox.dfxtools.values.ValueMap

/**
 * @author Lauri "datafox" Heino
 */
@Serializable
data class ModifierInitializer(
    val modifier: ModifierFactory,
    val targetValues: DataReference<@Contextual ModifiableValue>,
    val targetMaps: DataReference<@Contextual ValueMap>
) : ComponentInitializer {
    override fun initialize(component: Component) {
        val mod = modifier.build()
        targetValues.get().forEach { it.data.modifiers.add(mod) }
        targetMaps.get().forEach { it.data.modifiers.add(mod) }
    }
}