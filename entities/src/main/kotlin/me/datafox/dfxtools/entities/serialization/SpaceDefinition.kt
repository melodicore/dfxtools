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
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.Space

@Serializable
data class SpaceDefinition(
    val id: String,
    val handles: List<HandleDefinition>
) {
    constructor(space: Space) : this(
        space.handle.id,
        space.handles.map { HandleDefinition(it) }
    )

    fun build() {
        if(id == HandleManager.spaceSpace.handle.id) {
            handles.map { it to it.buildSpaceHandle() }.forEach { (def, handle) -> def.populate(handle) }
        } else {
            val space = HandleManager.getOrCreateSpace(id)
            handles.map { it to it.build(space) }.forEach { (def, handle) -> def.populate(handle) }
        }
    }
}
