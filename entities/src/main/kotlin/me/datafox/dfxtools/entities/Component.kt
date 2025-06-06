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
import me.datafox.dfxtools.entities.Engine.dataSpace
import me.datafox.dfxtools.entities.Engine.schemaSpace
import me.datafox.dfxtools.handles.*
import me.datafox.dfxtools.invalidation.AbstractObservableObserver
import me.datafox.dfxtools.utils.collection.PluggableMap
import me.datafox.dfxtools.utils.collection.PluggableMapSpec
import java.util.*
import kotlin.reflect.KClass

private val logger = KotlinLogging.logger {}

/**
 * @author Lauri "datafox" Heino
 */
class Component internal constructor(
    val entity: Entity,
    id: String
) : Handled, AbstractObservableObserver() {
    override val handle = componentSpace.getOrCreateHandle(id)
    private val index: Long = indexCounter++
    private val identifier = Any()
    private val _data: MutableMap<KClass<*>, HandleMap<EntityData<*>>> = mutableMapOf()
    val data: Map<KClass<*>, Map<Handle, EntityData<*>>> = _data
    private val _schemas: PluggableMap<Handle, Schema> = PluggableMap(
        TreeMap(),
        PluggableMapSpec(HandleMap.spec(schemaSpace), schemaSpec(this))
    )
    val schemas: Map<Handle, Schema> = _schemas

    operator fun <T : Any> get(type: KClass<T>, id: String): T? = getDataMap(type)[id]?.data

    fun <T : Any> getData(type: KClass<T>, id: String): EntityData<T>? = getDataMap(type)[id]

    fun <T : Any> getDataMap(type: KClass<T>): Map<Handle, EntityData<T>> = getDataMapInternal(type)

    operator fun contains(type: KClass<*>): Boolean = _data[type]?.isNotEmpty() ?: false

    fun contains(type: KClass<*>, id: String): Boolean = _data[type]?.contains(id) ?: false

    @JvmOverloads
    operator fun <T : Any> set(type: KClass<T>, id: String, saved: Boolean = true, data: T) {
        putInternal(type, id, saved, data)
        refreshSchemas()
    }

    fun refreshSchemas() {
        Engine.schemas.values
            .filterNot { it.handle in _schemas }
            .filter { it.isSchema(this) }
            .forEach { _schemas.putHandled(it) }
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other !is Component) return false

        if(index != other.index) return false

        return true
    }

    override fun hashCode(): Int {
        return index.hashCode()
    }

    override fun toString(): String {
        return "Component(handle=$handle, data=$_data, schemas=$_schemas)"
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> getDataMapInternal(type: KClass<T>): HandleMap<EntityData<T>> =
        _data[type] as HandleMap<EntityData<T>>? ?: HandleMap<EntityData<T>>(dataSpace).apply {
            _data[type] = this as HandleMap<EntityData<*>>
        }

    private fun <T : Any> putInternal(type: KClass<T>, id: String, saved: Boolean, data: T) {
        val map = getDataMapInternal(type)
        if(id in map) map[id]!!.data = data
        else map[dataSpace.getOrCreateHandle(id)] = EntityData(id, data, saved).apply { observers.add(this@Component, identifier) }
    }

    companion object {
         private var indexCounter: Long = 0L

        fun schemaSpec(component: Component): PluggableMapSpec<Handle, Schema> = PluggableMapSpec(
            afterAdd = { _, v -> EntityCache.schemaAdded(component.entity, component, v) },
            afterRemove = { _, v -> EntityCache.schemaRemoved(component.entity, component, v) }
        )
    }
}
