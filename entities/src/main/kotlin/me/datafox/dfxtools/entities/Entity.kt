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

import io.github.oshai.kotlinlogging.KotlinLogging
import me.datafox.dfxtools.entities.Engine.componentSpace
import me.datafox.dfxtools.entities.Engine.entitySpace
import me.datafox.dfxtools.handles.*
import me.datafox.dfxtools.utils.Logging.logThrow
import me.datafox.dfxtools.utils.collection.ListenableMap
import me.datafox.dfxtools.utils.collection.PluggableMapSpec
import me.datafox.dfxtools.utils.collection.toCollection
import java.util.*

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
class Entity(
    id: String
) : Handled {
    override val handle: Handle = entitySpace.getOrCreateHandle(id)
    private val index: Long = indexCounter++
    private var _added = false
    val added get() = _added
    private var _removed = false
    val removed get() = _removed
    private val _components: ListenableMap<Handle, Component> = ListenableMap(
        HandleMap.spec(componentSpace),
        componentSpec(this),
        TreeMap()
    )
    val components: ListenableMap.View<Handle, Component> get() {
        checkRemoved()
        return _components.view
    }

    fun createComponent(id: String): Component {
        checkRemoved()
        return Component(this, id).apply { _components.putHandled(this) }
    }

    operator fun get(id: String): Component? = components[id]

    fun getBySchema(id: String): List<Component> = components.values.filter { id in it.schemas }

    fun getBySchemas(ids: Iterable<String>): List<Component> =
        components.values.filter { it.schemas.keys.containsAll(ids.toCollection()) }

    fun hasSchema(id: String): Boolean = components.values.any { id in it.schemas }

    fun hasSchemas(ids: Iterable<String>): Boolean =
        components.values.any { it.schemas.keys.containsAll(ids.toCollection()) }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Entity) return false

        if(index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        return index.hashCode()
    }

    override fun toString(): String {
        return "Entity(added=$_added, removed=$_removed, components=$_components)"
    }

    internal fun added() {
        _added = true
    }

    internal fun removed() {
        _removed = true
    }

    private fun checkRemoved() {
        if(_removed) logThrow(logger, "This entity has been removed and cannot be accessed anymore") { IllegalStateException(it) }
    }

    companion object {
        private var indexCounter: Long = 0L

        fun componentSpec(entity: Entity): PluggableMapSpec<Handle, Component> = PluggableMapSpec(
            afterAdd = { _, v -> EntityCache.componentAdded(entity, v) },
            afterRemove = { _, v -> EntityCache.componentRemoved(entity, v) }
        )
    }
}