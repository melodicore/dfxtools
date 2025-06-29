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
import me.datafox.dfxtools.entities.Component
import me.datafox.dfxtools.entities.Engine
import me.datafox.dfxtools.entities.TypeSerializer
import kotlin.reflect.KClass

@Polymorphic
interface ComponentFilter {
    fun matches(component: Component): Boolean

    @Serializable
    @SerialName("all")
    data object All : ComponentFilter {
        override fun matches(component: Component): Boolean = true
    }

    @Serializable
    @SerialName("handle")
    data class Handle(val filter: HandleFilter) : ComponentFilter {
        override fun matches(component: Component): Boolean = filter.matches(component.handle)
    }

    @ConsistentCopyVisibility
    @Suppress("SERIALIZER_TYPE_INCOMPATIBLE")
    @Serializable
    @SerialName("data")
    data class Data<T : KClass<out Any>> private constructor(
        val filter: HandleFilter,
        @Serializable(with = TypeSerializer::class) val dataType: T
    ) : ComponentFilter {
        override fun matches(component: Component): Boolean = component.getDataMap(dataType).keys.any { filter.matches(it) }

        companion object {
            operator fun <T : Any> invoke(type: KClass<T>, filter: HandleFilter): Data<KClass<T>> = Data(filter, type)
        }
    }

    @Serializable
    @SerialName("schema")
    data class Schema(val handles: List<String>) : ComponentFilter {
        private val _handles by lazy { Engine.schemaSpace.getOrCreateHandles(handles) }

        override fun matches(component: Component): Boolean = _handles.all { Engine.schemas[it]?.isSchema(component) ?: false }
    }

    @Serializable
    @SerialName("and")
    class And(val filters: List<ComponentFilter>) : ComponentFilter {
        override fun matches(component: Component): Boolean = filters.all { it.matches(component) }
    }

    @Serializable
    @SerialName("or")
    data class Or(val filters: List<ComponentFilter>) : ComponentFilter {
        override fun matches(component: Component): Boolean = filters.any { it.matches(component) }
    }

    @Serializable
    @SerialName("not")
    data class Not(val filter: ComponentFilter) : ComponentFilter {
        override fun matches(component: Component): Boolean = !filter.matches(component)
    }
}
