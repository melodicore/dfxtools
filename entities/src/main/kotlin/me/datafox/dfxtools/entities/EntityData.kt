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

import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.Handled
import me.datafox.dfxtools.invalidation.AbstractObservable
import me.datafox.dfxtools.invalidation.property.InvalidatorProperty

/** @author Lauri "datafox" Heino */
class EntityData<T : Any>
@JvmOverloads
constructor(id: String, data: T, val saved: Boolean = true) : Handled, AbstractObservable() {
    override val handle = HandleManager.getOrCreateQualifiedHandle(id)
    var data: T by InvalidatorProperty(data)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EntityData<*>) return false

        if (handle != other.handle) return false
        if (data != other.data) return false

        return true
    }

    override fun hashCode(): Int {
        var result = handle.hashCode()
        result = 31 * result + data.hashCode()
        return result
    }

    override fun toString(): String {
        return "EntityData(handle=$handle, data=$data)"
    }
}
