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

package me.datafox.dfxtools.entities

import me.datafox.dfxtools.entities.Engine.schemaSpace
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.Handled
import kotlin.reflect.KClass

/** @author Lauri "datafox" Heino */
data class Schema(override val handle: Handle, val data: Map<KClass<*>, Set<Handle>>) : Handled {
    constructor(
        id: String,
        data: Map<KClass<*>, Set<String>>,
    ) : this(
        schemaSpace.getOrCreateHandle(id),
        data
            .map { (type, set) ->
                type to set.map { HandleManager.getOrCreateQualifiedHandle(it) }.toSet()
            }
            .toMap(),
    )

    init {
        if (handle.space != schemaSpace) throw IllegalArgumentException()
    }

    fun isSchema(component: Component): Boolean =
        data.all { component.getDataMap(it.key).keys.containsAll(it.value) }
}
