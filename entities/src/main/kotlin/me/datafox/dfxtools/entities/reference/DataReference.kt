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

import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.EntityData
import me.datafox.dfxtools.entities.type.SClass

/** @author Lauri "datafox" Heino */
@Serializable
data class DataReference<T : Any>(val components: ComponentReference, val dataType: SClass<T>, val filter: DataFilter) {
    fun get(): List<EntityData<T>> =
        components.get().flatMap { it.getDataMap(dataType).values }.filter { filter.matches(dataType, it) }
}
