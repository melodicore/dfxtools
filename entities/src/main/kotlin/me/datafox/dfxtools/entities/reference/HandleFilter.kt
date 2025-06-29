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
import kotlinx.serialization.Transient
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleManager

@Polymorphic
interface HandleFilter {
    fun matches(handle: Handle): Boolean

    @Serializable
    @SerialName("all")
    data object All : HandleFilter {
        override fun matches(handle: Handle): Boolean = true
    }

    @Serializable
    @SerialName("id")
    data class Id(val id: String) : HandleFilter {
        @Transient private val hasSpace: Boolean = id.contains('@')

        override fun matches(handle: Handle): Boolean =
            if (hasSpace) id == handle.toString() else id == handle.id
    }

    @Serializable
    @SerialName("space")
    data class Space(val space: String) : HandleFilter {
        private val _space by lazy { HandleManager.getOrCreateSpace(space) }

        override fun matches(handle: Handle): Boolean = handle in _space.handles
    }

    @Serializable
    @SerialName("tag")
    data class Tag(val tag: String) : HandleFilter {
        private val _tag by lazy { HandleManager.tagSpace.getOrCreateHandle(tag) }

        override fun matches(handle: Handle): Boolean = _tag in handle
    }

    @Serializable
    @SerialName("group")
    data class Group(val group: String) : HandleFilter {
        private val _group by lazy {
            val split = group.split(':')
            HandleManager.getOrCreateSpace(split[0]).getOrCreateGroup(split[1])
        }

        override fun matches(handle: Handle): Boolean = handle in _group
    }

    @Serializable
    @SerialName("and")
    data class And(val filters: List<HandleFilter>) : HandleFilter {
        override fun matches(handle: Handle): Boolean = filters.all { it.matches(handle) }
    }

    @Serializable
    @SerialName("or")
    data class Or(val filters: List<HandleFilter>) : HandleFilter {
        override fun matches(handle: Handle): Boolean = filters.any { it.matches(handle) }
    }

    @Serializable
    @SerialName("not")
    data class Not(val filter: HandleFilter) : HandleFilter {
        override fun matches(handle: Handle): Boolean = !filter.matches(handle)
    }
}
