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

package me.datafox.dfxtools.entities.serialization

import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.Component
import me.datafox.dfxtools.entities.EntityData
import kotlin.reflect.KClass

/**
 * @author Lauri "datafox" Heino
 */
@Serializable
@ConsistentCopyVisibility
data class DataDefinition private constructor(
    val type: KClass<*>,
    val id: String,
    val saved: Boolean,
    val data: Map<String, String>
) {
    constructor(type: KClass<*>, data: EntityData<*>) : this(
        type,
        data.handle.id,
        data.saved,
        convertTyped(type, data)
    )

    fun build(component: Component) {
        buildTyped(component, type, id, data)
    }

    private fun <T : Any> buildTyped(component: Component, type: KClass<T>, id: String, data: Map<String, String>) {
        val converter = Serialization.getConverter(type) ?: throw IllegalArgumentException("No converter was found")
        component[type, id, saved] = converter.convert(data)
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        private fun <T : Any> convertTyped(type: KClass<T>, data: EntityData<*>): Map<String, String> {
            if(type.isInstance(data.data)) return getConverter(type).convert(data.data as T)
            throw ClassCastException()
        }

        private fun <T : Any> getConverter(type: KClass<T>): Converter<T> =
            Serialization.getConverter(type) ?: throw IllegalArgumentException("No converter was found")
    }
}
