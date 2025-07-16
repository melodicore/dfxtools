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

package me.datafox.dfxtools.entities.reference

import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.Engine
import me.datafox.dfxtools.entities.Entity

@Polymorphic
interface EntityFilter {
    fun matches(entity: Entity): Boolean

    @Serializable
    @SerialName("all")
    data object All : EntityFilter {
        override fun matches(entity: Entity): Boolean = true
    }

    @Serializable
    @SerialName("handle")
    data class Handle(val filter: HandleFilter) : EntityFilter {
        override fun matches(entity: Entity): Boolean = filter.matches(entity.handle)
    }

    @Serializable
    @SerialName("component")
    data class Component(val filters: List<ComponentFilter>) : EntityFilter {
        override fun matches(entity: Entity): Boolean = filters.all { entity.components.values.any(it::matches) }
    }

    @Serializable
    @SerialName("schema")
    data class Schema(val handles: List<String>) : EntityFilter {
        private val _handles by lazy { Engine.schemaSpace.getOrCreateHandles(handles) }

        override fun matches(entity: Entity): Boolean =
            _handles.all { Engine.Cache.entitiesBySchema[it]?.contains(entity) ?: entity.hasSchemas(handles) }
    }

    @Serializable
    @SerialName("and")
    data class And(val filters: List<EntityFilter>) : EntityFilter {
        override fun matches(entity: Entity): Boolean = filters.all { it.matches(entity) }
    }

    @Serializable
    @SerialName("or")
    data class Or(val filters: List<EntityFilter>) : EntityFilter {
        override fun matches(entity: Entity): Boolean = filters.any { it.matches(entity) }
    }

    @Serializable
    @SerialName("not")
    data class Not(val filter: EntityFilter) : EntityFilter {
        override fun matches(entity: Entity): Boolean = !filter.matches(entity)
    }
}
