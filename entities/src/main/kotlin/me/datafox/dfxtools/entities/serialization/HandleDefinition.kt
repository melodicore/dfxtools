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
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.Space

@Serializable
data class HandleDefinition(
    val id: String,
    val tags: List<String>,
) {
    constructor(handle: Handle) : this(handle.id, handle.tags.map { it.id })

    fun build(space: Space): Handle = space.getOrCreateHandle(id)

    fun buildSpaceHandle(): Handle {
        if(!id.contains(':')) return HandleManager.getOrCreateSpace(id).handle
        val split = id.split(':')
        if(split.size != 2) throw IllegalArgumentException("Invalid id")
        return HandleManager.getOrCreateSpace(split[0]).getOrCreateGroup(split[1]).handle
    }

    fun populate(handle: Handle) = handle.tags.addAll(tags)
}
