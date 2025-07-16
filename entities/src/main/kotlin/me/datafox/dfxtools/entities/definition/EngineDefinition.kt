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

import kotlinx.serialization.Serializable
import me.datafox.dfxtools.entities.Engine
import me.datafox.dfxtools.handles.HandleManager

/** @author Lauri "datafox" Heino */
@Serializable
data class EngineDefinition(val spaces: List<SpaceDefinition>, val entities: List<EntityDefinition>) {
    @JvmOverloads
    constructor(
        saveAll: Boolean = false,
        saveInitializers: Boolean = false,
    ) : this(getSpaces(), Engine.entities.values.map { EntityDefinition(it, saveAll, saveInitializers) })

    companion object {
        private fun getSpaces(): List<SpaceDefinition> {
            return HandleManager.spaces.values.map { SpaceDefinition(it) }.filter { it.handles.isNotEmpty() }
        }
    }
}
