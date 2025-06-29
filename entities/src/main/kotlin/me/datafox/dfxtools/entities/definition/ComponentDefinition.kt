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

package me.datafox.dfxtools.entities.definition

import kotlin.reflect.KClass
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.Component
import me.datafox.dfxtools.entities.ComponentInitializer
import me.datafox.dfxtools.entities.Engine
import me.datafox.dfxtools.entities.Entity
import me.datafox.dfxtools.entities.definition.data.DataDefinition
import me.datafox.dfxtools.handles.get

/** @author Lauri "datafox" Heino */
@Serializable
data class ComponentDefinition(
    val id: String,
    val data: List<DataDefinition<*>>,
    val initializers: List<ComponentInitializer>,
) {
    @JvmOverloads
    constructor(
        component: Component,
        saveAll: Boolean = false,
    ) : this(
        component.handle.id,
        component.data.keys.flatMap { dataDefinitions(component, it, saveAll) },
        mutableListOf(),
    )

    fun build(entity: Entity) {
        val component = entity.components[id] ?: entity.createComponent(id)
        data.forEach { it.build(component) }
        initializers.forEach { component.addInitializer(it) }
    }

    companion object {
        private fun <T : Any> dataDefinitions(
            component: Component,
            type: KClass<T>,
            saveAll: Boolean,
        ): List<DataDefinition<*>> =
            component.getDataMap(type).mapNotNull { (_, data) ->
                if (saveAll || data.saved) Engine.Serialization.getType(type)?.convert(data)
                else null
            }
    }
}
