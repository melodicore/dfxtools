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

import me.datafox.dfxtools.entities.Engine.componentSpace
import me.datafox.dfxtools.entities.Engine.schemaSpace
import me.datafox.dfxtools.handles.Handle
import me.datafox.dfxtools.handles.HandleManager
import me.datafox.dfxtools.handles.HandleSet
import me.datafox.dfxtools.handles.Handled
import kotlin.reflect.KClass

/** @author Lauri "datafox" Heino */
data class Schema(
    override val handle: Handle,
    val componentHandles: Set<Handle>,
    val data: Map<KClass<*>, Set<Entry<*>>>,
) : Handled {
    init {
        if (handle.space != schemaSpace) throw IllegalArgumentException()
    }

    fun isSchema(component: Component): Boolean = data.values.all { it.all { e -> isSchema(component, e) } }

    private fun <T : Any> isSchema(component: Component, entry: Entry<T>): Boolean {
        if (componentHandles.isNotEmpty() && component.handle !in componentHandles) return false
        val data = component.getDataMap(entry.type)
        return entry.handle in data && entry.predicate(data[entry.handle]!!.data)
    }

    data class Entry<T : Any>(val type: KClass<T>, val handle: Handle, val predicate: (T) -> Boolean = { true })

    class Builder {
        val handles: HandleSet = HandleSet(componentSpace)
        val entries: MutableMap<KClass<*>, MutableSet<Entry<*>>> = mutableMapOf()

        fun component(id: String) {
            handles.add(componentSpace.getOrCreateHandle(id))
        }

        fun component(handle: Handle) {
            handles.add(handle)
        }

        fun <T : Any> entry(type: KClass<T>, handle: Handle, predicate: (T) -> Boolean = { true }) {
            get(type).add(Entry(type, handle, predicate))
        }

        fun <T : Any> entry(type: KClass<T>, id: String, predicate: (T) -> Boolean = { true }) {
            entry(type, HandleManager.getOrCreateQualifiedHandle(id), predicate)
        }

        @Suppress("UNCHECKED_CAST")
        private fun <T : Any> get(type: KClass<T>): MutableSet<Entry<T>> {
            return entries[type] as? MutableSet<Entry<T>>
                ?: mutableSetOf<Entry<T>>().apply { entries.put(type, this as MutableSet<Entry<*>>) }
        }
    }

    companion object {
        operator fun invoke(handle: Handle, block: Builder.() -> Unit): Schema {
            val builder = Builder()
            builder.block()
            return Schema(handle, builder.handles, builder.entries)
        }

        operator fun invoke(id: String, block: Builder.() -> Unit) = Schema(schemaSpace.getOrCreateHandle(id), block)
    }
}
